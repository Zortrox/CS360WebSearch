import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseManager {

	static Connection connection;
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
			connection = DriverManager.getConnection(url, user, pass);
			System.out.println("Connection Established!");
			
			DatabaseMetaData dbmd = connection.getMetaData();
			ResultSet ctlgs = dbmd.getCatalogs();
			while (ctlgs.next())
			    System.out.println("ctlgs="+ctlgs.getString(1));

			connection.close();
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean hasLocation(String url){
		return false;
	}
	
	//(webId, name, description, url, hash)
	public static void addLocation(String url, String name, String describtion, String hash){
		
	}
	
	public static void addKeyWords(ArrayList<Data> data){
		
	}
	
	public static void clearKeywordsForPage(String url){
		
	}
	

}
