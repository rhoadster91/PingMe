package beit.skn.pingmeuser;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class UserAuthenticationActivity extends Activity
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
    public void onCreate(Bundle savedInstanceState)
    {				
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Typeface cfont = Typeface.createFromAsset(getAssets(),"fonts/customfont.otf");
        Button buttonTitle = (Button)findViewById(R.id.tBarLogin);
        buttonTitle.setTypeface(cfont);
    	login = (Button)findViewById(R.id.button1);
		txt1 = (EditText)findViewById(R.id.txtUser);
		txt2 = (EditText)findViewById(R.id.txtPass);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		uname = sharedPref.getString("uname", "");
		upass = sharedPref.getString("upass", "");
		if(!(uname.contentEquals("") || upass.contentEquals("")))
		{
			txt1.setText(uname);
			txt2.setText(upass);
			UserApplication.uname = uname;
			UserApplication.isAuthentic = true;
			Intent startCommunicator = new Intent(getApplicationContext(), UserCommunicatorService.class);
			startService(startCommunicator);	
			Intent doneAuthentication = new Intent(getApplicationContext(), DashboardActivity.class);
			startActivity(doneAuthentication);
			finish();
		}
		login.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick(View v) 
				{	
					uname = txt1.getText().toString();
					upass = txt2.getText().toString();			
					UserApplication.uname = uname;					
					Intent startCommunicator = new Intent(getApplicationContext(), UserCommunicatorService.class);
					startService(startCommunicator);
					ifIncomingMessage = new IntentFilter();
					ifIncomingMessage.addAction(UserApplication.INTENT_TO_ACTIVITY);
					try
					{
						unregisterReceiver(brVerifyAuthenticity);			
					}
					catch(IllegalArgumentException iae)
					{
						// Do nothing
					}
					brVerifyAuthenticity = new BroadcastReceiver()
					{
						@Override
						public void onReceive(Context context, Intent intent) 
						{
							Intent doneAuthentication = new Intent(getApplicationContext(), DashboardActivity.class);
							prefEditor = sharedPref.edit();
							prefEditor.putString("uname", uname);
							prefEditor.putString("upass", upass);
							prefEditor.commit();							
							startActivity(doneAuthentication);
							unregisterReceiver(this);
							finish();							
						}						
					};
					registerReceiver(brVerifyAuthenticity, ifIncomingMessage);
				}
			}
		);
	}
}