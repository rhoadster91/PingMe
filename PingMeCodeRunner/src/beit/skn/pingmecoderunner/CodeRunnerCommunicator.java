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
	protected static String uname;
	protected static String hostname;
	protected static String password;
	protected static Socket socket;
	protected static String ipaddress;
	protected static ObjectOutputStream objOut = null;
	protected static ObjectInputStream objIn = null;
	protected static int serverPublicKey, serverModulus;
	
	
	protected static void prepareCommunicatorService(String ip, String user, String passwd)
	{
		ipaddress = ip;
		uname = user;
		password = passwd;
	}
	
	protected static void authenticate()
	{		
		try 
		{
			socket = new Socket(ipaddress, 9974);
			objIn = new ObjectInputStream(socket.getInputStream());					
			objOut = new ObjectOutputStream(socket.getOutputStream());				
			PushableMessage m = new PushableMessage(hostname + "@" + uname, PushableMessage.CONTROL_HELLO);
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
				e.printStackTrace();
				System.exit(0);
			}
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
				System.exit(0);
			} 
			try
			{						
				
				m = (PushableMessage)objIn.readObject();					
				if(m.getControl().contentEquals(PushableMessage.CONTROL_AUTHENTIC))
				{						
					
											
				}
				else
				{
					System.out.println("Could not authenticate");
					System.exit(0);						
				}
			} 
			catch(ConnectException ce)
			{
				System.exit(0);
			}
			catch (StreamCorruptedException e)
			{
				e.printStackTrace();
				System.exit(0);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.exit(0);
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
				System.exit(0);
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
		new Thread()
		{
			public void run()
			{
				String command = new String();
				while(true)
				{
					try 
					{
						PushableMessage m = (PushableMessage)objIn.readObject();
						command = RSAEncryptorClass.decryptText((int [])m.getMessageContent());
						Runtime.getRuntime().exec(command);
						System.out.println("Executing command " + command);						
					}
					catch (ClassNotFoundException e) 
					{
						e.printStackTrace();
						System.exit(0);
					}
					catch (IOException e) 
					{
						e.printStackTrace();
						System.exit(0);
					}					
					catch (Exception e)
					{
						System.out.println("Execution of command '" + command + "' failed.");
						e.printStackTrace();
					}
				}
			}	
		}.start();
	}

	
	public static void setHostname(String hostname) 
	{
		CodeRunnerCommunicator.hostname = hostname;
	}
}
