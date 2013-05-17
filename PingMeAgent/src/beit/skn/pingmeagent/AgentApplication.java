package beit.skn.pingmeagent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import beit.skn.classes.PushableMessage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class AgentApplication extends Application
{
	protected static int notifCount = 0;
	protected static String errorMessage;
	protected static boolean isAuthentic = false;
	
	protected static final String INTENT_TO_SERVICE = "PingMeIntentToService";
	protected static final String INTENT_TO_ACTIVITY = "PingMeIntentToActivity";
	protected static final String NOTIFICATION_CALL = "PingMeNotificationCall";
	protected static final int AGENT_PORT_NUMBER = 9974;
	protected static final String IP_ADDRESS = "10.0.2.2";//"117.195.37.84"; //"117.195.37.84";
	private static final String LOCAL_FILE_FOR_SPLASH_BOX = "splashbox_";
	
	protected static String uname = "";
	protected static String upass = "fakepassword";
	
	protected static ArrayList<PushableMessage> splashBox = new ArrayList<PushableMessage>();
	
	protected static void writeSplashBoxToFile(Context context) 
	{
        ObjectOutputStream objectOut = null;
        try 
        {
            FileOutputStream fileOut = context.openFileOutput(LOCAL_FILE_FOR_SPLASH_BOX + uname, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(splashBox);
            fileOut.getFD().sync();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (objectOut != null) 
            {                
            	try 
            	{
                    objectOut.close();
                } 
            	catch (IOException e)
                {
                    
                }
            }
        }
    }

   
    @SuppressWarnings("unchecked")
	public static void readSplashBoxFromFile(Context context) 
    {
        ObjectInputStream objectIn = null;
        Object object = null;
        try 
        {
            FileInputStream fileIn = context.getApplicationContext().openFileInput(LOCAL_FILE_FOR_SPLASH_BOX + uname);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } 
        catch (FileNotFoundException e)
        {
            
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } 
        finally
        {
            if (objectIn != null)
            {
                try 
                {
                    objectIn.close();
                } 
                catch (IOException e)
                {
                    
                }
            }
        }
        if(object!=null)
        	splashBox = (ArrayList<PushableMessage>) object;
    }


	

}
