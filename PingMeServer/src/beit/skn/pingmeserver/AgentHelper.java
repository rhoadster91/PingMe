package beit.skn.pingmeserver;

import java.io.*;
import java.net.Socket;

import beit.skn.classes.Message;

public class AgentHelper extends Thread 
{
	private Socket socket = null;
	private ServerMain serverMain;
	private String agentID = null;
	
	public AgentHelper(Socket s, ServerMain sm)
	{
		socket = s;
		serverMain = sm;		
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
			System.out.println("Agent " + agentID + " connected to server and is waiting for orders.");
		} 
		catch (IOException e) 
		{			
			e.printStackTrace();
		} catch (ClassNotFoundException e) 
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
