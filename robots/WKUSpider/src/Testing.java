public class Testing {

	public static void main(String[] args) {

		DatabaseManager.Initialize();
		
		Spider spider1 = new Spider("https://www.wku.edu/cs/index.php",1);
		spider1.crawl();
		
		DatabaseManager.printLocationDatabase();
		System.out.println("\n");
		DatabaseManager.printKeywordDatabase();
		System.out.println("\n");
		DatabaseManager.printDataDatabase();
		
		DatabaseManager.Exit();
		

	}

}
