package beit.skn.pingmecoderunner;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class CodeRunnerUI extends JFrame 
{	
	private static final long serialVersionUID = 2780122513437044602L;

	CodeRunnerUI()
	{
		setBounds(100, 100, 500, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//JLabel label = new JLabel("Please enter");
		
	}
	
	public static void main(String[] args) 
	{
		new CodeRunnerUI();

	}

}
