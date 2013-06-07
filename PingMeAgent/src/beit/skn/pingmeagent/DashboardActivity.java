package beit.skn.pingmeagent;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity 
{
	private static BroadcastReceiver brGetIncomingMessages = null;
	private static IntentFilter ifIncomingMessage = null;
	private static boolean isDisplayingRequest = false;
	private static boolean isDisplayingAbort = false;
	Button bLogout = null;
	Button bBusyToggle = null;
	TextView agentStatus = null;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);		
		updateBusyStatus();
		bBusyToggle = (Button)findViewById(R.id.buttonToggleBusy);
		bBusyToggle.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				if(AgentApplication.isBusy)
				{
					Intent sendMessageToService = new Intent();
					sendMessageToService.setAction(AgentApplication.INTENT_TO_SERVICE);
					PushableMessage m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_ABORT);
					sendMessageToService.putExtra("pushablemessage", m);
					sendBroadcast(sendMessageToService);
				}
				else
				{
					Intent sendMessageToService = new Intent();
					sendMessageToService.setAction(AgentApplication.INTENT_TO_SERVICE);
					PushableMessage m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_OK);
					sendMessageToService.putExtra("pushablemessage", m);
					sendBroadcast(sendMessageToService);
				}
			}			
		});
		bLogout = (Button)findViewById(R.id.buttonLogout);
		bLogout.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Intent sendMessageToService = new Intent();
				sendMessageToService.setAction(AgentApplication.INTENT_TO_SERVICE);
				PushableMessage m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_LOGOUT);
				sendMessageToService.putExtra("pushablemessage", m);
				sendBroadcast(sendMessageToService);
				Toast.makeText(getApplicationContext(), "Logged out successfully.", Toast.LENGTH_LONG).show();
				NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				nm.cancel(R.string.servicetext);
				finish();
			}			
		});
		if(!isDisplayingRequest)
			displayPendingMessage();
		if(!isDisplayingAbort)
			displayAbortMessage();		
		AgentApplication.readSplashBoxFromFile(getApplicationContext());
		AgentApplication.notifCount = 0;		
		ifIncomingMessage = new IntentFilter();
		ifIncomingMessage.addAction(AgentApplication.INTENT_TO_ACTIVITY);
		brGetIncomingMessages = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				PushableMessage m = (PushableMessage)intent.getSerializableExtra("pushablemessage");
				if(m.getControl().contentEquals(PushableMessage.CONTROL_OK))
					updateBusyStatus();
				setResultCode(Activity.RESULT_OK);
			}						
		};
		registerReceiver(brGetIncomingMessages, ifIncomingMessage);		
	}

	@Override
	protected void onDestroy() 
	{
		try
		{
			unregisterReceiver(brGetIncomingMessages);
		}
		catch(IllegalArgumentException iae)
		{
			// Do nothing
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() 
	{
		try
		{
			unregisterReceiver(brGetIncomingMessages);
		}
		catch(IllegalArgumentException iae)
		{
			// Do nothing
		}
		super.onPause();
	}
	
	protected void updateBusyStatus()
	{
		bBusyToggle = (Button)findViewById(R.id.buttonToggleBusy);
		agentStatus = (TextView)findViewById(R.id.agentStatus);
		if(AgentApplication.isBusy)
		{
			bBusyToggle.setBackgroundResource(R.drawable.busy);
			agentStatus.setText(R.string.busy_now);
		}
		else
		{
			
			bBusyToggle.setBackgroundResource(R.drawable.free);			
			agentStatus.setText(R.string.wait_now);
		}
		
	}
	
	@Override
	protected void onResume() 
	{
		if(!isDisplayingRequest)
			displayPendingMessage();
		if(!isDisplayingAbort)
			displayAbortMessage();		
		AgentApplication.notifCount = 0;		
		registerReceiver(brGetIncomingMessages, ifIncomingMessage);	
		super.onResume();
	}	
	
	
	protected static void onErrorOccured(Context con)
	{
		Toast.makeText(con, AgentApplication.errorMessage, Toast.LENGTH_LONG).show();
	}	
	
	protected void displayPendingMessage()
	{
		if(AgentApplication.pendingMessage!=null)
		{
			new AlertDialog.Builder(this)
		    .setTitle("New pending request")
		    .setMessage("Do you want to respond?")
		    .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) 
		        { 
		        	String uri = "geo:0,0?q=" + ((String)AgentApplication.pendingMessage.getMessageContent()).split("&&&")[1] + "," + ((String)AgentApplication.pendingMessage.getMessageContent()).split("&&&")[2] + "(go here)";
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
					startActivity(intent);
					Intent sendMessageToService = new Intent();
					sendMessageToService.setAction(AgentApplication.INTENT_TO_SERVICE);
					PushableMessage m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_PUSH);
					m.setDestination(""+AgentApplication.pendingMessage.getSender());
					sendMessageToService.putExtra("pushablemessage", m);
					sendBroadcast(sendMessageToService);
		        	AgentApplication.pendingMessage = null;
		        	isDisplayingRequest = false;
		        	AgentApplication.isBusy = true;
		        	updateBusyStatus();
		        }
		     })
		    .setNegativeButton("No", new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) 
		        { 
		        	Intent sendMessageToService = new Intent();
					sendMessageToService.setAction(AgentApplication.INTENT_TO_SERVICE);
					PushableMessage m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_ABORT);
					sendMessageToService.putExtra("pushablemessage", m);
					sendBroadcast(sendMessageToService);
		        	AgentApplication.pendingMessage = null;
		        	isDisplayingRequest = false;
		        }
		     })
		     .show();
			isDisplayingRequest = true;
		}
	}
	
	protected void displayAbortMessage()
	{
		if(AgentApplication.pendingAbortMessage!=null)
		{
			String reasonForAbort;
			if(AgentApplication.pendingAbortMessage.getSender().contentEquals("server"))
				reasonForAbort = "User request has already been taken by another agent.";
			else
				reasonForAbort = "User request has been canceled by user.";
			new AlertDialog.Builder(this)
		    .setTitle("Alert")
		    .setMessage(reasonForAbort)
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) 
		        { 
		        	   	AgentApplication.pendingAbortMessage = null;
		        	   	isDisplayingAbort = false;
		        }
		     })
		    .show();
			isDisplayingAbort = true;
		}
	}
}

