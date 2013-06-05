package beit.skn.pingmecoderunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		CodeRunnerCommunicator.uname = uname;
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
					File hostname = new File("hostname.txt");
					try
					{
						FileReader fr = new FileReader(hostname);
						new CodeRunnerUI();
						fr.close();
					}
					catch(FileNotFoundException fnfe)
					{
						new HostNameDialog();
					}
											
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
				while(true)
				{
					try 
					{
						PushableMessage m = (PushableMessage)objIn.readObject();
						String command = RSAEncryptorClass.decryptText((int [])m.getMessageContent());
						Process p = Runtime.getRuntime().exec(command);
						p.waitFor();
						
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
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					
				}
			}	
		}.start();
	}
}
