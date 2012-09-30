package beit.skn.pingmeserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import beit.skn.classes.Message;

public class UserHelper extends Thread 
{
	private Socket socket = null;
	private String userID = null;
	
	public String getUserID() 
	{
		return userID;
	}

	public UserHelper(Socket s)
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
			System.out.println("New user connected. Waiting for ID.");
			ctrl = m.getControl();
			if(ctrl.contentEquals("hello"))				
				userID = m.getSender();			
			System.out.println("User " + userID + " registered to server. Standby for user request.");
			while(true)
			{
				m = (Message)streamIn.readObject();
				ServerMain.pushMessageToClient(m, m.getControl(), "agent");
			}
			
		} 
		catch(SocketException se)
		{
			System.out.println("User " + userID + " disconnected from server. Deleting entry.");
			
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
