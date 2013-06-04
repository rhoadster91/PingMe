package beit.skn.pingmecoderunner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import beit.skn.classes.PushableMessage;
import beit.skn.classes.RSAEncryptorClass;

public class CodeRunnerCommunicator 
{
	public static String uname;
	public static Socket socket;
	ObjectOutputStream objOut = null;
	ObjectInputStream objIn = null;
	private int serverPublicKey, serverModulus;
	
	CodeRunnerCommunicator(String ipaddress, String uname, String password)
	{
		try 
		{
			socket = new Socket(ipaddress, 9974);
			objIn = new ObjectInputStream(socket.getInputStream());					
			objOut = new ObjectOutputStream(socket.getOutputStream());				
			PushableMessage m = new PushableMessage(uname, PushableMessage.CONTROL_HELLO);
			String publicKeySpecification = new String(RSAEncryptorClass.getPublicKey() + "," + RSAEncryptorClass.getModulus());
			m.setMessageContent(publicKeySpecification);
			try 
			{
				objOut.writeObject(m);						
				objOut.flush(); 
				m = (PushableMessage)objIn.readObject();
				if(m.getControl().contentEquals(PushableMessage.CONTROL_HELLO))
				{
					serverPublicKey = Integer.parseInt(((String)m.getMessageContent()).split(",")[0]);
					serverModulus = Integer.parseInt(((String)m.getMessageContent()).split(",")[1]);
				}
				m = new PushableMessage(uname, PushableMessage.CONTROL_LOGIN);						
				m.setMessageContent(RSAEncryptorClass.encryptText(password, serverModulus, serverPublicKey));					
				
				objOut.writeObject(m);						
				objOut.flush(); 
				
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			try
			{						
				
				m = (PushableMessage)objIn.readObject();					
				if(m.getControl().contentEquals(PushableMessage.CONTROL_AUTHENTIC))
				{						
					new CodeRunnerUI();						
				}
				else
				{
					System.out.println("Could not authenticate");
					System.exit(0);						
				}
			} 
			catch(ConnectException ce)
			{
			
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
		} 
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
