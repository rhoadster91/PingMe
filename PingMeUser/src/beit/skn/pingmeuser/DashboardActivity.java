package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DashboardActivity extends Activity 
{
	Button bPingText = null;
	Button bLogout = null;
	Button bSplash = null;
	Button bPingCab = null;
	Button bPingRick = null;
	Button bPingCop = null;
	Button bPingAmb = null;
	Button bPingPlace = null;
	Button bPingCode = null;
	Button bPingImage = null;
	ViewPager myPager;
	
	static IntentFilter ifLocationUpdate; 
	static BroadcastReceiver brLocationUpdate;
	static Bitmap bitmap;
	String myLoc = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);	
		ifLocationUpdate = new IntentFilter();		
		ifLocationUpdate.addAction(UserApplication.LOCATION_UPDATE);		
		brLocationUpdate = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context arg0, Intent arg1) 
			{
				myLoc = (String) arg1.getSerializableExtra("Location");
				
			}
			
		};
		registerReceiver(brLocationUpdate, ifLocationUpdate);
		Intent startLocMgr = new Intent(getApplicationContext(), UserLocationManagerService.class);
		startService(startLocMgr);
		
		DashboardPagerAdapter adapter = new DashboardPagerAdapter();
        myPager = (ViewPager) findViewById(R.id.mythreepanelpager);
        myPager.setAdapter(adapter);
        
		
		UserApplication.readSplashboxFromFile(getApplicationContext());
		
		ViewPager.OnPageChangeListener myListener = new ViewPager.OnPageChangeListener(){

			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onPageSelected(int arg0) 
			
			{
				
			}
			public void onPageScrolled(int arg0, float arg1, int arg2)			
			{
				switch(myPager.getCurrentItem())
				{
				case 1:
					bPingCab = (Button)findViewById(R.id.buttonPingTaxi);
					bPingCab.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							Intent startLocMgr = new Intent(getApplicationContext(), UserLocationManagerService.class);
							startService(startLocMgr);
							
							if(myLoc!=null)
							{
								Intent sendMessageToService = new Intent();
								sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
								PushableMessage m = new PushableMessage(UserApplication.uname, PushableMessage.CONTROL_PUSH);
								m.setMessageContent(new String("CAB&&&" + myLoc));
								sendMessageToService.putExtra("pushablemessage", m);
								sendBroadcast(sendMessageToService);
								Toast.makeText(getApplicationContext(), "Pinged for a cab.", Toast.LENGTH_LONG).show();
							}
							else
								Toast.makeText(getApplicationContext(), "Waiting to get location update.", Toast.LENGTH_LONG).show();
						}			
					});
					
					bPingCop = (Button)findViewById(R.id.buttonPingCop);
					bPingCop.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							Intent startLocMgr = new Intent(getApplicationContext(), UserLocationManagerService.class);
							startService(startLocMgr);
							
							if(myLoc!=null)
							{
							
								Intent sendMessageToService = new Intent();
								sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
								PushableMessage m = new PushableMessage(UserApplication.uname, PushableMessage.CONTROL_PUSH);
								m.setMessageContent(new String("COP&&&" + myLoc));
								sendMessageToService.putExtra("pushablemessage", m);
								sendBroadcast(sendMessageToService);
								Toast.makeText(getApplicationContext(), "Pinged for the police.", Toast.LENGTH_LONG).show();
							}
							else
								Toast.makeText(getApplicationContext(), "Waiting to get location update.", Toast.LENGTH_LONG).show();
						}			
					});
					
					bPingRick = (Button)findViewById(R.id.buttonPingAuto);
					bPingRick.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							Intent startLocMgr = new Intent(getApplicationContext(), UserLocationManagerService.class);
							startService(startLocMgr);
						
							if(myLoc!=null)
							{
								Intent sendMessageToService = new Intent();
							
								sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
								PushableMessage m = new PushableMessage(UserApplication.uname, PushableMessage.CONTROL_PUSH);
								m.setMessageContent(new String("RICKSHAW&&&" + myLoc));
								sendMessageToService.putExtra("pushablemessage", m);
								sendBroadcast(sendMessageToService);
								Toast.makeText(getApplicationContext(), "Pinged for a rickshaw.", Toast.LENGTH_LONG).show();
							}
							else
								Toast.makeText(getApplicationContext(), "Waiting to get location update.", Toast.LENGTH_LONG).show();
						}			
					});
					
					bPingAmb = (Button)findViewById(R.id.buttonPingAmbulance);
					bPingAmb.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							Intent startLocMgr = new Intent(getApplicationContext(), UserLocationManagerService.class);
							startService(startLocMgr);
							
							if(myLoc!=null)
							{
								Intent sendMessageToService = new Intent();
								sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
								PushableMessage m = new PushableMessage(UserApplication.uname, PushableMessage.CONTROL_PUSH);
								m.setMessageContent(new String("AMBULANCE&&&" + myLoc));
								sendMessageToService.putExtra("pushablemessage", m);
								sendBroadcast(sendMessageToService);
								Toast.makeText(getApplicationContext(), "Pinged for an ambulance.", Toast.LENGTH_LONG).show();
							}
							else
								Toast.makeText(getApplicationContext(), "Waiting to get location update.", Toast.LENGTH_LONG).show();
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
					
					bPingImage = (Button)findViewById(R.id.buttonPingImage);
					bPingImage.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(cameraIntent, 0);
							
						}			
					});
					
					bPingCode = (Button)findViewById(R.id.buttonPingCode);
					bPingCode.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							Intent iShowDevices = new Intent(getApplicationContext(), DeviceListActivity.class);
							startActivity(iShowDevices);
						}			
					});
					
					bPingPlace = (Button)findViewById(R.id.buttonPingPlace);
					bPingPlace.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							Intent startLocMgr = new Intent(getApplicationContext(), UserLocationManagerService.class);
							startService(startLocMgr);						
							if(myLoc!=null)
							{								
								Intent iPingText = new Intent(getApplicationContext(), PingTextActivity.class);
								iPingText.putExtra("Loc", new String("LOC " + myLoc.split("&&&")[0] + " " + myLoc.split("&&&")[1]));
								startActivity(iPingText);
							}
							else
								Toast.makeText(getApplicationContext(), "Waiting to get location update.", Toast.LENGTH_LONG).show();
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
					
					
					break;
				
				}
			}
			
		};
		myPager.setOnPageChangeListener(myListener);
		myPager.setCurrentItem(0);	
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 0) 
	    {
	        if (resultCode == RESULT_OK) 
	        {
	        	bitmap = (Bitmap) data.getExtras().get("data");	        	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle("Enter recepient name");
	        	final EditText input = new EditText(this);
	        	input.setInputType(InputType.TYPE_CLASS_TEXT);
	        	builder.setView(input);
	        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	Intent sendMessageToService = new Intent();
	    				sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
	    				PushableMessage m = new PushableMessage(UserApplication.uname, PushableMessage.CONTROL_PING_IMAGE);
	    				m.setDestination(input.getText().toString());
	    				sendMessageToService.putExtra("pushablemessage", m);
	    				sendBroadcast(sendMessageToService);
	        	    }
	        	});
	        	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
	        	{
	        	    public void onClick(DialogInterface dialog, int which) {
	        	        dialog.cancel();
	        	    }
	        	});

	        	builder.show();
	        	
	            
	        }
	        if(resultCode == RESULT_CANCELED)
	        {
	        	// do nothing
	        }
	    }
	}
	
	
	
	@Override
	protected void onDestroy()
	{
		try
		{
			unregisterReceiver(brLocationUpdate);
		}
		catch(IllegalArgumentException iae)
		{
			
		}
		super.onDestroy();
	}



	@Override
	protected void onPause() 
	{
		try
		{
			unregisterReceiver(brLocationUpdate);
		}
		catch(IllegalArgumentException iae)
		{
			
		}
		super.onPause();
	}

	

	@Override
	protected void onResume() 
	{
		try
		{
			unregisterReceiver(brLocationUpdate);
		}
		catch(IllegalArgumentException iae)
		{
			
		}
		registerReceiver(brLocationUpdate, ifLocationUpdate);
		super.onResume();
	}



	protected static void onErrorOccured(Context con)
	{
		if(!UserApplication.errorMessage.contentEquals(""))
			Toast.makeText(con, UserApplication.errorMessage, Toast.LENGTH_LONG).show();
	}	
	
}
