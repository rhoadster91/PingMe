package beit.skn.pingmeuser;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PingTextActivity extends Activity
{
	Button sendMessage = null;
	EditText aname, atext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pingtext);
		aname = (EditText)findViewById(R.id.txtDest);				
		atext = (EditText)findViewById(R.id.txtContent);
		String loc = (String) getIntent().getCharSequenceExtra("Loc");
		atext.setText(loc);
		sendMessage = (Button)findViewById(R.id.pushToClient);
		sendMessage.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Intent sendMessageToService = new Intent();
				sendMessageToService.setAction(UserApplication.INTENT_TO_SERVICE);
				PushableMessage m = new PushableMessage(UserApplication.uname, PushableMessage.CONTROL_PING_TEXT);
				m.setDestination(aname.getText().toString());			
				m.setMessageContent(atext.getText().toString());
				sendMessageToService.putExtra("pushablemessage", m);
				sendBroadcast(sendMessageToService);
			}			
		});
	}

}
