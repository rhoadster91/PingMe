package beit.skn.pingmeuser;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

public class PointsOnMapActivity extends FragmentActivity 
{
	private GoogleMap map;
	static Marker lastClickedMarker;
	static int clickCount = 0;
	static ArrayList<Marker> markerList;
	static boolean newPointAdded = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mymapfragment)).getMap();
	    Intent startLocationManager = new Intent(this, UserLocationManagerService.class);
	    startService(startLocationManager);
	    new InitializeMapTask().execute();
	    
	}
	
	private class InitializeMapTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) 
		{
			while(true)
			{
				if(map==null)
				{
					try 
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				else
				{					
				    return null;
				}					
			}				
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			map.setMyLocationEnabled(true);	
			map.setOnMyLocationChangeListener(new OnMyLocationChangeListener()
			{

				public void onMyLocationChange(Location arg0)
				{					
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 15));	    
				    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);		
				    map.setOnMyLocationChangeListener(null);
				}
				
			});
			map.setOnMarkerClickListener(new OnMarkerClickListener()
			{

				public boolean onMarkerClick(Marker arg0) 
				{
					if(arg0.equals(lastClickedMarker))
					{
						clickCount++;
						if(clickCount==4)
						{
							AlertDialog.Builder builder = new AlertDialog.Builder(PointsOnMapActivity.this);
				        	builder.setTitle("Confirm");
				        	final TextView text = new TextView(getApplicationContext());
				        	text.setText("Do you want to delete this trigger?");
				        	builder.setView(text);
				        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
				        	{ 
				        	    public void onClick(DialogInterface dialog, int which) 
				        	    {
				        	    	int i = 0;
				        	    	for(Marker curMarker:markerList)
				        			{					        	    		
				        				if(curMarker.equals(lastClickedMarker))
				        				{
				        					
				        					UserApplication.pointList.remove(i);
				        					UserApplication.writePointListToFile(getApplicationContext());
				        					clickCount = 0;
				        					lastClickedMarker = null;
				        					refreshPoints();
				        					return;
				        				}
				        				i++;
				        			}
				        	    	
				        	    }
				        	});
				        	builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
				        	{
				        	    public void onClick(DialogInterface dialog, int which) 
				        	    {
				        	    	
				        	    }
				        	});

				        	builder.show();
						}
					}
					else
					{
						clickCount = 1;
						lastClickedMarker = arg0;
					}
					return false;
				}
				
			});
			map.setOnMarkerDragListener(new OnMarkerDragListener()
			{

				public void onMarkerDrag(Marker arg0) 
				{
					// TODO Auto-generated method stub
					
				}

				public void onMarkerDragEnd(Marker arg0) 
				{
					LatLng latlng = arg0.getPosition();
					LocationPoint newLocPoint = new LocationPoint(arg0.getTitle(), latlng.latitude, latlng.longitude, 100);
					int i = 0;
        	    	for(Marker curMarker:markerList)
        			{					        	    		
        				if(curMarker.equals(arg0))
        				{
        					newLocPoint.radius = UserApplication.pointList.get(i).radius;
        					UserApplication.pointList.set(i, newLocPoint);
        					UserApplication.writePointListToFile(getApplicationContext());
        					clickCount = 0;
        					lastClickedMarker = null;
        					//refreshPoints();
        					animateMarker(arg0);
        					return;
        				}
        				i++;
        			}
					
				}

				public void onMarkerDragStart(Marker arg0) 
				{
					// TODO Auto-generated method stub
					
				}
				
			});
			map.setOnMapClickListener(new OnMapClickListener()
		    {

				public void onMapClick(LatLng arg0) 
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(PointsOnMapActivity.this);
		        	builder.setTitle("Place new trigger here?");
		        	final LatLng curLatLng = arg0;
		        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		        	{ 
		        	    public void onClick(DialogInterface dialog, int which) 
		        	    {
		        	    	AlertDialog.Builder builder = new AlertDialog.Builder(PointsOnMapActivity.this);
				        	builder.setTitle("Set radius (in metres)");
				        	final EditText inputRadius = new EditText(getApplicationContext());
				        	inputRadius.setInputType(InputType.TYPE_CLASS_NUMBER);
				        	inputRadius.setText("100");
				        	builder.setView(inputRadius);				        	
				        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
				        	{ 
				        	    public void onClick(DialogInterface dialog, int which) 
				        	    {
				        	    	AlertDialog.Builder builder = new AlertDialog.Builder(PointsOnMapActivity.this);
						        	builder.setTitle("Set label");
						        	final EditText inputLabel = new EditText(getApplicationContext());
						        	inputLabel.setInputType(InputType.TYPE_CLASS_TEXT);						        	
						        	builder.setView(inputLabel);				        	
						        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
						        	{ 
						        	    public void onClick(DialogInterface dialog, int which) 
						        	    {						        	    	
						        	    	LocationPoint temp = new LocationPoint(inputLabel.getText().toString(), curLatLng.latitude, curLatLng.longitude, Double.parseDouble(inputRadius.getText().toString()));
						        	    	UserApplication.pointList.add(temp);
						        	    	UserApplication.writePointListToFile(getApplicationContext());
						        	    	newPointAdded = true;				        					
						        	    	refreshPoints();						        	    	
						        	    }
						        	}).show(); 					        	    	
				        	    	
				        	    }
				        	}).show(); 	
		        	    		    				
		        	    }
		        	});
		        	builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
		        	{
		        	    public void onClick(DialogInterface dialog, int which)
		        	    {
		        	        
		        	    }
		        	}).show();
				}
		    	
		    });
			refreshPoints();
		    super.onPostExecute(result);		    
		}		
	}	
	
	private void refreshPoints()
	{
		markerList = new ArrayList<Marker>();	    
		UserApplication.readPointListFromFile(getApplicationContext());	
		Marker marker = null;
		if(map!=null)
		{
			map.clear();
			for(LocationPoint curLocPoint:UserApplication.pointList)
			{
				marker = map.addMarker(new MarkerOptions().position(new LatLng(curLocPoint.latitude, curLocPoint.longitude)).title(curLocPoint.label));
				marker.setDraggable(true);
				map.addCircle(new CircleOptions()
				.center(new LatLng(curLocPoint.latitude, curLocPoint.longitude))
		        .radius(curLocPoint.radius)
		        .strokeColor(Color.BLUE)
		    	.strokeWidth(2)
		        .fillColor(0x330000ff));
				markerList.add(marker);				
				
			}
			marker = markerList.get(markerList.size() - 1);
			if(newPointAdded && marker!=null)
				animateMarker(marker);				
			
		}
	}
	
	private void animateMarker(final Marker marker)
	{
		final LatLng target = marker.getPosition();
		final long duration = 800;
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();		
		Projection proj = map.getProjection();

		Point startPoint = proj.toScreenLocation(marker.getPosition());
		startPoint.offset(0, -50);	
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() 
		{
		    public void run() 
		    {
		        long elapsed = SystemClock.uptimeMillis() - start;
		        float t = interpolator.getInterpolation((float) elapsed / duration);
		        double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
		        double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
		        marker.setPosition(new LatLng(lat, lng));
		        if (t < 1.0)
		        {
		            // Post again 10ms later.
		            handler.postDelayed(this, 5);
		        }
		        else 
		        {
		        	if(newPointAdded)
		        		newPointAdded = false;
		        	else
		        		refreshPoints();
		        }
		    }
		});
	}
}
