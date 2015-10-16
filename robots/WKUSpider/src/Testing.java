public class Testing {

	public static void main(String[] args) {

		String startingPoint = "https://www.wku.edu/";
		int amt = 1;
		
		DatabaseManager.Initialize();
		
		if (args[0].equals("-display")) {
			DatabaseManager.printLocationDatabase();
			System.out.println("\n");
			DatabaseManager.printKeywordDatabase();
			System.out.println("\n");
			DatabaseManager.printDataDatabase();
			
			DatabaseManager.Exit();
			
			return;
		}
		
		if(args.length > 0)
			amt = Integer.parseInt(args[0]);
		
		if(args.length > 1)
			startingPoint = args[1];
		
		Spider spider = new Spider(startingPoint,amt);
		spider.crawl();
		
		DatabaseManager.Exit();
		

	}

}
