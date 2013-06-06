package beit.skn.pingmeserver;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

import beit.skn.classes.PushableMessage;
import beit.skn.classes.RSAEncryptorClass;

public class ServerMain extends Thread
{
	private static ServerSocket userServer = null;
	private static ServerSocket agentServer = null;
	private static ServerSocket coderunnerServer = null;
	private static final int CODERUNNER_PORT_NUMBER = 9974;
	private static final int USER_PORT_NUMBER = 9975;
	private static final int AGENT_PORT_NUMBER = 9976;
	private static ArrayList<UserHelper> userHelpers = null;
	private static ArrayList<AgentHelper> agentHelpers = null;
	private static ArrayList<CodeRunnerHelper> coderunnerHelpers = null;
	private String type = null;
	private static ArrayList<Boolean> serviceRequests = null;
	private static ArrayList<String> serviceRequestUsers = null;
	
	public ServerMain(String param)
	{
		type = param;
	}
	
	public void run()
	{
		if(type.contentEquals("agent"))
			this.runAgentThread();
		else if(type.contentEquals("user"))
			this.runUserThread();		
		else
			this.runCodeRunnerThread();
		super.run();
	}
	
	public void runCodeRunnerThread()
	{
		CodeRunnerHelper tempCodeRunner = null;		
		try 
		{
			Socket s = null;
			coderunnerServer = new ServerSocket(CODERUNNER_PORT_NUMBER);
			System.out.println("New Code Runner Server up and running, and listening on port " + CODERUNNER_PORT_NUMBER + "...");
			while(true)
			{
				s = coderunnerServer.accept();
				s.setKeepAlive(true);				
				tempCodeRunner = new CodeRunnerHelper(s);
				tempCodeRunner.start();				
				coderunnerHelpers.add(tempCodeRunner);
			}
		} 
		catch (IOException e)		
		{			
			e.printStackTrace();
		}
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
				s.setKeepAlive(true);				
				tempUser = new UserHelper(s);
				tempUser.start();				
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
				s.setKeepAlive(true);
				tempAgent = new AgentHelper(s);
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
		System.out.println("Server Public Key Pair: (" + RSAEncryptorClass.getPublicKey() + ", " + RSAEncryptorClass.getModulus() + ")");
		System.out.print("Initializing database...");
		DBConnect.initializeDatabase();
		System.out.println("OK!");		
		new ServerMain("agent").start();
		new ServerMain("user").start();
		new ServerMain("coderunner").start();
		userHelpers = new ArrayList<UserHelper>();
		agentHelpers = new ArrayList<AgentHelper>();
		coderunnerHelpers = new ArrayList<CodeRunnerHelper>();
		serviceRequests = new ArrayList<Boolean>();
		serviceRequestUsers = new ArrayList<String>();
		System.out.println("Done.");
	}
	
	public static void deleteEntry(String id, String clientClass)
	{
		if(clientClass.contentEquals("agent"))
		{
			Iterator<AgentHelper> agentIterator = null;
			AgentHelper temp;
			agentIterator = agentHelpers.iterator();
			while(agentIterator.hasNext())
			{
				temp = agentIterator.next();
				if(temp.getAgentID().contentEquals(id) && id!=null)
				{
					temp.pushMessage(new PushableMessage(null, PushableMessage.CONTROL_LOGOUT));
					agentIterator.remove();					
					System.out.println("Agent " + id + " unregistered.");
					break;
				}
			}
		}
		else
		{
			Iterator<UserHelper> userIterator = null;
			UserHelper temp;
			userIterator = userHelpers.iterator();
			while(userIterator.hasNext())
			{
				temp = userIterator.next();
				if(temp.getUserID().contentEquals(id))
				{
					temp.pushMessage(new PushableMessage(null, PushableMessage.CONTROL_LOGOUT));					
					userIterator.remove();
					System.out.println("User " + id + " unregistered.");
					break;
				}
			}
		}
	}
	
	
	public static void pushMessageToClient(PushableMessage m, String id, String clientClass)
	{
		if(clientClass.contentEquals("agent"))
		{
			Iterator<AgentHelper> agentIterator = null;
			AgentHelper temp;
			agentIterator = agentHelpers.iterator();
			while(agentIterator.hasNext())
			{
				temp = agentIterator.next();
				if(temp.getAgentID().contentEquals(id))
				{
					temp.pushMessage(m);
					break;
				}
			}			
		}
		else
		{
			Iterator<UserHelper> userIterator = null;
			UserHelper temp;
			userIterator = userHelpers.iterator();
			while(userIterator.hasNext())
			{
				temp = userIterator.next();
				if(temp.getUserID().contentEquals(id))
				{
					temp.pushMessage(m);
					break;	
				}
			}
		}
	}
	
	public static void multicastToAgents(PushableMessage m, String agentClass)
	{
		int serviceID = serviceRequests.size();
		double lat = Double.parseDouble((((String)m.getMessageContent()).split("&&&")[1]));
		double lon = Double.parseDouble((((String)m.getMessageContent()).split("&&&")[2]));
		serviceRequests.add(new Boolean(false));
		serviceRequestUsers.add(m.getSender());
		m.setSender(""+serviceID);
		Iterator<AgentHelper> agentIterator = null;
		AgentHelper temp;
		agentIterator = agentHelpers.iterator();
		while(agentIterator.hasNext())
		{
			temp = agentIterator.next();
			if(temp.getAgentClass().contentEquals(agentClass) && !temp.isBusy() && !(LocationMath.distance(lat, lon, temp.getLatitude(), temp.getLongitude()) > 500))
			{
				temp.pushMessage(m);
				temp.setBusy(true);
				System.out.println("Call distance: " + LocationMath.distance(lat, lon, temp.getLatitude(), temp.getLongitude()) + "Call message forwarded to " + temp.getAgentID());
			}
		}
	}
	
	public static boolean isServiced(int id)
	{
		return serviceRequests.get(id).booleanValue();		
	}
	
	public static void setServiced(int id, boolean value)
	{
		serviceRequests.set(id, new Boolean(value));		
	}
	
	public static String getServiceUser(int id)
	{
		return serviceRequestUsers.get(id);		
	}
}
