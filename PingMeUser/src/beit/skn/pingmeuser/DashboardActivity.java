package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DashboardActivity extends Activity 
{
	Button bPingText = null;
	Button bLogout = null;
	Button bSplash = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);			
		UserApplication.readObjectFromFile(getApplicationContext());
		
		bPingText = (Button)findViewById(R.id.buttonPingText);
		bPingText.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Intent iPingText = new Intent(getApplicationContext(), PingTextActivity.class);
				startActivity(iPingText);
			}			
		});
		
		bLogout = (Button)findViewById(R.id.buttonLogout);
		bLogout.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Intent sendMessageToService = new Intent();
				sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
				PushableMessage m = new PushableMessage(UserApplication.uname, PushableMessage.CONTROL_LOGOUT);
				sendMessageToService.putExtra("pushablemessage", m);
				sendBroadcast(sendMessageToService);
				Toast.makeText(getApplicationContext(), "Logged out successfully.", Toast.LENGTH_LONG).show();
				finish();
			}			
		});
		
		bSplash = (Button)findViewById(R.id.dashtosplash);
		bSplash.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Intent iSplashbox = new Intent(getApplicationContext(), SplashBoxActivity.class);
				startActivity(iSplashbox);
			}			
		});
		
	}

	
	
	protected static void onErrorOccured(Context con)
	{
		Toast.makeText(con, UserApplication.errorMessage, Toast.LENGTH_LONG).show();
	}	
	
}
