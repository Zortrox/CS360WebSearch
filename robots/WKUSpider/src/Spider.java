import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Spider {

	private String startingPoint = "";
	private int numOfSearches = 0;
	Queue<String> queue = new LinkedList<String>();
	
	public Spider(String startingPoint, int numOfSearches) {
		super();
		this.startingPoint = startingPoint;
		this.numOfSearches = numOfSearches;
	}
	
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
		
		ArrayList<Data> nodes = page.getData();
		for(Data d : nodes)
			d.print();
		
		DatabaseManager.addLocation(page.url, page.title, page.preview, "lalalalalala");
		DatabaseManager.addKeyWords(page.getData());
		
		for(String u : page.getLinks())
			if(!DatabaseManager.hasLocation(u))
				queue.add(u);
		
		numOfSearches--;
		run(queue.remove());
		
	}
}
