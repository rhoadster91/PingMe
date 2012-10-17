package beit.skn.pingmeagent;

import android.app.Application;

public class AgentApplication extends Application
{
	protected static int notifCount = 0;
	protected static String errorMessage;
	protected static final String INTENT_TO_SERVICE = "PingMeIntentToService";
	protected static final String INTENT_TO_ACTIVITY = "PingMeIntentToActivity";
	protected static final String NOTIFICATION_CALL = "PingMeNotificationCall";
	protected static final int AGENT_PORT_NUMBER = 9976;
	protected static final String IP_ADDRESS = "192.168.0.101";
}
