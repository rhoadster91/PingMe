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
			System.out.println("New user connected. Waiting for ID.");
			ctrl = m.getControl();
			if(ctrl.contentEquals(PushableMessage.CONTROL_HELLO))				
				userID = m.getSender();					
			System.out.println("User " + userID + " registered to server. Standby for user request.");
			m = new PushableMessage("server", PushableMessage.CONTROL_AUTHENTIC);
			pushMessage(m);			
			while(true)
			{				
				m = (PushableMessage)objIn.readObject();
				System.out.println("Received packet");
				if(m.getControl().contentEquals(PushableMessage.CONTROL_PUSH))
					ServerMain.pushMessageToClient(m, m.getDestination(), "agent");
				else if(m.getControl().contentEquals(PushableMessage.CONTROL_PING_TEXT))
					ServerMain.pushMessageToClient(m, m.getDestination(), "user");
				else if(m.getControl().contentEquals(PushableMessage.CONTROL_LOGOUT))
				{
					System.out.println("User " + userID + " requested log out. Deleting entry.");
					ServerMain.deleteEntry(userID, "user");	
					return;
				}
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
