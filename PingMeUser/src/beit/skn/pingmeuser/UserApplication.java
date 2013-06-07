package beit.skn.pingmeuser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import beit.skn.classes.PushableMessage;
import android.app.Application;
import android.content.Context;


public class UserApplication extends Application
{
	protected static int notifCount = 0;
	protected static String errorMessage = "";
	protected static boolean isAuthentic = false;	
	protected static final String INTENT_TO_SERVICE = "PingMeUserIntentToService";
	protected static final String INTENT_TO_ACTIVITY = "PingMeUserIntentToActivity";
	protected static final String LOCATION_UPDATE = "PingMeUserLocationUpdated";
	protected static final String NOTIFICATION_CALL = "PingMeUserNotificationCall";
	protected static final String TERMINATE_LOCATION_SERVICE = "PingMeAgentTerminateLocationService";	
	protected static final String SMS_SENT = "PingMeUserSMSSent";
	protected static final String SMS_DELIVERED = "PingMeUserSMSDelivered";	
	protected static final int USER_PORT_NUMBER = 9975;
	protected static final String LAN_IP_ADDRESS = "192.168.0.102";
	protected static final String WAN_IP_ADDRESS = "rhoadster91.no-ip.org";
	
	private static final String LOCAL_FILE_FOR_SPLASH_BOX = "splashbox_";
	private static final String LOCAL_FILE_FOR_DEVICES = "devices_";
	
	protected static String uname = "";
	protected static String upass = "";
	protected static String IP_ADDRESS;
	protected static ArrayList<PushableMessage> splashBox = new ArrayList<PushableMessage>();
	protected static ArrayList<String> deviceList = new ArrayList<String>();
	
	protected static void writeSplashBoxToFile(Context context) 
	{
        ObjectOutputStream objectOut = null;
        try {

            FileOutputStream fileOut = context.openFileOutput(LOCAL_FILE_FOR_SPLASH_BOX + uname, Context.MODE_PRIVATE);
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
	
	protected static void writeDeviceListToFile(Context context) 
	{
        ObjectOutputStream objectOut = null;
        try {

            FileOutputStream fileOut = context.openFileOutput(LOCAL_FILE_FOR_DEVICES + uname, Context.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(deviceList);
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
	public static void readSplashboxFromFile(Context context) 
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
        else
        	splashBox = new ArrayList<PushableMessage>();
        
    }

    @SuppressWarnings("unchecked")
	public static void readDevicelistFromFile(Context context) 
    {
        ObjectInputStream objectIn = null;
        Object object = null;
        try 
        {
            FileInputStream fileIn = context.getApplicationContext().openFileInput(LOCAL_FILE_FOR_DEVICES + uname);
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
        	deviceList = (ArrayList<String>) object;
        else
        	deviceList = new ArrayList<String>();
        
    }
}
