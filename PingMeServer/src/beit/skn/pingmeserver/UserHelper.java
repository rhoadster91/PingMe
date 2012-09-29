package beit.skn.pingmeserver;

import java.net.*;

public class UserHelper extends Thread 
{
	private Socket socket = null;
	private ServerMain serverMain = null;
	private String userID = null;
	
	public UserHelper(Socket s, ServerMain sm)
	{
		socket = s;
		serverMain = sm;		
	}
	
	@Override
	public void run()
	{
		
	}
}
