import java.util.Queue;

public class Spider extends Thread{

	int id;
	int numOfSearches = 0;
	SpiderThread controller;
	Queue<String> links;
	
	public Spider(int id, int numOfSearches, SpiderThread con) {
		this.numOfSearches = numOfSearches;
		this.id = id;
		this.controller = con;
		links = con.links;
	}
	
	/**
	 * Starts the crawling on a new thread.
	 * If the links queue is currently empty, then wait a few seconds for another spider to populate it
	 */
	public void run(){
		if(links.isEmpty())
			try {
				System.out.println("Thread " + (id == -1 ? "Initial" : id) + " is waiting for links");
				Thread.sleep(3000*id);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		if(!links.isEmpty()){
			System.out.println("Thread " + (id == -1 ? "Initial" : id) + " is starting");
			run(links.remove());
			System.out.println("Thread " + (id == -1 ? "Initial" : id) + " is finished");
		} else{
			System.out.println("Thread " + (id == -1 ? "Initial" : id) + " found no links!");
		}
	}

	/**
	 * Recursive method that will recursively parse pages
	 * @param url
	 */
	private void run(String url){
		System.out.println("Spider " + id + " is crawling... " + url);
		
		controller.numOfPages++;
		
		if(numOfSearches == 0)
			return;
		
		PageParser page = new PageParser(url);
		
		if(!page.isEmpty){
		
			// int pageID = DatabaseManager.addLocation(page.url, page.title,
			// page.preview, page.text);
			//
			// if(pageID != -1)
			// DatabaseManager.addData(page.getData(), pageID);

			for (String u : page.getLinks())
				// if(DatabaseManager.getLocation(u) == -1)
				links.add(u);
		}
		
		if(links.isEmpty())
			return;

		numOfSearches--;
		run(links.remove());
	}
}
