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

	private void run(String url){
		
		if(numOfSearches == 0 || queue.isEmpty()){
			System.out.println("Crawling Complete");
			return;
		}
		
		PageParser page = new PageParser(url);
		
		DatabaseManager.addLocation(page.url, page.title, page.preview, "");
//		DatabaseManager.addKeyWords(page.getData());
		
		for(Data d : page.getData())
			d.print();
		
		for(String u : page.getLinks())
			if(DatabaseManager.hasLocation(u) != -1)
				queue.add(u);
		
		numOfSearches--;
		run(queue.remove());
		
	}
}
