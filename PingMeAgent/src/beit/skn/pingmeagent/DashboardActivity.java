package beit.skn.pingmeagent;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class DashboardActivity extends Activity 
{
	private static BroadcastReceiver brVerifyAuthenticity = null;
	private static IntentFilter ifIncomingMessage = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);			
		ifIncomingMessage = new IntentFilter();
		ifIncomingMessage.addAction(AgentCommunicatorService.INTENT_TO_ACTIVITY);
		brVerifyAuthenticity = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				PushableMessage m = (PushableMessage)intent.getSerializableExtra("pushablemessage");
				Toast.makeText(getApplicationContext(), (String)m.getMessageContent(), Toast.LENGTH_LONG).show();
				setResultCode(Activity.RESULT_OK);
			}						
		};
		registerReceiver(brVerifyAuthenticity, ifIncomingMessage);
	}

	@Override
	protected void onDestroy() 
	{
		try
		{
			unregisterReceiver(brVerifyAuthenticity);
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
			unregisterReceiver(brVerifyAuthenticity);
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
		registerReceiver(brVerifyAuthenticity, ifIncomingMessage);	
		super.onResume();
	}	
	
}
