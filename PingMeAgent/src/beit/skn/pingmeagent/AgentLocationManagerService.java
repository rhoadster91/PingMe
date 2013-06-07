package beit.skn.pingmeagent;

import beit.skn.classes.PushableMessage;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;


public class AgentLocationManagerService extends Service
{
	static LocationManager locmgr = null;	
	static LocationListener onLocationChange = null;
	static IntentFilter ifTerminateLocationService  = null;
	static BroadcastReceiver brTerminateLocationService = null;
	
	@Override
	public void onCreate() 
	{
		ifTerminateLocationService = new IntentFilter();
		ifTerminateLocationService.addAction(AgentApplication.TERMINATE_LOCATION_SERVICE);		
		brTerminateLocationService = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context context, Intent intent) 
			{
				stopSelf();
			}
			
		};
		registerReceiver(brTerminateLocationService, ifTerminateLocationService);
		locmgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		onLocationChange=new LocationListener() 
		{
	        public void onLocationChanged(Location loc) 
	        {
	        	Intent iUpdatedLocation = new Intent();
	        	iUpdatedLocation.setAction(AgentApplication.LOCATION_UPDATE);
	        	String str = new String(loc.getLatitude()  + "&&&" + loc.getLongitude());
				iUpdatedLocation.putExtra("Location", str);
				sendStickyBroadcast(iUpdatedLocation);	
				Intent sendMessageToService = new Intent();
				sendMessageToService.setAction(AgentApplication.INTENT_TO_SERVICE);
				PushableMessage m = new PushableMessage(AgentApplication.uname, PushableMessage.CONTROL_UPDATE_LOCATION);
				m.setMessageContent(loc.getLatitude() + "&&&" + loc.getLongitude());
				sendMessageToService.putExtra("pushablemessage", m);
				sendBroadcast(sendMessageToService);
			}
	         
	        public void onProviderDisabled(String provider) 
	        {
	        // required for interface, not used
	        }
	         
	        public void onProviderEnabled(String provider) 
	        {
	        // required for interface, not used
	        }
	         
	        public void onStatusChanged(String provider, int status, Bundle extras) 
	        {
	        // required for interface, not used
	        }

			
	    };
	    locmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,100,onLocationChange);
	    if(locmgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
	    	locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,100,onLocationChange);
	    super.onCreate();
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		
		return START_STICKY;
	}

	

	@Override
	public void onDestroy() 
	{
		try
		{
			unregisterReceiver(brTerminateLocationService);
			locmgr.removeUpdates(onLocationChange);
		}
		catch(Exception e)
		{
			
		}
		super.onDestroy();
	}


	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
