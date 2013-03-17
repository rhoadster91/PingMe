package beit.skn.pingmeuser;


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
					UserApplication.IP_ADDRESS = UserApplication.LAN_IP_ADDRESS;
				else
					UserApplication.IP_ADDRESS = ip;
				Intent launchApp = new Intent(getApplicationContext(), UserAuthenticationActivity.class);				
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
					UserApplication.IP_ADDRESS = UserApplication.WAN_IP_ADDRESS;
				else
					UserApplication.IP_ADDRESS = ip;				
				Intent launchApp = new Intent(getApplicationContext(), UserAuthenticationActivity.class);				
				startActivity(launchApp);
				finish();
			}
		});
	}

}
