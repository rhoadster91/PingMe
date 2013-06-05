package beit.skn.pingmeuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DeviceListAdapter extends ArrayAdapter<Object> 
{

	private final Context context;
	private final Object[] values;	
	String s;
	
	public DeviceListAdapter(Context context, Object[] objects)
	{		
		super(context, R.layout.splashrow, objects);
		this.context = context;
		this.values = objects;
		UserApplication.readDevicelistFromFile(context);
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.devicerow, parent, false);
	    TextView sender = (TextView) rowView.findViewById(R.id.deviceName);
	    s = (String)values[position];
	    sender.setText(s);
	    sender.setTextColor(getContext().getResources().getColor(R.color.black));	    
	    return rowView;
	  }

}
