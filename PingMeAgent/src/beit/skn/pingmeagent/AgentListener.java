package beit.skn.pingmeagent;

import beit.skn.classes.PushableMessage;

public class AgentListener extends Thread
{
	private DashboardActivity dAct = null;
	
	public AgentListener(DashboardActivity d)
	{
		dAct = d;
	}

	@Override
	public void run()
	{
		while(true)
		{
			PushableMessage m = AgentTalker.readMessage();
			dAct.listenToStream(m);
		}
	}
}
