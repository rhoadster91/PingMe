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
			System.out.println("Agent " + agentID + " registered to server with public key pair (" + agentPublicKey + ", " + agentModulus + "). Waiting for user request.");
			m = new PushableMessage("server", PushableMessage.CONTROL_HELLO);
			m.setMessageContent(new String(RSAEncryptorClass.getPublicKey() + "," + RSAEncryptorClass.getModulus()));
			pushMessage(m);			
			m = (PushableMessage)objIn.readObject();
			agentPassword = RSAEncryptorClass.decryptText((int [])m.getMessageContent()).trim();
			if(m.isEncrypted())
				agentPassword = EncryptionStub.decrypt(agentPassword);
			if(DBConnect.isAuthentic(agentID, agentPassword, "agents"))
			{
				m = new PushableMessage("server", PushableMessage.CONTROL_AUTHENTIC);
				m.setMessageContent(new String(RSAEncryptorClass.getPublicKey() + "," + RSAEncryptorClass.getModulus()));
				
				pushMessage(m);			
				while(true)
				{				
					m = (PushableMessage)objIn.readObject();
					System.out.println("Received packet");
					if(m.getControl().contentEquals(PushableMessage.CONTROL_PUSH))
					{
						ServerMain.pushMessageToClient(m, m.getDestination(), "agent");
						System.out.println("Call for " + ((String)m.getMessageContent()).split("&&&")[0] + " from lat " + ((String)m.getMessageContent()).split("&&&")[1] + " long " + ((String)m.getMessageContent()).split("&&&")[2]);
					}
					else if(m.getControl().contentEquals(PushableMessage.CONTROL_PING_TEXT))
						ServerMain.pushMessageToClient(m, m.getDestination(), "user");
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
				System.out.println("Agent" + agentID + " failed to authenticate. Deleting entry.");
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
}
