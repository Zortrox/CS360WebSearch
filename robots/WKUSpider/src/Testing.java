

public class Testing {

	public static void main(String[] args) {

		//http://www.wku.edu/cs/index.php
		
//		PageParser page = new PageParser("https://www.wku.edu/cs/index.php");
//		page.hashCode(); // just to get rid of warning for now
//		
//		ArrayList<Data> nodes = page.getData();
//		
//		for(Data d : nodes){
//			d.print();
//		}

		DatabaseManager.Initialize();
		
		Spider spider1 = new Spider("https://www.wku.edu/cs/index.php",1);
		spider1.crawl();
		
		DatabaseManager.printLocationDatabase();
		
		DatabaseManager.Exit();
		

	}

}
