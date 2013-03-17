package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
		DashboardPagerAdapter adapter = new DashboardPagerAdapter();
        final ViewPager myPager = (ViewPager) findViewById(R.id.mythreepanelpager);
        myPager.setAdapter(adapter);
        
		
		UserApplication.readObjectFromFile(getApplicationContext());
		
		myPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
				switch(myPager.getCurrentItem())
				{
				case 2:
					bPingText = (Button)findViewById(R.id.buttonPingText);
					bPingText.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							Intent iPingText = new Intent(getApplicationContext(), PingTextActivity.class);
							startActivity(iPingText);
						}			
					});
					
					bLogout = (Button)findViewById(R.id.buttonLogOut);
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
							NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
							nm.cancel(R.string.servicetext);
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
					break;
				
				}
			}

			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		myPager.setCurrentItem(1);

		
	}

	
	
	protected static void onErrorOccured(Context con)
	{
		if(!UserApplication.errorMessage.contentEquals(""))
			Toast.makeText(con, UserApplication.errorMessage, Toast.LENGTH_LONG).show();
	}	
	
}
