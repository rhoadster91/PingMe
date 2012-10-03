package beit.skn.pingmeuser;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import beit.skn.classes.PushableMessage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class UserAuthenticationActivity extends Activity
{
	private Button login = null;
	private EditText txt1 = null;
	private String ipaddress = "192.168.0.101";
	private static final int USER_PORT_NUMBER = 9975;	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);       
		login = (Button)findViewById(R.id.button1);
		txt1 = (EditText)findViewById(R.id.txtUser);
		login.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{				
				PushableMessage m = new PushableMessage(txt1.getText().toString(), "hello");
				Socket socket = null;
				try 
				{
					socket = new Socket(ipaddress, USER_PORT_NUMBER);
					UserTalker.setSocket(socket);
					UserTalker.pushMessage(m);					
					m = UserTalker.readMessage();
					if(m.getControl().contentEquals("authentic"))
					{
						Intent showDashboard = new Intent(UserAuthenticationActivity.this, DashboardActivity.class);						
						UserAuthenticationActivity.this.startActivity(showDashboard);
						UserTalker.setUname(txt1.getText().toString());
						finish();
					}
				}
				catch (UnknownHostException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}				
			}
		});        
    }
}