package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SplashBoxAdapter extends ArrayAdapter<Object> 
{
	private final Context context;
	private final Object[] values;
	private int count;
	
	public SplashBoxAdapter(Context context, Object[] objects)
	{		
		super(context, R.layout.splashrow, objects);
		this.context = context;
		this.values = objects;
		count = UserApplication.splashBox.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.splashrow, parent, false);
	    TextView sender = (TextView) rowView.findViewById(R.id.splashSender);
	    PushableMessage m = (PushableMessage)values[count - position - 1];
	    sender.setText(m.getSender());
	    sender.setTextColor(getContext().getResources().getColor(R.color.black));
	    TextView content = (TextView) rowView.findViewById(R.id.splashContent);
	    content.setText((String)m.getMessageContent());	    	    
	    return rowView;
	  }
}
