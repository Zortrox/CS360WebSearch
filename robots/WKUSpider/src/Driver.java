import java.util.concurrent.TimeUnit;


public class Driver {

	public static void main(String[] args) {
		boolean start = false, portscan = false;
		String displayData = "";
		int numOfThreads = 10;
		int amt = -1;
		String startingPoint = "https://www.wku.edu/";
		
		if(args.length == 0){
			System.out.println("No parameters were entered.\n"
					+ "-start \t\t: starts the spider\n"
					+ "-startPortScan \t: starts the port scan indexing\n"
					+ "-display \t: enter the database name to display its contents\n"
					+ "-amt \t\t: set the amount of pages for each thread to transverse\n"
					+ "-numOfThreads \t: set the maximum number of threads to use\n"
					+ "-startURL \t: enter the starting URL location");
			return;
		}
		
		for(int i = 0; i < args.length ; i++){
			switch(args[i]){
			case "-start":
				start = true;
				break;
			case "-startPortScan":
				portscan = true;
				break;
			case "-display":
				displayData = args[i+1];
				i++;
				break;
			case "-numOfThreads":
				numOfThreads = Integer.parseInt(args[i+1]);
				i++;
				break;
			case "-amt":
				amt = Integer.parseInt(args[i+1]);
				i++;
				break;
			case "-startURL":
				startingPoint = args[i+1];
				i++;
				break;
			}
		}
		
		// Starts the database connection ------------------------------------------------------------------------------
		DatabaseManager.Initialize();
		
		
		// displaying the data inside the databases	
		switch (displayData) {
		case "locations":
			DatabaseManager.printLocationDatabase();
			break;
		case "keywords":
			DatabaseManager.printKeywordDatabase();
			break;
		case "siteKeywords":
			DatabaseManager.printDataDatabase();
			break;
		case "webServers":
			DatabaseManager.printWebServers();
			break;
		}
		
		
		if (start) {
			long startTime = System.nanoTime();
			
			System.out.println("Begin Crawling");
			SpiderThread th = new SpiderThread(numOfThreads, startingPoint, amt);

			long totalTime = (System.nanoTime() - startTime);
			System.out.println(th.numOfPages + " pages crawled in " + ""
					+ TimeUnit.NANOSECONDS.toHours(totalTime) + " hours " + ""
					+ TimeUnit.NANOSECONDS.toMinutes(totalTime)%60 + " minutes "
					+ "" + TimeUnit.NANOSECONDS.toSeconds(totalTime)%60
					+ " seconds " + ""
					+ TimeUnit.NANOSECONDS.toMillis(totalTime)%1000
					+ " milliseconds");
		}
		
		if (portscan) {
			long startTime = System.nanoTime();
			
			System.out.println("Begin Crawling Port Scanner Version!");
			SpiderThread th = new SpiderThread(numOfThreads, amt);

			long totalTime = (System.nanoTime() - startTime);
			System.out.println(th.numOfPages + " pages crawled in " + ""
					+ TimeUnit.NANOSECONDS.toHours(totalTime) + " hours " + ""
					+ TimeUnit.NANOSECONDS.toMinutes(totalTime)%60 + " minutes "
					+ "" + TimeUnit.NANOSECONDS.toSeconds(totalTime)%60
					+ " seconds " + ""
					+ TimeUnit.NANOSECONDS.toMillis(totalTime)%1000
					+ " milliseconds");
		}
		
		// Closes the database connection ---------------------------------------------------------------
		DatabaseManager.Exit();

	}

}
