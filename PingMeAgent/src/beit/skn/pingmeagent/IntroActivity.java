package beit.skn.pingmeagent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class IntroActivity extends Activity 
{
	Button bLAN = null;
	Button bWAN = null;
	EditText customIP = null;
	String ip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		bLAN = (Button)findViewById(R.id.bLAN);
		bWAN = (Button)findViewById(R.id.bWAN);
		customIP = (EditText)findViewById(R.id.customip);
		
		
		
		bLAN.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				ip = customIP.getText().toString().trim();
				if(ip.contentEquals(""))
					AgentApplication.IP_ADDRESS = AgentApplication.LAN_IP_ADDRESS;
				else
					AgentApplication.IP_ADDRESS = ip;
				Intent launchApp = new Intent(getApplicationContext(), AgentAuthenticationActivity.class);				
				startActivity(launchApp);
				finish();
			}
		});
		
		bWAN.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v) 
			{
				ip = customIP.getText().toString().trim();
				if(ip.contentEquals(""))
					AgentApplication.IP_ADDRESS = AgentApplication.WAN_IP_ADDRESS;
				else
					AgentApplication.IP_ADDRESS = ip;				
				Intent launchApp = new Intent(getApplicationContext(), AgentAuthenticationActivity.class);				
				startActivity(launchApp);
				finish();
			}
		});
	}

}
