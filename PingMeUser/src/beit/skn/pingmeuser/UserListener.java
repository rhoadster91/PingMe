package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;

public class UserListener extends Thread
{
	private DashboardActivity dAct = null;
	
	public UserListener(DashboardActivity d)
	{
		dAct = d;
	}

	@Override
	public void run()
	{
		while(true)
		{
			PushableMessage m = UserTalker.readMessage();
			dAct.listenToStream(m);
		}
	}
}
