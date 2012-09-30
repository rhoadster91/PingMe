package beit.skn.classes;

import java.io.Serializable;

public class Message implements Serializable
{
	private static final long serialVersionUID = 4345780394134979995L;
	private String senderID = null;
	private Object content = null;
	private String control = null;
	
	public Message()
	{
		senderID = "";
		control = "";		
	}
	
	public Message(String sID, String ctrl)
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
	
	public void setMessageContent(Object c)
	{
		this.content = c;
	}
	
	public Object getMessageContent()
	{
		return content;
	}
	
}
