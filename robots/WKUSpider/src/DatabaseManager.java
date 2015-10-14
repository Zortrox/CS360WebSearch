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
			
			System.out.println("Creating connection...");
			connection = DriverManager.getConnection(url,user,pass);
			System.out.println("Connection established!");
			
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
			System.out.println("Connection closed");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Checks if the database already has this location stored
	 * @param url - the URL to check
	 * @return the locaion of the URL
	 */
	public static int hasLocation(String url){
		try {
			pst = connection.prepareStatement("SELECT * FROM locations");
	        rs = pst.executeQuery();
	        
	        while (rs.next()) {
	        	if(rs.getString(4).equals(url))
	        		return rs.getInt(1);
	        }
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	//(webId, name, description, url, hash)
	/**
	 * Adds a new location to the database
	 * @param url - the URL of the page
	 * @param name - the Title of the page
	 * @param description - A description of the page
	 * @param hash - a has of the page
	 * @return the index of the location
	 */
	public static int addLocation(String url, String name, String description, String hash){
		try {
			
			pst = connection.prepareStatement("INSERT INTO locations (webId, name, description, url, hash)"
					+ " values (?, ?, ?, ?, ?)");
			
			pst.setInt(1, 3);
			pst.setString(2,name);
			pst.setString(3, description);
			pst.setString(4, url);
			pst.setString(5, hash);
			
			pst.execute();
			
			return 3;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
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
