package beit.skn.pingmeagent;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class DashboardActivity extends Activity 
{
	private static BroadcastReceiver brGetIncomingMessages = null;
	private static IntentFilter ifIncomingMessage = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);	
		displayPendingMessage();		
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
				Toast.makeText(getApplicationContext(), (String)m.getMessageContent(), Toast.LENGTH_LONG).show();
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

	@Override
	protected void onResume() 
	{
		displayPendingMessage();
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
		        	AgentApplication.pendingMessage = null;
		        }
		     })
		    .setNegativeButton("No", new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) 
		        { 
		        	AgentApplication.pendingMessage = null;
		        }
		     })
		     .show();
		}
	}
}

