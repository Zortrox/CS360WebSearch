public class Testing {

	public static void main(String[] args) {

		String startingPoint = "https://www.wku.edu/";
		int amt = 1;
		
		DatabaseManager.Initialize();
		
		if (args[0].equals("-display")) {
			
			if(args.length < 2){
				System.out.println("Enter name of database after -display");
				return;
			}
			
			switch(args[1]){
			case "locations":
				DatabaseManager.printLocationDatabase();
				break;
			case "keywords":
				DatabaseManager.printKeywordDatabase();
				break;
			case "siteKeywords":
				DatabaseManager.printDataDatabase();
				break;
			default:
				System.out.println("Unknown command:\nUse name of database");
				
			}
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
