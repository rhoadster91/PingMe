package beit.skn.pingmeuser;

import java.io.ByteArrayOutputStream;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
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
	Button bChangeICE = null;
	Button bSignOut = null;
	CheckBox checkPersistent = null;
	CheckBox checkDND = null;
	
	ProgressDialog loading = null;
	ViewPager myPager;
	ViewPager.OnPageChangeListener myListener;
	
	static IntentFilter ifLocationUpdate; 
	static BroadcastReceiver brLocationUpdate;
	static Bitmap bitmap = null;
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
         myPager.setOffscreenPageLimit(3);
		
		UserApplication.readSplashboxFromFile(getApplicationContext());
		
		myListener = new ViewPager.OnPageChangeListener(){

			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			 
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				
			}
					
			public void onPageSelected(int arg0)
			{
				try
				{
					switch(myPager.getCurrentItem())
					{
					case 0:
						bChangeICE = (Button)findViewById(R.id.buttonICE);
						bChangeICE.setOnClickListener(new OnClickListener()
						{
							public void onClick(View arg0)
							{
								SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());								
								AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
					        	builder.setTitle("Current contact number: " + sharedPref.getString("emergency number", "(none)"));
					        	final EditText input = new EditText(getApplicationContext());
					        	input.setInputType(InputType.TYPE_CLASS_PHONE);
					        	builder.setView(input);
					        	builder.setPositiveButton("Change", new DialogInterface.OnClickListener() 
					        	{ 
					        	    public void onClick(DialogInterface dialog, int which) 
					        	    {
					        	    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
										SharedPreferences.Editor prefEditor = sharedPref.edit();	
										prefEditor.putString("emergency number", input.getText().toString());
										prefEditor.commit();	    				
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
						});
						
						bSignOut = (Button)findViewById(R.id.buttonSignOut);
						bSignOut.setOnClickListener(new OnClickListener()
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
								SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
								SharedPreferences.Editor prefEditor = sharedPref.edit();	
								prefEditor.clear();
								prefEditor.commit();
								finish();
							}			
						});
						
						checkPersistent = (CheckBox)findViewById(R.id.checkPersistent);
						checkPersistent.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("persistent notification", true));
						checkPersistent.setOnCheckedChangeListener(new OnCheckedChangeListener()
						{
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
							{
								if(!isChecked)
								{
									AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
						        	builder.setTitle("Caution");
						        	final TextView text = new TextView(getApplicationContext());
						        	text.setText("The persistent notification tells the Android system that PingMe is active, thereby preventing the system from closing it automatically. It is not recommended to close this service, especially if your device has less RAM.");
						        	builder.setView(text);
						        	builder.setPositiveButton("Turn off anyway", new DialogInterface.OnClickListener() 
						        	{ 
						        	    public void onClick(DialogInterface dialog, int which) 
						        	    {
						        	    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
											SharedPreferences.Editor prefEditor = sharedPref.edit();	
											prefEditor.putBoolean("persistent notification", false);
											try
											{
												NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
											    nm.cancel(R.string.servicetext);
											    nm.cancel(R.string.notificationtext);		    
											}
											catch(Exception e)
											{
												// Do nothing
											}
											prefEditor.commit();	    				
						        	    }
						        	});
						        	builder.setNegativeButton("Keep on", new DialogInterface.OnClickListener() 
						        	{
						        	    public void onClick(DialogInterface dialog, int which) 
						        	    {
						        	    	checkPersistent.setChecked(true);
						        	    }
						        	});

						        	builder.show();
								}
								else
								{
									SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
									if(!sharedPref.getBoolean("persistent notification", true))
										Toast.makeText(getApplicationContext(), "Changes will reflect next time you log in", Toast.LENGTH_LONG).show();
									SharedPreferences.Editor prefEditor = sharedPref.edit();	
									prefEditor.putBoolean("persistent notification", true);
									prefEditor.commit();									
								}
							}							
						});
						
						checkDND = (CheckBox)findViewById(R.id.checkDND);
						checkDND.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("dnd", false));
						checkDND.setOnCheckedChangeListener(new OnCheckedChangeListener()
						{
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
							{
								
									SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();	
									prefEditor.putBoolean("dnd", isChecked);
									prefEditor.commit();					
									if(isChecked)
										Toast.makeText(getApplicationContext(), "New pings will not be notified.", Toast.LENGTH_LONG).show();
									else
										Toast.makeText(getApplicationContext(), "New pings will be notified.", Toast.LENGTH_LONG).show();
							}
														
						});	
						
						break;

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
				catch(Exception e)
				{
					// Do nothing
				}
			}
			
		};
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(1);
		loading = ProgressDialog.show(this, "", "Loading...");
		new InitializePagerTask().execute();
	}
	
	private class InitializePagerTask extends AsyncTask<Void, Void, Void> 
	{
@Override
		protected Void doInBackground(Void... params) 
		{
			try 
			{
				
				Thread.sleep(500);
				loading.dismiss();
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void result) 
		{
			myPager.setOnPageChangeListener(myListener);
			myPager.setCurrentItem(0, false);	
			myPager.setCurrentItem(1, false);
			loading.dismiss();
		}
	                       
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
	    				ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	    				byte[] byteArray = stream.toByteArray();
	    				m.setMessageContent(byteArray);
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
