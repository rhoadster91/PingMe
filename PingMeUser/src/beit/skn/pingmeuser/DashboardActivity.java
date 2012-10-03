package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DashboardActivity extends Activity 
{
	private EditText txt1 = null;
	private EditText txt2 = null;
	
	private Button sendMessage = null; 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		Toast.makeText(getBaseContext(), "Authenticated and registered on server", Toast.LENGTH_LONG).show();
		setContentView(R.layout.dash);
		super.onCreate(savedInstanceState);
		
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

}
