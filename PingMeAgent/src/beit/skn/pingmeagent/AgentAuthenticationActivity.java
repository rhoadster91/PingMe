package beit.skn.pingmeagent;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AgentAuthenticationActivity extends Activity
{
	private Button login = null;
	private EditText txt1 = null;
	private EditText txt2 = null;	
	private String uname, upass;
	private static SharedPreferences sharedPref = null;
	private static SharedPreferences.Editor prefEditor = null;
	private static BroadcastReceiver brVerifyAuthenticity = null;
	private static IntentFilter ifIncomingMessage = null;
	
	@Override
	protected void onDestroy() 
	{
		if(brVerifyAuthenticity!=null)
			unregisterReceiver(brVerifyAuthenticity);
		super.onDestroy();
	}

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		uname = sharedPref.getString("uname", null);
		upass = sharedPref.getString("upass", null);
		if(uname!=null && upass!=null)
		{
			Intent skipAuthentication = new Intent(this, DashboardActivity.class);
			startActivity(skipAuthentication);
			finish();
		}
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);		        
    	login = (Button)findViewById(R.id.button1);
		txt1 = (EditText)findViewById(R.id.txtUser);
		txt2 = (EditText)findViewById(R.id.txtPass);	
		login.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick(View v) 
				{	
					uname = txt1.getText().toString();
					upass = txt2.getText().toString();							
					Intent startCommunicator = new Intent(getApplicationContext(), AgentCommunicatorService.class);
					startService(startCommunicator);
					prefEditor = sharedPref.edit();
					prefEditor.putString("uname", uname);
					prefEditor.putString("upass", upass);
					Intent sendPushMessageToActivity = new Intent();
					sendPushMessageToActivity.setAction(AgentCommunicatorService.INTENT_TO_SERVICE);
					PushableMessage m = new PushableMessage(uname, "hello");
					m.setMessageContent(upass);
					sendPushMessageToActivity.putExtra("pushablemessage", m);
					sendStickyBroadcast(sendPushMessageToActivity);
					ifIncomingMessage = new IntentFilter();
					ifIncomingMessage.addAction(AgentCommunicatorService.INTENT_TO_ACTIVITY);
					brVerifyAuthenticity = new BroadcastReceiver()
					{
						@Override
						public void onReceive(Context context, Intent intent) 
						{
							Intent doneAuthentication = new Intent(getApplicationContext(), DashboardActivity.class);
							startActivity(doneAuthentication);
							finish();							
						}						
					};
					registerReceiver(brVerifyAuthenticity, ifIncomingMessage);
				}
			}
		);
	}
}