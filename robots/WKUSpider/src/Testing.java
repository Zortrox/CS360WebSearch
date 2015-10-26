import java.util.concurrent.TimeUnit;


public class Testing {

	public static void main(String[] args) {
		
//		Spider s = new Spider( "https://www.wku.edu/",6);
//			s.crawl();
		
//		try {
//			System.out.println(InetAddress.getByName(new URL("https://www.wku.edu/").getHost()).getHostAddress());
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		DatabaseManager.Initialize();
//		
//		if (args[0].equals("-display")) {
//			
//			if(args.length < 2){
//				System.out.println("Enter name of database after -display");
//				return;
//			}
//			
//			switch(args[1]){
//			case "locations":
//				DatabaseManager.printLocationDatabase();
//				break;
//			case "keywords":
//				DatabaseManager.printKeywordDatabase();
//				break;
//			case "siteKeywords":
//				DatabaseManager.printDataDatabase();
//				break;
//			default:
//				System.out.println("Unknown command:\nUse name of database");
//				
//			}
//			DatabaseManager.Exit();
//			
//			return;
//		}
//		
//		int numOfThreads = 10;
//		int amt = 0;
//		String startingPoint = "https://www.wku.edu/";
//		
//		if(args.length > 0)
//			amt = Integer.parseInt(args[0]);
//		
//		if(args.length > 1)
//			startingPoint = args[1];
//		
//		if(args.length > 2)
//			numOfThreads = Integer.parseInt(args[2]);
//		
//		
		long startTime = System.nanoTime();
		
		System.out.println("Begin Crawling");

		SpiderThread th = new SpiderThread(5,"https://www.wku.edu/");
		
		long totalTime = (System.nanoTime() - startTime);
		
		System.out.println(th.numOfPages + " pages crawled in "
				+ "" + TimeUnit.NANOSECONDS.toHours(totalTime) + " hours "
				+ "" + TimeUnit.NANOSECONDS.toMinutes(totalTime) + " minutes "
				+ "" + TimeUnit.NANOSECONDS.toSeconds(totalTime) + " seconds ");
		
//		DatabaseManager.Exit();

	}

}
