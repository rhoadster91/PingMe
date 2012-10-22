package beit.skn.pingmeagent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

import beit.skn.classes.PushableMessage;

public class AgentTalker 
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
		AgentTalker.uname = uname;
	}

	public static Socket getSocket() 
	{
		return socket;
	}

	public static void setSocket(Socket socket) 
	{
		AgentTalker.socket = socket;
		try 
		{
			if(objOut!=null)
				objOut.close();
			if(objIn!=null)				
				objIn.close();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		objOut = null;
		objIn = null;
	}
	
	
	public static void pushMessage(PushableMessage m)
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
	
	public static PushableMessage readMessage() throws StreamCorruptedException, IOException, ClassNotFoundException
	{
		PushableMessage m;
		if(objIn==null)
			objIn = new ObjectInputStream(socket.getInputStream());
		m = (PushableMessage)objIn.readObject();
		return m;		
		
	}
}
