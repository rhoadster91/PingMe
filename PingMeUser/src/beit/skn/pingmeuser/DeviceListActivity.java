package beit.skn.pingmeuser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
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
				String str = (String)o;			
				Toast.makeText(getApplicationContext(), "You have selected the device: " + str, Toast.LENGTH_LONG).show();				
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
