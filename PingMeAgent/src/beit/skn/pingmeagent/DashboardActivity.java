package beit.skn.pingmeagent;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class DashboardActivity extends Activity 
{
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		Toast.makeText(getBaseContext(), "Authenticated and registered on server", Toast.LENGTH_LONG).show();
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);		
		new AgentListener(this).start();
	}
	
	PushableMessage listenerMsg;
	public void listenToStream(PushableMessage m) 
    {        
		listenerMsg = m;		
        mHandler.post(new Runnable()
        {
            public void run() 
            {
            	AlertDialog.Builder alertTest = new AlertDialog.Builder(DashboardActivity.this);
                alertTest.setTitle("You have a new request");
                alertTest.setMessage((String)listenerMsg.getMessageContent()).create();
                alertTest.setPositiveButton("Okay", null);
                alertTest.show();
            }
        });
    }
}
