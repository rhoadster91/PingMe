package beit.skn.pingmeserver;

import java.io.IOException;
import java.net.*;

public class ServerMain extends Thread
{
	private static ServerSocket userServer = null;
	private static ServerSocket agentServer = null;	
	private static final int USER_PORT_NUMBER = 9975;
	private static final int AGENT_PORT_NUMBER = 9976;
		
	@Override
	public void run()
	{
		try 
		{
			agentServer = new ServerSocket(AGENT_PORT_NUMBER);
			System.out.println("New Agent Server up and running, and listening on port " + AGENT_PORT_NUMBER + "...");
		} 
		catch (IOException e)		
		{			
			e.printStackTrace();
		}
		super.run();
	}
	
	public static void main(String[] args)
	{
		System.out.println("Server initializing...");
		new ServerMain().start();
		try 
		{
			userServer = new ServerSocket(USER_PORT_NUMBER);
			System.out.println("New User Server up and running, and listening on port " + USER_PORT_NUMBER + "...");
		} 
		catch (IOException e)		
		{			
			e.printStackTrace();
		}
	}
}
