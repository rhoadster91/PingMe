package beit.skn.pingmeuser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class UserLocationManagerService extends Service 
{
	static LocationManager locmgr = null;	
	static LocationListener onLocationChange = null;
	
	@Override
	public void onCreate() 
	{
		locmgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		onLocationChange=new LocationListener() 
		{
	        public void onLocationChanged(Location loc) 
	        {
	        	Intent iUpdatedLocation = new Intent();
	        	iUpdatedLocation.setAction(UserApplication.LOCATION_UPDATE);
	        	String str = new String(loc.getLatitude()  + "&&&" + loc.getLongitude());
				iUpdatedLocation.putExtra("Location", str);
				sendBroadcast(iUpdatedLocation);   	 
				stopSelf();
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
	    locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10000.0f,onLocationChange);
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
}
