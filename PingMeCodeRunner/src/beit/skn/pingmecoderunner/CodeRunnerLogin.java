package beit.skn.pingmecoderunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class CodeRunnerLogin extends JDialog
{	
	private static final long serialVersionUID = 3799152690595971824L;
	JLabel label1 = new JLabel("Username");
	JLabel label2 = new JLabel("Password");
	JButton cmdOK = new JButton("OK");
	JButton cmdCancel = new JButton("Cancel");
	JTextField textUsername = new JTextField();
	JPasswordField textPassword = new JPasswordField();	
	static String ipaddress;
	
	CodeRunnerLogin()
	{
		setLayout(null);
		setTitle("Log in to CodeRunner");
		setBounds(300, 300, 300, 200);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		label1.setBounds(10, 10, 80, 50);
		label2.setBounds(10, 60, 80, 50);
		add(label1);
		add(label2);
		textUsername.setBounds(90, 20, 160, 30);
		textPassword.setBounds(90, 70, 160, 30);		
		add(textUsername);
		add(textPassword);
		cmdOK.setBounds(90, 110, 75, 40);
		cmdCancel.setBounds(175, 110, 75, 40);				
		add(cmdOK);	
		cmdOK.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				new CodeRunnerCommunicator(ipaddress, textUsername.getText(), new String(textPassword.getPassword()));	
			}
			
		});
		add(cmdCancel);	
		setVisible(true);
		
	}
	public static void main(String[] args) 
	{
		ipaddress = args[0];
		new CodeRunnerLogin();
	}

}
