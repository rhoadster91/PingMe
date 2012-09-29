package beit.skn.classes;

public class Message 
{
	String senderID = null;
	Object content = null;
	
	public void setSender(String s)
	{
		senderID = s;
	}
	
	public void setMessageContent(Object c)
	{
		content = c;
	}
	
	public Object getMessageContent()
	{
		return content;
	}
}
