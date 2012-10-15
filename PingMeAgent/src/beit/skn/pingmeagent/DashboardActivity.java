package beit.skn.pingmeagent;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;

public class DashboardActivity extends Activity 
{
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);	
		
	}	
	
}
