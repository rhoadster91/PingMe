package beit.skn.pingmeserver;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import beit.skn.classes.PushableMessage;

public class UserHelper extends Thread 
{
	protected Socket socket = null;
	private String userID = null;
	private ObjectOutputStream objOut = null;
	private ObjectInputStream objIn = null;
	
	
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
		PushableMessage m = null;
		String ctrl = null;
		try 
		{
			if(objIn==null)
				objIn = new ObjectInputStream(socket.getInputStream());			
			m = (PushableMessage)objIn.readObject();
			System.out.println("New user connected. Waiting for ID.");
			ctrl = m.getControl();
			if(ctrl.contentEquals("hello"))				
				userID = m.getSender();		
			else if(m.getControl().contentEquals("logout"))
			{
				System.out.println("User " + userID + " requested log out. Deleting entry.");
				ServerMain.deleteEntry(userID, "user");	
				return;
			}
			System.out.println("User " + userID + " registered to server. Standby for user request.");
			m = new PushableMessage("server", "authentic");
			pushMessage(m);			
			while(true)
			{				
				m = (PushableMessage)objIn.readObject();
				System.out.println("Received packet");
				if(m.getControl().contentEquals("push"))
					ServerMain.pushMessageToClient(m, m.getDestination(), "agent");
			}
			
		} 
		catch(SocketException se)
		{
			System.out.println("User " + userID + " disconnected from server. Deleting entry.");			
		}
		catch(EOFException e)
		{
			
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
