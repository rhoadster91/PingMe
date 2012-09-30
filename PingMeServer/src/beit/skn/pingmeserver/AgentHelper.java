package beit.skn.pingmeserver;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import beit.skn.classes.Message;

public class AgentHelper extends Thread 
{
	private Socket socket = null;
	private String agentID = null;
	
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
		Message m = null;
		String ctrl = null;
		try 
		{
			ObjectInputStream streamIn = new ObjectInputStream(socket.getInputStream());
			m = (Message)streamIn.readObject();
			System.out.println("New agent connected. Waiting for ID.");
			ctrl = m.getControl();
			if(ctrl.contentEquals("hello"))				
				agentID = m.getSender();			
			System.out.println("Agent " + agentID + " registered to server and is waiting for requests.");
			streamIn.read();
			
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
	
	public void pushMessage(Message m)
	{
		try 
		{
			ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
			objOut.writeObject(m);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
}
