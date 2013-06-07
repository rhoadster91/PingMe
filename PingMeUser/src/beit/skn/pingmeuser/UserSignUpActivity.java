package beit.skn.pingmeuser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import beit.skn.classes.PushableMessage;
import beit.skn.classes.RSAEncryptorClass;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserSignUpActivity extends Activity 
{
	private Button buttonSignup;
	private Button buttonClear;
	private EditText textUsername;
	private EditText textPassword;
	private EditText textConfirmPassword;
	private EditText textFullname;
	private EditText textEmail;
	private EditText textEmergencyNumber;
	ObjectOutputStream objOut = null;
	ObjectInputStream objIn = null;	
	private int serverPublicKey, serverModulus;
	private boolean successfulSignUp = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		buttonSignup = (Button)findViewById(R.id.bSignUp);
		buttonClear = (Button)findViewById(R.id.bClear);
		textUsername = (EditText)findViewById(R.id.textNewUser);
		textPassword = (EditText)findViewById(R.id.textNewPassword);
		textConfirmPassword = (EditText)findViewById(R.id.textNewPasswordConfirm);
		textFullname = (EditText)findViewById(R.id.textNewFullName);
		textEmail = (EditText)findViewById(R.id.textNewEmail);
		textEmergencyNumber = (EditText)findViewById(R.id.textNewEmergency);
		
		buttonSignup.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{		
				String username = textUsername.getText().toString();
				String password = textPassword.getText().toString();
				String confirmPassword= textConfirmPassword.getText().toString();				
				String emailID= textEmail.getText().toString();
				String emergencyNumber = textEmergencyNumber.getText().toString();

				if((!password.contentEquals(confirmPassword)))
					Toast.makeText(getApplicationContext(), "Password and confirm password do not match.", Toast.LENGTH_LONG).show();
				else if(!username.split(" ")[0].contentEquals(username))
					Toast.makeText(getApplicationContext(), "User name should not have spaces in between.", Toast.LENGTH_LONG).show();
				else if(!emailID.contains("@") || !emailID.contains("."))
					Toast.makeText(getApplicationContext(), "Malformed email ID.", Toast.LENGTH_LONG).show();
				else if(emergencyNumber.length()!=10)
					Toast.makeText(getApplicationContext(), "Emergency number should be 10 digit long.", Toast.LENGTH_LONG).show();
				else
					new LoginTask().execute();				
			}
			
		});
		
		buttonClear.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{		
				textUsername.setText("");
				textPassword.setText("");
				textConfirmPassword.setText("");
				textFullname.setText("");
				textEmail.setText("");
				textEmergencyNumber.setText("");
			}
			
		});
	}
	
	private class LoginTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPostExecute(Void result) 
		{
			if(successfulSignUp)
			{
				Toast.makeText(getApplicationContext(), "Sign up successful. Taking you to log in page.", Toast.LENGTH_LONG).show();
				finish();				
			}
			else
				Toast.makeText(getApplicationContext(), "Sign up unsuccessful. Please check your internet connectivity.", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			try 
			{
				String signupinfo = textUsername.getText().toString() + "&&&" +
									textPassword.getText().toString() + "&&&" +
									textFullname.getText().toString() + "&&&" +
									textEmail.getText().toString() + "&&&" +
									textEmergencyNumber.getText().toString();
				Socket socket = new Socket(UserApplication.IP_ADDRESS, UserApplication.USER_PORT_NUMBER);
				objIn = new ObjectInputStream(socket.getInputStream());					
				objOut = new ObjectOutputStream(socket.getOutputStream());				
				PushableMessage m = new PushableMessage("NEW USER", PushableMessage.CONTROL_HELLO);
				String publicKeySpecification = new String(RSAEncryptorClass.getPublicKey() + "," + RSAEncryptorClass.getModulus());
				m.setMessageContent(publicKeySpecification);
				objOut.writeObject(m);						
				objOut.flush(); 
				m = (PushableMessage)objIn.readObject();
				if(m.getControl().contentEquals(PushableMessage.CONTROL_HELLO))
				{
					serverPublicKey = Integer.parseInt(((String)m.getMessageContent()).split(",")[0]);
					serverModulus = Integer.parseInt(((String)m.getMessageContent()).split(",")[1]);
				}
				m = new PushableMessage("NEW USER", PushableMessage.CONTROL_PUSH);					
				m.setMessageContent(RSAEncryptorClass.encryptText(signupinfo, serverModulus, serverPublicKey));
				objOut.writeObject(m);						
				objOut.flush(); 
				m = (PushableMessage)objIn.readObject();
				if(m.getControl().contentEquals(PushableMessage.CONTROL_OK))
					successfulSignUp = true;
				socket.close();
			}
			catch (UnknownHostException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

}
