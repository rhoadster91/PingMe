package beit.skn.pingmeserver;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ServerMain extends Thread
{
	private static ServerSocket userServer = null;
	private static ServerSocket agentServer = null;	
	private static final int USER_PORT_NUMBER = 9975;
	private static final int AGENT_PORT_NUMBER = 9976;
	private static ArrayList<UserHelper> userHelpers = null;
	private static ArrayList<AgentHelper> agentHelpers = null;
	private String type = null;
	
	public ServerMain(String param)
	{
		type = param;
	}
	
	public void run()
	{
		if(type.contentEquals("agent"))
			this.runAgentThread();
		else
			this.runUserThread();		
		super.run();
	}
	
	public void runUserThread()
	{
		UserHelper tempUser = null;		
		try 
		{
			Socket s = null;
			userServer = new ServerSocket(USER_PORT_NUMBER);
			System.out.println("New User Server up and running, and listening on port " + USER_PORT_NUMBER + "...");
			while(true)
			{
				s = userServer.accept();
				tempUser = new UserHelper(s, this);
				userHelpers.add(tempUser);
			}
		} 
		catch (IOException e)		
		{			
			e.printStackTrace();
		}
	}
	
	public void runAgentThread()
	{
		AgentHelper tempAgent = null;		
		try 
		{
			Socket s = null;
			agentServer = new ServerSocket(AGENT_PORT_NUMBER);
			System.out.println("New Agent Server up and running, and listening on port " + AGENT_PORT_NUMBER + "...");
			while(true)
			{
				s = agentServer.accept();
				tempAgent = new AgentHelper(s, this);
				tempAgent.start();
				agentHelpers.add(tempAgent);				
			}
		} 
		catch (IOException e)		
		{			
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("Server initializing...");
		new ServerMain("agent").start();
		new ServerMain("user").start();		
		userHelpers = new ArrayList<UserHelper>();
		agentHelpers = new ArrayList<AgentHelper>();		
	}
}
