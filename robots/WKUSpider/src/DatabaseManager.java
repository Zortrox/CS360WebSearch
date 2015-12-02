import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DatabaseManager {
	//ر للطلبة بيئة دراس
	static Connection connection;
	private static final String url = "jdbc:mysql://127.0.0.1:3306/webSearchEngine";
	private static final String user = "crawl";
	private static final String pass = "webCrawl!";
	private static ArrayList<String> visited = new ArrayList<String>();
	
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
	
	/**
	 * Closes the connection
	 */
	public static void Exit(){
		try {
			connection.close();
			System.out.println("Connection closed");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the location index of a URL
	 * @param url - the url of the page
	 * @return the index of the location
	 */
	public static int getLocation(String url){
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT webId FROM locations WHERE url = \""+url+"\"");
	        ResultSet rs = pst.executeQuery();
	        
	        int index = -1;
	        
	        while(rs.next())
	        	index = rs.getInt(1);

	        pst.close();
	        rs.close();
	        
	        return index;
		} catch (SQLException e) {
			e.printStackTrace();
		}

        return -1;
	}
	
	/**
	 * Adds a new location to the database
	 * @param url - the URL of the page
	 * @param name - the Title of the page
	 * @param description - A description of the page
	 * @param fulltext - all the text on the page
	 * @return the index of the location
	 */
	public static int addLocation(String url, String name, String description, String fulltext){
		try {

			PreparedStatement pst = connection.prepareStatement("SELECT webId FROM locations WHERE url = \""+url+"\"");
	        ResultSet rs = pst.executeQuery();
	        
	        int index = -1;
	        
	        while(rs.next())
	        	index = rs.getInt(1);
	        
	        if(fulltext.length() > 15000000){
	        	fulltext = fulltext.substring(0,15000);
	        	System.out.println(url + " has too long full text");
	        }
	        
			if (index != -1) {
				pst.close();
				pst = connection.prepareStatement("UPDATE locations SET siteFullText = ?, description = ? WHERE webId = ?");
				pst.setString(1, fulltext);
				pst.setString(2, description);
				pst.setInt(3, index);
				pst.execute();
				
		        pst.close();
		        rs.close();
				
				return index;
			} else {

				PreparedStatement pst2 = connection.prepareStatement("INSERT INTO locations (name, description, url, siteFullText)"
						+ " values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

				pst2.setString(1, name);
				pst2.setString(2, description);
				pst2.setString(3, url);
				pst2.setString(4, fulltext);

				pst2.executeUpdate();

				ResultSet rs2 = pst2.getGeneratedKeys();
				rs2.next();
				
				int result = rs2.getInt(1);
				
				pst2.close();
				rs2.close();

		        pst.close();
		        rs.close();

				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Gets the keyword index in the database
	 * @param keyword - the keyword as a string case-insensitive
	 * @return the index of the keyword
	 */
	public static int addKeyword(String keyword){
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT keyId FROM keywords WHERE word = \""+keyword+"\"");
	        ResultSet rs = pst.executeQuery();
	        
	        int index = -1;
	        
	        while(rs.next())
	        	index = rs.getInt(1);
	        
	        if(index != -1)
	        	return index;
	        else
	        {
				pst = connection.prepareStatement("INSERT INTO keywords (word) " + "values (?)",Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, keyword.toLowerCase());
				pst.executeUpdate();

				ResultSet rs2 = pst.getGeneratedKeys();
				
				int ni = -1;
				
		        while(rs2.next())
		        	ni = rs2.getInt(1);
		        
		        pst.close();
		        rs.close();
		        rs2.close();

				return ni;
			}
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Adds a Data object to the database for a page.
	 * @param data - An array list of Data objects
	 * @param pageID - The id of the page index. This is returned from the addLocation() function
	 */
	public static void addData(ArrayList<Data> data, int pageID){
		
		loop:
		for(Data d : data){
			int keyID = addKeyword(d.word);
			
			// skips the word if it wasn't in the database
			if(keyID == -1)
				continue;
			
			try {		      
				
				PreparedStatement pst = connection.prepareStatement("SELECT * FROM siteKeywords WHERE webID = " + pageID + " AND keyId = " + keyID);
		        ResultSet rs = pst.executeQuery();
		        
		        // update the weight if the keyid and pageid are found
		        while (rs.next())
		        {
					pst = connection
							.prepareStatement("UPDATE siteKeywords SET weight = ? WHERE webId = ? AND keyId = ?");
					pst.setInt(1, d.weight);
					pst.setInt(2, pageID);
					pst.setInt(3, keyID);
					pst.execute();
					
					rs.close();
					pst.close();
					
					continue loop;
				}
				try{
					PreparedStatement pst2 = connection
							.prepareStatement("INSERT INTO siteKeywords (webId, keyId, weight)"
									+ " values (?, ?, ?)");
					pst2.setInt(1, pageID);
					pst2.setInt(2, keyID);
					pst2.setInt(3, d.weight);
					pst2.execute();
					pst2.close();
				} catch (MySQLIntegrityConstraintViolationException e){
					System.out.println("Somehow, there is a duplicate");
				}
				
		        pst.close();
		        rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Gets an array of the in-use WebServers
	 * @return - string array of in-use webservers
	 */
	public static ArrayList<String> getWebServerList(){
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT * FROM webServers");
			ResultSet rs = pst.executeQuery();
			
			ArrayList<String> list = new ArrayList<String>();

			while (rs.next()) {
				String url = rs.getString(1);
				url = url.substring(0,url.indexOf(":"));
				if(rs.getBoolean(2) && !list.contains(url))
					list.add(url);		
			}
			
	        pst.close();
	        rs.close();
	        
	        return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void visit(String u){
		visited.add(u);
	}
	
	public static boolean visited(String u){
		return visited.contains(u);
	}
	
	public static void printLocationDatabase(){
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT * FROM locations");
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				System.out.println();
				System.out.print(rs.getInt(1) + " : ");
				System.out.print(rs.getString(2) + " : ");
				System.out.print(rs.getString(3) + " : ");
				System.out.print(rs.getString(4) + " : ");
				System.out.print(rs.getString(5) + " : ");
			}
	        pst.close();
	        rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printKeywordDatabase(){
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT * FROM keywords");
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				System.out.println();
				System.out.print(rs.getInt(1) + " : ");
				System.out.print(rs.getString(2) + " : ");
			}
	        pst.close();
	        rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void printDataDatabase(){
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT * FROM siteKeywords");
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				System.out.println();
				System.out.print(rs.getInt(1) + " : ");
				System.out.print(rs.getInt(2) + " : ");
				System.out.print(rs.getInt(3) + " : ");
			}
			
	        pst.close();
	        rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void printWebServers(){
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT * FROM webServers");
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				System.out.println();
				System.out.print(rs.getString(1) + " : ");
				System.out.print(rs.getBoolean(2) + " : ");
			}
			
	        pst.close();
	        rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
