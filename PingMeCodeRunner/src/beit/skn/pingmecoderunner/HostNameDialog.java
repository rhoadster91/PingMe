package beit.skn.pingmecoderunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class HostNameDialog extends JDialog 
{
	private static final long serialVersionUID = 4117951475055326875L;
	JLabel label1 = new JLabel("Please name this computer.");
	JLabel label2 = new JLabel("You will be asked to do this only once.");
	
	JButton cmdOK = new JButton("OK");
	JTextField textHostname = new JTextField();
	
	HostNameDialog()
	{
		setLayout(null);
		setTitle("Name this computer");
		setBounds(300, 300, 300, 200);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		label1.setBounds(10, 10, 190, 50);
		add(label1);
		label2.setBounds(10, 30, 250, 50);
		add(label2);
		
		textHostname.setBounds(10, 70, 250, 30);
		add(textHostname);
		cmdOK.setBounds(90, 110, 75, 40);
		add(cmdOK);	
		cmdOK.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(!textHostname.getText().trim().contentEquals(""))
				{
					File hostname = new File("hostname.txt");
					try 
					{
						FileWriter fw = new FileWriter(hostname);
						fw.write(textHostname.getText());
						fw.close();
						setVisible(false);
						new CodeRunnerUI();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					
				}
			}
			
		});
		setVisible(true);
		
	}
	
	public static void main(String[] args) 
	{
		new HostNameDialog();

	}
	

}
