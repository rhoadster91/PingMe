package beit.skn.pingmeserver;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import beit.skn.classes.PushableMessage;
import beit.skn.classes.RSAEncryptorClass;

public class AgentHelper extends Thread 
{
	protected Socket socket = null;
	private String agentID = "";
	private String agentPassword = "";
	private ObjectOutputStream objOut = null;
	private ObjectInputStream objIn = null;
	private int agentPublicKey, agentModulus;
	private String agentClass = null;
	private boolean isBusy = false;
	private double latitude, longitude;
	
	public String getAgentID()
	{
		return agentID;
	}

	public AgentHelper(Socket s)
	{
		socket = s;		
		try 
		{
			objOut = new ObjectOutputStream(socket.getOutputStream());
			objIn = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		PushableMessage m = null;
		String ctrl = null;
		try 
		{
			m = (PushableMessage)objIn.readObject();
			System.out.println("New agent connected. Waiting for ID.");
			ctrl = m.getControl();
			if(ctrl.contentEquals(PushableMessage.CONTROL_HELLO))				
				agentID = m.getSender();		
			String pair = (String)m.getMessageContent(); 
			agentPublicKey = Integer.parseInt(pair.split(",")[0]);
			agentModulus = Integer.parseInt(pair.split(",")[1]);			
			System.out.println("Agent " + agentID + " attempting to authenticate with public key pair (" + agentPublicKey + ", " + agentModulus + "). Waiting for user request.");
			m = new PushableMessage("server", PushableMessage.CONTROL_HELLO);
			m.setMessageContent(new String(RSAEncryptorClass.getPublicKey() + "," + RSAEncryptorClass.getModulus()));
			pushMessage(m);			
			m = (PushableMessage)objIn.readObject();
			agentPassword = RSAEncryptorClass.decryptText((int [])m.getMessageContent()).trim();
			if(m.isEncrypted())
				agentPassword = EncryptionStub.decrypt(agentPassword);
			if(DBConnect.isAuthentic(agentID, agentPassword, "agents"))
			{
				agentClass = DBConnect.getAgentClassFromDatabase(agentID);
				System.out.println("Agent " + agentID + " of class " + agentClass + " authenticated. Standind by for user requests.");				
				m = new PushableMessage("server", PushableMessage.CONTROL_AUTHENTIC);
				agentPassword = EncryptionStub.encrypt(agentPassword);
				m.setMessageContent(RSAEncryptorClass.encryptText(agentPassword, agentModulus, agentPublicKey));				
				pushMessage(m);			
				while(true)
				{				
					m = (PushableMessage)objIn.readObject();
					System.out.println("Received packet");
					if(m.getControl().contentEquals(PushableMessage.CONTROL_PUSH))
					{
						int serviceID = Integer.parseInt(m.getDestination());
						if(!ServerMain.isServiced(serviceID))
						{
							ServerMain.setServiced(serviceID, true);							
							m.setDestination(ServerMain.getServiceUser(serviceID));
							m.setMessageContent(new String("Your rickshaw in on its way to your location"));
							ServerMain.pushMessageToClient(m, m.getDestination(), "user");
						}
						else
						{
							PushableMessage msg = new PushableMessage("server", PushableMessage.CONTROL_ABORT);
							pushMessage(msg);							
						}
						
					}
					else if(m.getControl().contentEquals(PushableMessage.CONTROL_ABORT))
					{
						isBusy = false;
						PushableMessage msg = new PushableMessage("online", PushableMessage.CONTROL_OK);
						pushMessage(msg);
					}
					else if(m.getControl().contentEquals(PushableMessage.CONTROL_OK))
					{
						isBusy = true;
						PushableMessage msg = new PushableMessage("busy", PushableMessage.CONTROL_OK);
						pushMessage(msg);
					}					
					else if(m.getControl().contentEquals(PushableMessage.CONTROL_UPDATE_LOCATION))
					{
						latitude = Double.parseDouble(((String)m.getMessageContent()).split("&&&")[0]);
						longitude = Double.parseDouble(((String)m.getMessageContent()).split("&&&")[1]);
						System.out.println("UPDATE: Agent " + agentID + " has moved to location " + latitude + ", " + longitude);
					}
					else if(m.getControl().contentEquals(PushableMessage.CONTROL_LOGOUT))
					{
						System.out.println("Agent " + agentID + " requested log out. Deleting entry.");
						ServerMain.deleteEntry(agentID, "agent");	
						return;
					}
				}
			}
			else
			{
				System.out.println("Agent " + agentID + " failed to authenticate. Deleting entry.");
				m = new PushableMessage("server", PushableMessage.CONTROL_ABORT);
				pushMessage(m);
				ServerMain.deleteEntry(agentID, "agent");	
				return;
			}
		} 
		catch(SocketException se)
		{
			se.printStackTrace();
			System.out.println("Agent " + agentID + " disconnected from server. Deleting entry.");
			ServerMain.deleteEntry(agentID, "agent");			
		}
		catch(EOFException eofe)
		{
			//System.out.println("Couldn't connect to Agent " + agentID + ". Deleting entry.");
			//ServerMain.deleteEntry(agentID, "agent");
			eofe.printStackTrace();
		}
		catch (IOException e) 
		{			
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) 
		{			
			e.printStackTrace();
		}			
	}
	
	public void pushMessage(PushableMessage m)
	{
		try 
		{
			if(objOut==null)
				objOut = new ObjectOutputStream(socket.getOutputStream());
			objOut.writeObject(m);
			objOut.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}

	public String getAgentClass() 
	{
		return agentClass;
	}

	public boolean isBusy() 
	{
		return isBusy;
	}

	public void setBusy(boolean isBusy) 
	{
		this.isBusy = isBusy;
	}

	public double getLatitude() 
	{
		return latitude;
	}

	
	public double getLongitude() 
	{
		return longitude;
	}

	
}
