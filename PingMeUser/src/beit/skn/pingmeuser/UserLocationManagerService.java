package beit.skn.pingmeuser;

import beit.skn.classes.LocationMath;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.preference.PreferenceManager;

public class UserLocationManagerService extends Service 
{
	static LocationManager locmgr = null;	
	static LocationListener onLocationChange = null;
	static IntentFilter ifTerminateLocationService  = null;
	static BroadcastReceiver brTerminateLocationService = null;
	
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
	public void onCreate() 
	{
		ifTerminateLocationService = new IntentFilter();
		ifTerminateLocationService.addAction(UserApplication.TERMINATE_LOCATION_SERVICE);		
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
	        	iUpdatedLocation.setAction(UserApplication.LOCATION_UPDATE);
	        	String str = new String(loc.getLatitude()  + "&&&" + loc.getLongitude());
				iUpdatedLocation.putExtra("Location", str);
				sendStickyBroadcast(iUpdatedLocation);	
				checkIfEneteredTriggerArea(loc);
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
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void checkIfEneteredTriggerArea(Location loc)
	{
		UserApplication.readPointListFromFile(getApplicationContext());
		for(LocationPoint curLocPoint:UserApplication.pointList)
		{
			if(LocationMath.distance(curLocPoint.latitude, curLocPoint.longitude, loc.getLatitude(), loc.getLongitude())<curLocPoint.radius)
			{
				showTempNotification(curLocPoint);
				return;
			}
		}
	}
	
	private void showTempNotification(LocationPoint locPoint)
	{
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, "You have arrived at " + locPoint.label, 0);
        Intent showActivity = new Intent(this, PointsOnMapActivity.class);
        showActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        showActivity.setAction(UserApplication.INTENT_TO_ACTIVITY);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showActivity, 0);        
        notification.setLatestEventInfo(this, getText(R.string.servicename), "You have arrived at " + locPoint.label, contentIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;  
        if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("dnd", false))			
        	nm.notify(R.string.hello, notification);
	}	
}
