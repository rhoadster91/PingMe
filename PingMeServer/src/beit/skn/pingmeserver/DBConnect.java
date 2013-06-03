package beit.skn.pingmeserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect 
{
	private static Connection con;
	static
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = null;
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pingmedb","root", "root");
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void performInsert()
	{
		try 
		{
			Statement st = con.createStatement();
			st.execute("insert into users values('girish', 'fireworks')");
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static boolean isAuthentic(String name, String password, String table)
	{
		try 
		{
			ResultSet rs = con.prepareStatement("select * from " + table + " where uname = '" + name + "' and password = '" + password + "'").executeQuery();			
			if(!rs.next())
				return false;
			else
				return true;
				
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return false;		
	}
	
	public static String getAgentClassFromDatabase(String agentID)
	{
		try 
		{
			ResultSet rs = con.prepareStatement("select class from agents where uname = '" + agentID + "'").executeQuery();	
			rs.first();
			String agentClass = rs.getString(1);
			return agentClass;
				
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
