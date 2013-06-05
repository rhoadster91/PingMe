package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
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
				}
				
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
