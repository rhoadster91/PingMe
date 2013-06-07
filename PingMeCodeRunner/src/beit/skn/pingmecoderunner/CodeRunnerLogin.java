package beit.skn.pingmecoderunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
				new File(textUsername.getText()).mkdir();
				CodeRunnerCommunicator.prepareCommunicatorService(ipaddress, textUsername.getText(), new String(textPassword.getPassword()));				
				try
				{
					FileInputStream fis = new FileInputStream(textUsername.getText() + "/hostname.txt");	
					Scanner sc = new Scanner(fis);
					CodeRunnerCommunicator.setHostname(sc.nextLine());
					CodeRunnerCommunicator.authenticate();
					new CodeRunnerUI();
					sc.close();
				}
				catch(FileNotFoundException fnfe)
				{
					new HostNameDialog();
				} 									
				setVisible(false);
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
