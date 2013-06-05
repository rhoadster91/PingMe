package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SplashBoxActivity extends Activity
{
	ListView splashList = null;
	SplashBoxAdapter splashAdapter = null;
	private static BroadcastReceiver brGetIncomingMessages = null;
	private static IntentFilter ifIncomingMessage = null;
	int count;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		refreshList();
		
		UserApplication.notifCount = 0;		
		ifIncomingMessage = new IntentFilter();
		ifIncomingMessage.addAction(UserApplication.INTENT_TO_ACTIVITY);
		brGetIncomingMessages = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				refreshList();
				setResultCode(Activity.RESULT_OK);
			}						
		};
		registerReceiver(brGetIncomingMessages, ifIncomingMessage);	
	}
	
	private void refreshList()
	{
		splashList = (ListView)findViewById(R.id.listView1);
		splashAdapter = new SplashBoxAdapter(getApplicationContext(), UserApplication.splashBox.toArray());
		splashList.setAdapter(splashAdapter);	
		splashList.setClickable(true);
		count = splashList.getCount();
		splashList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				
				Object o = splashList.getItemAtPosition(count - arg2 - 1);
				PushableMessage m = (PushableMessage)o;		
				if(m.getControl().contentEquals(PushableMessage.CONTROL_PING_IMAGE))
				{
					Intent iPingImage = new Intent(getApplicationContext(), ImageViewer.class);
					iPingImage.putExtra("image message", m);
					startActivity(iPingImage);
				}
				else
				{
					if(((String)m.getMessageContent()).split(" ")[0].contentEquals("LOC"))
					{
						String uri = "geo:0,0?q=" + ((String)m.getMessageContent()).split(" ")[1] + "," + ((String)m.getMessageContent()).split(" ")[2] + "(" + m.getSender() + ")";
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
						startActivity(intent);
					}
					else
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(SplashBoxActivity.this);
			        	builder.setTitle(m.getSender());
			        	final TextView text = new TextView(getApplicationContext());
			        	text.setText((String)m.getMessageContent());
			        	builder.setView(text);
			        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			        	{ 
			        	    public void onClick(DialogInterface dialog, int which) 
			        	    {
			        	    	// Do nothing									    				
			        	    }
			        	});
			        	
			        	builder.show();
					}
				}
				
			}
			
		});
		
		splashList.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{				
				AlertDialog.Builder builder = new AlertDialog.Builder(SplashBoxActivity.this);
	        	builder.setTitle("Confirm");
	        	final int i = arg2;
	        	final TextView text = new TextView(getApplicationContext());
	        	text.setText("Do you want to delete this ping?");
	        	builder.setView(text);
	        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	UserApplication.splashBox.remove(count - i - 1);	 
	        	    	UserApplication.writeSplashBoxToFile(getApplicationContext());
	        	    	refreshList();
	        	    }
	        	});
	        	builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
	        	{
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	
	        	    }
	        	});

	        	builder.show();
				return false;
				
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
		refreshList();
		super.onResume();
	}	
	
	
	

}
