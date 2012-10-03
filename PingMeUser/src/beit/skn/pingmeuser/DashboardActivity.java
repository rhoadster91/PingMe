package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DashboardActivity extends Activity 
{
	private EditText txt1 = null;
	private EditText txt2 = null;
	private static UserListener userListener = null;
	
	private Handler mHandler = new Handler();
	private Button sendMessage = null; 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);
		if(userListener==null)
		{
			userListener = new UserListener(this);
			userListener.setPriority(Thread.MAX_PRIORITY);
			userListener.start();
		}
		txt1 = (EditText)findViewById(R.id.txtDest);
		txt2 = (EditText)findViewById(R.id.txtContent);
		
		sendMessage = (Button)findViewById(R.id.pushToClient);
		sendMessage.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				PushableMessage m = new PushableMessage(UserTalker.getUname(), "push");
				m.setDestination(txt1.getText().toString());
				m.setMessageContent((String)txt2.getText().toString());
				UserTalker.pushMessage(m);
			}			
		});		
	}
	
	PushableMessage listenerMsg;
	public void listenToStream(PushableMessage m) 
    {        
		listenerMsg = m;		
        mHandler.post(new Runnable()
        {
            public void run() 
            {
            	if(listenerMsg.getControl().contentEquals("fail"))
            	{
	            	AlertDialog.Builder alertTest = new AlertDialog.Builder(DashboardActivity.this);
	                alertTest.setTitle("Sorry");
	                alertTest.setMessage((String)listenerMsg.getMessageContent()).create();
	                alertTest.setPositiveButton("Okay", null);
	                alertTest.show();
            	}
            }
        });
    }

}
