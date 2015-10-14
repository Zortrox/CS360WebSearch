import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseManager {

	static Connection connection;
	static PreparedStatement pst;
	static ResultSet rs;
	private static final String url = "jdbc:mysql://127.0.0.1:3306/webSearchEngine";
	private static final String user = "crawl";
	private static final String pass = "webCrawl!";
	
	/**
	 * sets up the driver and readies the functions needed
	 */
	public static boolean Initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			System.out.println("Starting connection test...");
			connection = DriverManager.getConnection(url,user,pass);
			System.out.println("Connection Established! Ready to test print a database...");
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void Exit(){
		try {
			connection.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if the database already has this location stored
	 * @param url - the URL to check
	 * @return true is the database contains this location and false otherwise
	 */
	public static boolean hasLocation(String url){
		try {
			pst = connection.prepareStatement("SELECT * FROM locations");
	        rs = pst.executeQuery();
	        
	        while (rs.next()) {
	        	if(rs.getString(4).equals(url))
	        		return true;
	        }
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//(webId, name, description, url, hash)
	public static boolean addLocation(String url, String name, String description, String hash){
		try {
			
			pst = connection.prepareStatement("insert into locations (webId, name, description, url, hash) "
					+ "values ("+3+", "+name+", "+description+", "+url+", "+hash+")");
			return pst.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static void addKeyWords(ArrayList<Data> data){
		
	}
	
	public static void clearKeywordsForPage(String url){
		
	}
	
	public static void printLocationDatabase(){
		try {
			pst = connection.prepareStatement("SELECT * FROM locations");
			rs = pst.executeQuery();

			while (rs.next()) {
				System.out.println();
				System.out.print(rs.getInt(1) + " : ");
				System.out.print(rs.getString(2) + " : ");
				System.out.print(rs.getString(3) + " : ");
				System.out.print(rs.getString(4) + " : ");
				System.out.print(rs.getString(5) + " : ");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}
