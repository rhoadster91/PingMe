package beit.skn.pingmeagent;

import java.io.IOException;
import java.io.StreamCorruptedException;
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
	private Socket socket = null;
	private static BroadcastReceiver brSendRequested = null;
	private static IntentFilter ifSendRequested = null;
	private static String errorMessage = null;
	private static final int MAX_ATTEMPTS = 10;
	
	private Thread incomingMessageReader = new Thread()
	{
		
		@Override
		public void run() 
		{
			PushableMessage m;
			while(true)
			{
				try 
				{
					m = AgentTalker.readMessage();
					Intent iReadRequested = new Intent();
					iReadRequested.setAction(AgentApplication.INTENT_TO_ACTIVITY);
					iReadRequested.putExtra("pushablemessage", m);
					AgentApplication.splashBox.add(m);
					AgentApplication.writeSplashBoxToFile(getApplicationContext());
					sendOrderedBroadcast(iReadRequested, null, new BroadcastReceiver()
					{
						@Override
						public void onReceive(Context context, Intent intent)
						{
							int result = getResultCode();
							switch(result)
							{
							case Activity.RESULT_OK:	
								AgentApplication.notifCount = 0;
								break;
								
							default:
								showTempNotification((PushableMessage)intent.getSerializableExtra("pushablemessage"));
								break;
							}
						}				
					}, null, Activity.RESULT_CANCELED, null, null);
				} 
				catch (StreamCorruptedException e)
				{
					errorMessage = "Stream corrupted. Sorry for the inconvenience.";						
					stopSelf();
					e.printStackTrace();
					break;
				} 
				catch (IOException e) 
				{
					errorMessage = "Server shut down. Sorry for the inconvenience. Please try again later.";					
					stopSelf();
					e.printStackTrace();
					break;
				} 
				catch (ClassNotFoundException e) 
				{
					errorMessage = "Version mismatch. Please update your app.";
					stopSelf();
					e.printStackTrace();
					break;
				}				
			}
		}		
	};	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		return START_STICKY;
	}

	@Override
	public void onCreate() 
	{
		socket = null;
		int attempts = 0;
		AgentApplication.errorMessage = null;
		errorMessage = null;
		if(AgentApplication.isAuthentic==false)
		{
			try 
			{			
				socket = new Socket(AgentApplication.IP_ADDRESS, AgentApplication.AGENT_PORT_NUMBER);
			} 
			catch (UnknownHostException uhe) 
			{
				attempts++;
				if(attempts==MAX_ATTEMPTS)
					stopSelf();
				try 
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				uhe.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			socket = AgentTalker.getSocket();
		}
				
		
		AgentTalker.setSocket(socket);
		brSendRequested = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context arg0, Intent arg1) 
			{
				PushableMessage m = (PushableMessage)arg1.getSerializableExtra("pushablemessage");
				if(m.getControl().contentEquals(PushableMessage.CONTROL_HELLO) && !AgentApplication.isAuthentic)
				{				
					AgentTalker.pushMessage(m);
					try
					{
						m = AgentTalker.readMessage();
						if(m.getControl().contentEquals(PushableMessage.CONTROL_AUTHENTIC))
						{
							Toast.makeText(getApplicationContext(), "Authenticated and registered on server", Toast.LENGTH_LONG).show();
							Intent iIsAuthentic = new Intent();
							iIsAuthentic.setAction(AgentApplication.INTENT_TO_ACTIVITY);
							sendBroadcast(iIsAuthentic);
							showPersistentNotification();	
							AgentApplication.isAuthentic = true;
							AgentApplication.uname = m.getDestination();
							incomingMessageReader.start();						
						}
						else
						{
							Toast.makeText(getApplicationContext(), "Could not authenticate.", Toast.LENGTH_LONG).show();
							stopSelf();
						}
					} 
					catch (StreamCorruptedException e)
					{
						errorMessage = "Stream corrupted. Sorry for the inconvenience.";		
						e.printStackTrace();
						stopSelf();
						
					} 
					catch (IOException e) 
					{
						errorMessage = "Server shut down. Sorry for the inconvenience. Please try again later.";					
						e.printStackTrace();
						stopSelf();
					} 
					catch (ClassNotFoundException e) 
					{
						errorMessage = "Version mismatch. Please update your app.";						
						e.printStackTrace();
						stopSelf();
					}										
				}
				else if(m.getControl().contentEquals(PushableMessage.CONTROL_HELLO) && AgentApplication.isAuthentic)
				{
					Intent iIsAuthentic = new Intent();
					iIsAuthentic.setAction(AgentApplication.INTENT_TO_ACTIVITY);
					sendBroadcast(iIsAuthentic);
				}
				else
					AgentTalker.pushMessage(m);
			}			
		};
		ifSendRequested = new IntentFilter();
		ifSendRequested.addAction(AgentApplication.INTENT_TO_SERVICE);
		registerReceiver(brSendRequested, ifSendRequested);	
		super.onCreate();
	}
	
	
	
	@Override
	public void onDestroy() 
	{		
		
		try
		{
			unregisterReceiver(brSendRequested);			
		}
		catch(IllegalArgumentException iae)
		{
			// Do nothing
		} 
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    nm.cancel(R.string.servicetext);
	    nm.cancel(R.string.notificationtext);
	    if(errorMessage!=null)
	    {	
		    AgentApplication.isAuthentic = false;	    
		    AgentApplication.errorMessage = errorMessage;
		    DashboardActivity.onErrorOccured(getApplicationContext());
		    if(socket!=null)
		    {
			    PushableMessage m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_LOGOUT);
			    AgentTalker.pushMessage(m);			   
		    }
	    }
		//android.os.Process.killProcess(android.os.Process.myPid());	    
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
        CharSequence text = getText(R.string.servicetext);
        Notification notification = new Notification(R.drawable.icon, text, 0);
        Intent showActivity = new Intent(this, DashboardActivity.class);
        showActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);   
        showActivity.setAction(AgentApplication.INTENT_TO_ACTIVITY);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showActivity, 0);
        notification.setLatestEventInfo(this, getText(R.string.servicename), text, contentIntent);
        notification.flags = Notification.FLAG_NO_CLEAR ^ Notification.FLAG_ONGOING_EVENT;        
        nm.notify(R.string.servicetext, notification);
	}
	
	private void showTempNotification(PushableMessage m)
	{
		AgentApplication.notifCount++;		
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String text = String.format(getString(R.string.notificationtext), AgentApplication.notifCount);
        Notification notification = new Notification(R.drawable.icon, text, 0);
        Intent showActivity = new Intent(this, DashboardActivity.class);
        showActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        showActivity.putExtra("pushablemessage", m);
        showActivity.setAction(AgentApplication.INTENT_TO_ACTIVITY);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showActivity, 0);        
        notification.setLatestEventInfo(this, getText(R.string.servicename), text, contentIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;
        nm.notify(R.string.notificationtext, notification);
	}
	

	
}
