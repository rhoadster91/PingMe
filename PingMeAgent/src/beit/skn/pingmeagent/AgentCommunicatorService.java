package beit.skn.pingmeagent;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import beit.skn.classes.PushableMessage;
import beit.skn.classes.RSAEncryptorClass;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class AgentCommunicatorService extends Service
{
	private static BroadcastReceiver brSendRequested = null;
	private static IntentFilter ifSendRequested = null;
	private String errorMessage = null;
	private boolean logoutRequested = false;
	Socket socket = null;
	ObjectOutputStream objOut = null;
	ObjectInputStream objIn = null;
	private boolean handshaked = false;
	private int serverPublicKey, serverModulus;
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		return START_STICKY;
	}	
	
		
	@Override
	public void onCreate() 
	{		
		new Handshaker().execute();
		super.onCreate();
	}
	
	private class Handshaker extends AsyncTask<Void, Void, Void>
	{		
		
		@Override
		protected Void doInBackground(Void... params)
		{			
			try 
			{
				socket = new Socket(AgentApplication.IP_ADDRESS, AgentApplication.AGENT_PORT_NUMBER);
				objIn = new ObjectInputStream(socket.getInputStream());					
				objOut = new ObjectOutputStream(socket.getOutputStream());				
				PushableMessage m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_HELLO);
				String publicKeySpecification = new String(RSAEncryptorClass.getPublicKey() + "," + RSAEncryptorClass.getModulus());
				m.setMessageContent(publicKeySpecification);				
				try 
				{
					objOut.writeObject(m);						
					objOut.flush(); 
					m = (PushableMessage)objIn.readObject();					
					if(m.getControl().contentEquals(PushableMessage.CONTROL_HELLO))
					{
						serverPublicKey = Integer.parseInt(((String)m.getMessageContent()).split(",")[0]);
						serverModulus = Integer.parseInt(((String)m.getMessageContent()).split(",")[1]);
					}
					m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_LOGIN);
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					String upass = sharedPref.getString("upass", "");
					if(upass.contentEquals(""))					
						m.setMessageContent(RSAEncryptorClass.encryptText(AgentApplication.upass, serverModulus, serverPublicKey));					
					else					
					{
						m.setMessageContent(RSAEncryptorClass.encryptText(upass, serverModulus, serverPublicKey));
						m.setEncrypted(true);
					}
					objOut.writeObject(m);						
					objOut.flush(); 
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (ClassNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				try
				{						
					m = (PushableMessage)objIn.readObject();
					if(m.getControl().contentEquals(PushableMessage.CONTROL_AUTHENTIC))
					{
						if(!AgentApplication.isAuthentic)
						{
							AgentApplication.upass = RSAEncryptorClass.decryptText((int [])m.getMessageContent());							
							Intent iIsAuthentic = new Intent();
							iIsAuthentic.setAction(AgentApplication.INTENT_TO_ACTIVITY);
							sendBroadcast(iIsAuthentic);							
							AgentApplication.isAuthentic = true;
						}
						showPersistentNotification();						
					}
					else
					{
						if(m.getControl().contentEquals(PushableMessage.CONTROL_ABORT))
						{
							SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
							SharedPreferences.Editor prefEditor = sharedPref.edit();						
							prefEditor.putString("uname", "");
							prefEditor.putString("upass", "");
							prefEditor.commit();	
						}
						errorMessage = "Could not authenticate. Please check credentials and/or your internet connection.";
						socket.close();
						socket = null;
						this.cancel(true);
						stopSelf();
					}
				} 
				catch(ConnectException ce)
				{
					errorMessage = "Server not working.";
					Log.d("ConnectExcept", "Not connecting");
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
			catch (UnknownHostException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) 
		{
			initiateSendRequestListeners();			
			if(socket!=null)
			{
				Intent startLocMgr = new Intent(getApplicationContext(), AgentLocationManagerService.class);
				startService(startLocMgr);
				handshaked = true;
				new MessageReader().execute();
			}
			super.onPostExecute(result);
		}
	}
	
	
	private class MessageReader extends AsyncTask<Void, Void, Void>
	{		
		@Override
		protected Void doInBackground(Void...params)
		{			
			while(socket!=null)
			{
				PushableMessage m;
				try 
				{
					m = (PushableMessage)objIn.readObject();
					if(m.getControl().contentEquals(PushableMessage.CONTROL_PING_TEXT) || m.getControl().contentEquals(PushableMessage.CONTROL_PUSH))
					{
						AgentApplication.splashBox.add(m);
						AgentApplication.pendingMessage = m;
						AgentApplication.writeSplashBoxToFile(getApplicationContext());
					}
					else if(m.getControl().contentEquals(PushableMessage.CONTROL_LOGOUT))
					{
						socket.close();
						socket = null;
						objIn = null;
						objOut = null;
						logoutRequested = true;
						handshaked = false;
						AgentApplication.isAuthentic = false;
						this.cancel(true);		
						stopSelf();
						break;
					}
					else if(m.getControl().contentEquals(PushableMessage.CONTROL_ABORT))
					{
						AgentApplication.pendingAbortMessage = m;						
					}
					else if(m.getControl().contentEquals(PushableMessage.CONTROL_OK))
					{
						if(m.getSender().contentEquals("busy"))
							AgentApplication.isBusy = true;
						else if(m.getSender().contentEquals("online"))
							AgentApplication.isBusy = false;
					}
					Intent iReadRequested = new Intent();
					iReadRequested.setAction(AgentApplication.INTENT_TO_ACTIVITY);
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
			return null;
			
		}
		
		@Override
		protected void onPostExecute(Void result) 
		{
			initiateSendRequestListeners();
			super.onPostExecute(result);
		}

	}
	
	private class MessageSender extends AsyncTask<PushableMessage, Void, Void>
	{		
		@Override
		protected Void doInBackground(PushableMessage... params)
		{			
			if(!handshaked)
			{
				new Handshaker().execute();
			}
			PushableMessage m = params[0];
			try 
			{
				if(socket!=null)
				{
					objOut.writeObject(m);						
					objOut.flush();
				}
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				stopSelf();				
				e.printStackTrace();
			} 			
			return null;					 			
		}
		
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
		}

	}
	
	
	public void initiateSendRequestListeners()
	{
		ifSendRequested = new IntentFilter();
		ifSendRequested.addAction(AgentApplication.INTENT_TO_SERVICE);		
		brSendRequested = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context context, Intent intent) 
			{
				PushableMessage m = (PushableMessage) intent.getSerializableExtra("pushablemessage");
				new MessageSender().execute(m);
			}
			
		};
		registerReceiver(brSendRequested, ifSendRequested);
	}
	
	@Override
	public void onDestroy() 
	{				
		Intent iTerminateLocationService = new Intent();
		iTerminateLocationService.setAction(AgentApplication.TERMINATE_LOCATION_SERVICE);
		sendBroadcast(iTerminateLocationService);		
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
	    AgentApplication.isAuthentic = false;
	    if(!logoutRequested)
	    {
	    	AgentApplication.errorMessage = errorMessage;
	    	DashboardActivity.onErrorOccured(getApplicationContext());
	    }	  
	    else
	    	AgentApplication.errorMessage = "";
	    try
	    {
	    	unregisterReceiver(brSendRequested);
	    }
	    catch(Exception e)
	    {
	    	
	    }
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
