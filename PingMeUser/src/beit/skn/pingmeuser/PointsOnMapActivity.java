package beit.skn.pingmeuser;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.widget.EditText;

public class PointsOnMapActivity extends FragmentActivity 
{
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mymapfragment)).getMap();
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
		 UserApplication.readPointListFromFile(getApplicationContext());		   
		if(map!=null)
		{
			for(LocationPoint curLocPoint:UserApplication.pointList)
			{
				map.addMarker(new MarkerOptions().position(new LatLng(curLocPoint.latitude, curLocPoint.longitude)).title(curLocPoint.label));
				map.addCircle(new CircleOptions()
				.center(new LatLng(curLocPoint.latitude, curLocPoint.longitude))
		        .radius(curLocPoint.radius)
		        .strokeColor(Color.BLUE)
		    	.strokeWidth(2)
		        .fillColor(0x330000ff));
			}
		}
	}
}
