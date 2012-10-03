package beit.skn.pingmeserver;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import beit.skn.classes.PushableMessage;

public class AgentHelper extends Thread 
{
	private Socket socket = null;
	private String agentID = null;
	private ObjectOutputStream objOut = null;
	private ObjectInputStream objIn = null;
	
	public String getAgentID()
	{
		return agentID;
	}

	public AgentHelper(Socket s)
	{
		socket = s;		
	}
	
	@Override
	public void run()
	{
		PushableMessage m = null;
		String ctrl = null;
		try 
		{
			objIn = new ObjectInputStream(socket.getInputStream());
			m = (PushableMessage)objIn.readObject();
			System.out.println("New agent connected. Waiting for ID.");
			ctrl = m.getControl();
			if(ctrl.contentEquals("hello"))				
				agentID = m.getSender();			
			System.out.println("Agent " + agentID + " registered to server and is waiting for requests.");
			m = new PushableMessage("server", "authentic");
			pushMessage(m);
			while(true)
			{				
				m = (PushableMessage)objIn.readObject();
				if(m.getControl().contentEquals("push"))
					ServerMain.pushMessageToClient(m, m.getDestination(), "user");
			}			
		} 
		catch(SocketException se)
		{
			System.out.println("Agent " + agentID + " disconnected from server. Deleting entry.");
			ServerMain.deleteEntry(agentID, "agent");
			
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
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}
}
