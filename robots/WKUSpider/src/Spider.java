import java.util.LinkedList;
import java.util.Queue;

public class Spider {

	private String startingPoint = "";
	private int numOfSearches = 0;
	Queue<String> queue = new LinkedList<String>();
	
	public Spider(String startingPoint, int numOfSearches) {
		this.startingPoint = startingPoint;
		this.numOfSearches = numOfSearches;
	}
	
	/**
	 * Starts the crawling based on previous set parameters
	 */
	public void crawl(){
		queue.add(startingPoint);
		run(startingPoint);
	}

	/**
	 * Recursive method that will recursively parse pages
	 * @param url
	 */
	private void run(String url){
		
		if(numOfSearches == 0 || queue.isEmpty()){
			System.out.println("Crawling Complete");
			return;
		}
		

		PageParser page = new PageParser(url);

		int pageID = DatabaseManager.addLocation(page.url, page.title,
				page.preview, "");

		DatabaseManager.addData(page.getData(), pageID);

		for (String u : page.getLinks())
			if (DatabaseManager.getLocation(u) == -1)
				queue.add(u);

		numOfSearches--;
		run(queue.remove());
		
	}
}
