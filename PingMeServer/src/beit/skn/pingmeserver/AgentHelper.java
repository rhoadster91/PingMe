package beit.skn.pingmeserver;

import java.net.Socket;

public class AgentHelper extends Thread 
{
	private Socket socket = null;
	private ServerMain serverMain;
	private String agentID = null;
	
	public AgentHelper(Socket s, ServerMain sm)
	{
		socket = s;
		serverMain = sm;		
	}
	
	@Override
	public void run()
	{
		
	}
}
