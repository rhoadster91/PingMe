package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DashboardActivity extends Activity 
{
	private static BroadcastReceiver brGetIncomingMessages = null;
	private static IntentFilter ifIncomingMessage = null;
	private Button sendMessage = null;
	private EditText aname = null;
	private EditText atext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);			
		UserApplication.notifCount = 0;		
		ifIncomingMessage = new IntentFilter();
		ifIncomingMessage.addAction(UserApplication.INTENT_TO_ACTIVITY);
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
		sendMessage = (Button)findViewById(R.id.pushToClient);
		sendMessage.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Intent sendMessageToService = new Intent();
				sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
				PushableMessage m = new PushableMessage(UserApplication.uname, "push");
				aname = (EditText)findViewById(R.id.txtDest);				
				atext = (EditText)findViewById(R.id.txtContent);				
				m.setDestination(aname.getText().toString());			
				m.setMessageContent(atext.getText().toString());
				sendMessageToService.putExtra("pushablemessage", m);
				sendBroadcast(sendMessageToService);
			}			
		});
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
		registerReceiver(brGetIncomingMessages, ifIncomingMessage);	
		super.onResume();
	}	
	
	
	protected static void onErrorOccured(Context con)
	{
		Toast.makeText(con, UserApplication.errorMessage, Toast.LENGTH_LONG).show();
	}	
	
}
