package beit.skn.pingmeagent;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import beit.skn.classes.PushableMessage;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class AgentCommunicatorService extends Service
{
	private static final String IP_ADDRESS = "192.168.0.101";
	private static final int AGENT_PORT_NUMBER = 9976;	
	public static final String INTENT_TO_SERVICE = "PingMeIntentToService";
	public static final String INTENT_TO_ACTIVITY = "PingMeIntentToActivity";
	
	private static Socket socket = null;
	private static String uname = null;	
	private static BroadcastReceiver brSendRequested = null;
	private static IntentFilter ifSendRequested = null;
	
	public static String getUname() 
	{
		return uname;
	}

	public static void setUname(String uname) 
	{
		AgentCommunicatorService.uname = uname;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		return START_STICKY;
	}

	@Override
	public void onCreate() 
	{
		PushableMessage m = new PushableMessage();
		try 
		{
			socket = new Socket(IP_ADDRESS, AGENT_PORT_NUMBER);
			AgentTalker.setSocket(socket);
			AgentTalker.pushMessage(m);
			m = AgentTalker.readMessage();
			if(m.getControl().contentEquals("authentic"))
			{
				Toast.makeText(getBaseContext(), "Authenticated and registered on server", Toast.LENGTH_LONG).show();				
			}
			else
			{
				Toast.makeText(getBaseContext(), "Could not authenticate.", Toast.LENGTH_LONG).show();
				stopSelf();
			}
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		brSendRequested = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context arg0, Intent arg1) 
			{
				PushableMessage m = (PushableMessage)arg1.getSerializableExtra("pushablemessage");
				AgentTalker.pushMessage(m);
			}			
		};
		ifSendRequested = new IntentFilter();
		ifSendRequested.addAction(INTENT_TO_SERVICE);
		registerReceiver(brSendRequested, ifSendRequested);		
		showPersistentNotification();
		readIncomingMessages();
		super.onCreate();
	}
	
	public void readIncomingMessages()
	{
		PushableMessage m;
		while(true)
		{
			m = AgentTalker.readMessage();
			Intent iReadRequested = new Intent();
			iReadRequested.setAction(INTENT_TO_ACTIVITY);
			iReadRequested.putExtra("pushablemessage", m);
			sendOrderedBroadcast(iReadRequested, null, new BroadcastReceiver()
			{
				@Override
				public void onReceive(Context context, Intent intent)
				{
					int result = getResultCode();
					switch(result)
					{
					case Activity.RESULT_OK:						
						break;
						
					default:
						showTempNotification();
						break;
					}
				}				
			}, null, Activity.RESULT_CANCELED, null, null);
		}
	}
	
	@Override
	public void onDestroy() 
	{
		unregisterReceiver(brSendRequested);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}
	
	private void showPersistentNotification()
	{
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        CharSequence text = getText(R.string.servicename);
        Notification notification = new Notification(R.drawable.icon, text, 0);
        Intent showActivity = new Intent(this, DashboardActivity.class);
        showActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);    
        showActivity.setAction(INTENT_TO_ACTIVITY);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showActivity, 0);
        notification.setLatestEventInfo(this, getText(R.string.servicetext), text, contentIntent);
        notification.flags = Notification.FLAG_NO_CLEAR ^ Notification.FLAG_ONGOING_EVENT;        
        nm.notify(R.string.servicename, notification);
	}
	
	private void showTempNotification()
	{
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        CharSequence text = getText(R.string.servicename);
        Notification notification = new Notification(R.drawable.icon, text, 0);
        Intent showActivity = new Intent(this, DashboardActivity.class);
        showActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);    
        showActivity.setAction(INTENT_TO_ACTIVITY);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showActivity, 0);
        notification.setLatestEventInfo(this, getText(R.string.notificationtext), text, contentIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.string.servicename, notification);
	}
}
