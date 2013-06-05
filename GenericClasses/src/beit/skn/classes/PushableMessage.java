package beit.skn.classes;

import java.io.Serializable;

public class PushableMessage implements Serializable
{
	private static final long serialVersionUID = 4345780394134979995L;
	private String senderID = null;
	private String destID = null;
	private Object content = null;
	private String control = null;
	private boolean isEncrypted = false;
	
	public static final String CONTROL_HELLO = "Hello";
	public static final String CONTROL_LOGIN = "Login";
	public static final String CONTROL_LOGOUT = "Logout";
	public static final String CONTROL_PUSH = "Push";
	public static final String CONTROL_PING_TEXT = "Text Ping Message";
	public static final String CONTROL_AUTHENTIC = "Authentic";
	public static final String CONTROL_ABORT = "Abort";
	public static final String CONTROL_PING_IMAGE = "Image Ping Message";
	public static final String CONTROL_PING_CODE = "Code Ping Message";
	
	public PushableMessage()
	{
		senderID = "";
		control = "";		
	}
	
	public PushableMessage(String sID, String ctrl)
	{
		senderID = sID;
		control = ctrl;
	}
	
	public String getControl() 
	{
		return control;
	}

	public void setControl(String control) 
	{
		this.control = control;
	}

	public void setSender(String s)
	{
		this.senderID = s;
	}
	
	public String getSender()
	{
		return senderID;
	}
	
	public void setDestination(String s)
	{
		this.destID = s;
	}
	
	public String getDestination()
	{
		return destID;
	}
	
	
	public void setMessageContent(Object c)
	{
		this.content = c;
	}
	
	public Object getMessageContent()
	{
		return content;
	}

	public boolean isEncrypted() 
	{
		return isEncrypted;
	}

	public void setEncrypted(boolean isEncrypted) 
	{
		this.isEncrypted = isEncrypted;
	}
	
}
