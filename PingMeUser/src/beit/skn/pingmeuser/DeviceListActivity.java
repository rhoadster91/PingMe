package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class DeviceListActivity extends Activity 
{
	ListView deviceList = null;
	DeviceListAdapter deviceAdapter = null;
	Button addDevice = null;
	int count;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device);
		addDevice = (Button)findViewById(R.id.bAddDevice);
		addDevice.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v) 
			{
				Toast.makeText(getApplicationContext(), "Scan QR code on the CodeRunner page", Toast.LENGTH_LONG).show();
				try
				{
				    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
				    startActivityForResult(intent, 0);
				}
				catch (Exception e) 
				{    
				    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
				    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
				    startActivity(marketIntent);
				}
			}
			
		});
		refreshList();
		
		
	}
	
	private void refreshList()
	{
		deviceList = (ListView)findViewById(R.id.listView1);
		deviceAdapter = new DeviceListAdapter(getApplicationContext(), UserApplication.deviceList.toArray());
		deviceList.setAdapter(deviceAdapter);	
		deviceList.setClickable(true);
		count = deviceList.getCount();
		deviceList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				
				Object o = deviceList.getItemAtPosition(arg2);
				final String str = (String)o;			
				AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);
	        	builder.setTitle("Enter command to be executed on this device:");
	        	final EditText input = new EditText(DeviceListActivity.this);
	        	input.setInputType(InputType.TYPE_CLASS_TEXT);
	        	builder.setView(input);
	        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	Intent sendMessageToService = new Intent();
	    				sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
	    				PushableMessage m = new PushableMessage(UserApplication.uname, PushableMessage.CONTROL_PING_CODE);
	    				m.setDestination(str);
	    				m.setMessageContent(input.getText().toString());
	    				sendMessageToService.putExtra("pushablemessage", m);
	    				sendBroadcast(sendMessageToService);	    				
	        	    }
	        	});
	        	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
	        	{
	        	    public void onClick(DialogInterface dialog, int which) {
	        	        dialog.cancel();
	        	    }
	        	}).show();				
			}
			
		});
		deviceList.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)			
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);
	        	builder.setTitle("Confirm");
	        	final int i = arg2;
	        	final TextView text = new TextView(getApplicationContext());
	        	text.setText("Do you want to remove this device?");
	        	builder.setView(text);
	        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	UserApplication.deviceList.remove(i);	 
	        	    	UserApplication.writeDeviceListToFile(getApplicationContext());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 0) 
	    {
	        if (resultCode == RESULT_OK) 
	        {
	            String contents = data.getStringExtra("SCAN_RESULT");
	            UserApplication.deviceList.add(contents);
	            UserApplication.writeDeviceListToFile(getApplicationContext());
	            refreshList();
	        }
	        if(resultCode == RESULT_CANCELED)
	        {
	        	// do nothing
	        }
	    }
	}

	@Override
	protected void onResume() 
	{
		refreshList();
		super.onResume();
	}	
}
