package beit.skn.pingmeuser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

import beit.skn.classes.Message;

public class UserTalker 
{
	private static Socket socket = null;
	private static String uname = null;	
	private static ObjectOutputStream objOut = null;
	private static ObjectInputStream objIn = null;
	
	
	
	public static String getUname() 
	{
		return uname;
	}

	public static void setUname(String uname) 
	{
		UserTalker.uname = uname;
	}

	public static Socket getSocket() 
	{
		return socket;
	}

	public static void setSocket(Socket socket) 
	{
		UserTalker.socket = socket;
		
	}
	
	
	public static void pushMessage(Message m)
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
	
	public static Message readMessage()
	{
		Message m;
		try 
		{
			if(objIn==null)
				objIn = new ObjectInputStream(socket.getInputStream());
			m = (Message)objIn.readObject();
			return m;
		} 
		catch (StreamCorruptedException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}		
		return null;
	}
}
