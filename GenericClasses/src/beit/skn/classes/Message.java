package beit.skn.classes;

public class Message 
{
	String senderID = null;
	Object content = null;
	String control = null;
	
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
	
	public void setMessageContent(Object c)
	{
		this.content = c;
	}
	
	public Object getMessageContent()
	{
		return content;
	}
	
}
