import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	public static int getLocation(String url){
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
	public static int addLocation(String url, String name, String description, String fulltext){
		try {
			
			pst = connection.prepareStatement("SELECT * FROM locations");
	        rs = pst.executeQuery();
	        
	        while (rs.next()) 
	        	if(rs.getString(4).equals(url)){
	        		pst = connection.prepareStatement("UPDATE locations SET description = ? WHERE webId = ?");
	        		pst.setString(1, description);
	        		pst.setInt(2, rs.getInt(1));
	        		pst.execute();
	        		return rs.getInt(1);
	        	}
			
			pst = connection.prepareStatement("INSERT INTO locations (name, description, url, siteFullText)"
					+ " values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pst.setString(1, name);
			pst.setString(2, description);
			pst.setString(3, url);
			pst.setString(4, fulltext);
			
			pst.executeUpdate();
			
			ResultSet rs = pst.getGeneratedKeys();
			rs.next();
			
			return rs.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static int addKeyword(String keyword){
		try {
			pst = connection.prepareStatement("SELECT * FROM keywords");
	        rs = pst.executeQuery();
	        
	        while (rs.next()) 
	        	if(rs.getString(2).equals(keyword.toLowerCase()))
	        		return rs.getInt(1);
	        
	        if(keyword.length() >= 15){
	        	System.out.println(keyword + " too long for keyword column");
	        	return -1;
	        }
	        
			pst = connection.prepareStatement("INSERT INTO keywords (word) "+ "values (?)", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1,keyword.toLowerCase());
			pst.executeUpdate();
			
			ResultSet rs = pst.getGeneratedKeys();
			rs.next();
			
			return rs.getInt(1);
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static void addData(ArrayList<Data> data, int pageID){
		
		loop:
		for(Data d : data){
			int keyID = addKeyword(d.word);
			
			// skips the word if it wasn't in the database
			if(keyID == -1)
				continue;
			
			try {		      
				
				pst = connection.prepareStatement("SELECT * FROM siteKeywords");
		        rs = pst.executeQuery();
		        
		        while (rs.next()) 
		        	if(rs.getInt(1) == pageID && rs.getInt(2) == keyID){
		        		pst = connection.prepareStatement("UPDATE siteKeywords SET weight = ? WHERE webId = ? AND keyID = ?");
		        		pst.setInt(1, d.weight);
		        		pst.setInt(2, pageID);
		        		pst.setInt(3, keyID);
		        		pst.execute();
		        		continue loop;
		        	}
				
				pst = connection.prepareStatement("INSERT INTO siteKeywords (webId, keyId, weight)"
						+ " values (?, ?, ?)");
				pst.setInt(1,pageID);
				pst.setInt(2,keyID);
				pst.setInt(3, d.weight);
				pst.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void clearDataForPage(String url){
		
		
		
	}
	
	
	public static void clearData(){
		
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
	
	public static void printKeywordDatabase(){
		try {
			pst = connection.prepareStatement("SELECT * FROM keywords");
			rs = pst.executeQuery();

			while (rs.next()) {
				System.out.println();
				System.out.print(rs.getInt(1) + " : ");
				System.out.print(rs.getString(2) + " : ");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printDataDatabase(){
		try {
			pst = connection.prepareStatement("SELECT * FROM siteKeywords");
			rs = pst.executeQuery();

			while (rs.next()) {
				System.out.println();
				System.out.print(rs.getInt(1) + " : ");
				System.out.print(rs.getInt(2) + " : ");
				System.out.print(rs.getInt(3) + " : ");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
