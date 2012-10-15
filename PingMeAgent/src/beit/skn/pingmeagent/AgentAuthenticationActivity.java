package beit.skn.pingmeagent;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import beit.skn.classes.*;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AgentAuthenticationActivity extends Activity
{
	private Button login = null;
	private EditText txt1 = null;
	private static SharedPreferences sharedPref;
	private static SharedPreferences.Editor editorPref;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);		        
    	login = (Button)findViewById(R.id.button1);
		txt1 = (EditText)findViewById(R.id.txtUser);
		login.setOnClickListener
		(
			new OnClickListener()
			{
				public void onClick(View v) 
				{	
				
				
				}
			}
		);
	}
}