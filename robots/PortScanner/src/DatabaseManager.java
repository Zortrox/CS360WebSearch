import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	
	
	public static int addIP(String ipAddress, boolean inUse) {
		try {
			pst = connection.prepareStatement("SELECT * FROM webServers");
	        rs = pst.executeQuery();
	        
	        while (rs.next()) {
	        	if(rs.getString(1).equals(ipAddress)){
	        		pst = connection.prepareStatement("UPDATE locations SET inUse = ? WHERE IP = ?");
	        		pst.setBoolean(1, inUse);
	        		pst.setString(2, ipAddress);
	        		pst.execute();
	        		//System.out.println("Updating current listening server. - " + ipAddress);
	        		return 1;
	        	}
	        }
			
			pst = connection.prepareStatement("INSERT INTO webServers (IP, inUse)"
					+ " VALUES (?, ?)");
			
			pst.setString(1, ipAddress);
			pst.setBoolean(2, inUse);
			
			pst.executeUpdate();
			//System.out.println("New listening server found! - " + ipAddress);
			return 0;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public static int removeIP(String ipAddress) {
		try {
			String smt = "SELECT * FROM webServers WHERE IP = ?";
			pst = connection.prepareStatement(smt);
			pst.setString(1, ipAddress);
	        rs = pst.executeQuery(smt);
	        
	        while (rs.next()) {
	        	if(rs.getString(1).equals(ipAddress)){
	        		pst = connection.prepareStatement("DELETE FROM webServers WHERE IP = ?");
	        		pst.setString(1, ipAddress);
	        		pst.execute();
	        		//System.out.println("Server no longer listening. - " + ipAddress);
	        		return 1;
	        	}
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//System.out.println("No listening server found.");
		
		return 0;
	}
}
