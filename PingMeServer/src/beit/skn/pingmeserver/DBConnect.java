package beit.skn.pingmeserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect 
{
	public static void initializeDatabase()
	{
		
	}
	
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
	
	public static String getEmergencyNumberFromDatabase(String userID)
	{
		try 
		{
			ResultSet rs = con.prepareStatement("select ice from users where uname = '" + userID + "'").executeQuery();	
			rs.first();
			String emergencyNumber = rs.getString(1);
			return emergencyNumber;
				
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	
	public static void createNewUser(String signupinfo)
	{
		System.out.println("Signing up new user...");
		String uname = signupinfo.split("&&&")[0];
		String password = signupinfo.split("&&&")[1];
		String fullname = signupinfo.split("&&&")[2];
		String email = signupinfo.split("&&&")[3];
		String emergencyNumber = signupinfo.split("&&&")[4];
		Statement st;
		try 
		{
			st = con.createStatement();
			String query = new String("insert into users(uname, password, fullname, email, ice, blacklisted) values('" + uname + "', '" + password + "', '" + fullname + "', '" + email + "', '" + emergencyNumber + "', 0)");
			System.out.println(query);
			st.execute(query);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
