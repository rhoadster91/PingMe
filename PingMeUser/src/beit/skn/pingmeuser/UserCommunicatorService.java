package beit.skn.pingmeuser;

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

public class UserCommunicatorService extends Service
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
					m = UserTalker.readMessage();
					Intent iReadRequested = new Intent();
					iReadRequested.setAction(UserApplication.INTENT_TO_ACTIVITY);
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
								UserApplication.notifCount = 0;
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
		while(socket==null)
		{
			try 
			{			
				socket = new Socket(UserApplication.IP_ADDRESS, UserApplication.USER_PORT_NUMBER);				
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
				break;
			}			
		}
		UserTalker.setSocket(socket);
		brSendRequested = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context arg0, Intent arg1) 
			{
				PushableMessage m = (PushableMessage)arg1.getSerializableExtra("pushablemessage");
				if(m.getControl().contentEquals("hello") && !UserApplication.isAuthentic)
				{				
					UserTalker.pushMessage(m);
					try
					{
						m = UserTalker.readMessage();
						if(m.getControl().contentEquals("authentic"))
						{
							Toast.makeText(getApplicationContext(), "Authenticated and registered on server", Toast.LENGTH_LONG).show();
							Intent iIsAuthentic = new Intent();
							iIsAuthentic.setAction(UserApplication.INTENT_TO_ACTIVITY);
							sendBroadcast(iIsAuthentic);
							showPersistentNotification();	
							UserApplication.isAuthentic = true;
							UserApplication.uname = m.getDestination();
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
						stopSelf();
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						errorMessage = "Server shut down. Sorry for the inconvenience. Please try again later.";					
						stopSelf();
						e.printStackTrace();
					} 
					catch (ClassNotFoundException e) 
					{
						errorMessage = "Version mismatch. Please update your app.";
						stopSelf();
						e.printStackTrace();
					}										
				}
				else if(m.getControl().contentEquals("hello") && UserApplication.isAuthentic)
				{
					Intent iIsAuthentic = new Intent();
					iIsAuthentic.setAction(UserApplication.INTENT_TO_ACTIVITY);
					sendBroadcast(iIsAuthentic);
				}
				else
					UserTalker.pushMessage(m);
			}			
		};
		ifSendRequested = new IntentFilter();
		ifSendRequested.addAction(UserApplication.INTENT_TO_SERVICE);
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
		Toast.makeText(getApplicationContext(), "onDestroy Called", Toast.LENGTH_LONG).show();
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    nm.cancel(R.string.servicetext);
	    nm.cancel(R.string.notificationtext);
	    UserApplication.isAuthentic = false;	    
	    UserApplication.errorMessage = errorMessage;
	    DashboardActivity.onErrorOccured(getApplicationContext());
	    if(socket!=null)
	    {
		    PushableMessage m = new PushableMessage(UserApplication.uname, "logout");
		    UserTalker.pushMessage(m);
		    try 
		    {
				socket.close();
			} 
		    catch (IOException e)
			{
				e.printStackTrace();
			}
	    }
		android.os.Process.killProcess(android.os.Process.myPid());	    
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
        showActivity.setAction(UserApplication.INTENT_TO_ACTIVITY);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showActivity, 0);
        notification.setLatestEventInfo(this, getText(R.string.servicename), text, contentIntent);
        notification.flags = Notification.FLAG_NO_CLEAR ^ Notification.FLAG_ONGOING_EVENT;        
        nm.notify(R.string.servicetext, notification);
	}
	
	private void showTempNotification(PushableMessage m)
	{
		UserApplication.notifCount++;		
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String text = String.format(getString(R.string.notificationtext), UserApplication.notifCount);
        Notification notification = new Notification(R.drawable.icon, text, 0);
        Intent showActivity = new Intent(this, DashboardActivity.class);
        showActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        showActivity.putExtra("pushablemessage", m);
        showActivity.setAction(UserApplication.INTENT_TO_ACTIVITY);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showActivity, 0);        
        notification.setLatestEventInfo(this, getText(R.string.servicename), text, contentIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;        
        nm.notify(R.string.notificationtext, notification);
	}
	
}
