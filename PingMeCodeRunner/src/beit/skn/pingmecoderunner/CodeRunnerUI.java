package beit.skn.pingmecoderunner;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

public class CodeRunnerUI extends JFrame 
{	
	private static final long serialVersionUID = 2780122513437044602L;
	private static Image image;
	
	CodeRunnerUI()
	{
		setLayout(null);
		setBounds(100, 100, 320, 320);
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
		try 
		{
			FileInputStream fis = new FileInputStream("hostname.txt");
			Scanner sc = new Scanner(fis);
			String hostname = sc.nextLine();
			
			ByteArrayOutputStream out = QRCode.from(hostname + "@" + CodeRunnerCommunicator.uname).to(ImageType.PNG).stream();
	
			FileOutputStream fout = new FileOutputStream(new File("hostqrcode.jpg"));
			fout.write(out.toByteArray());
			fout.flush();
			fout.close();
		}
		catch (FileNotFoundException e) 
		{
			
		} 
		catch (IOException e) 
		{
			
		}
		JLabel label1 = new JLabel("Scan the QR code below to pair with your phone.");
		label1.setBounds(10, 0, 300, 30);
		JLabel label2 = new JLabel("CodeRunner is running and awaiting requests.");
		label2.setBounds(10, 240, 300, 30);
		add(label1);	
		add(label2);		
		setVisible(true);
		
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		image = getToolkit().getImage("hostqrcode.jpg");
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(image, 0);
		g.drawImage(image, 50, 60, 200, 200, this);
	}
	
	public static void main(String[] args) 
	{
		new CodeRunnerUI();

	}

}
