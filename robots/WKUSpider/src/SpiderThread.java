import java.util.LinkedList;
import java.util.Queue;


public class SpiderThread {

	int numOfThreads, maxThreads, limit;
	String startingPoint;
	int numOfPages = 0;
	Queue<String> links = new LinkedList<String>();
	
	public SpiderThread(int maxThreads, String start, int amt) {
		this.maxThreads = maxThreads;
		startingPoint = start;
		limit = amt;
		startThreads();
	}
	
	public SpiderThread(int maxThreads, int amt) {
		this.maxThreads = maxThreads;
		limit = amt;
		startThreadsPort();
	}
	
	public synchronized void startThreadsPort() {
		if(DatabaseManager.getWebServerList() == null){
			System.out.println("No webservers");
			return;
		}
		
		links.addAll(DatabaseManager.getWebServerList());

		Thread[] threads = new Thread[maxThreads];

		Spider spider = new Spider(-1, 1, this);
		spider.run();

		for (int n = 0; n < maxThreads; n++) {
			Thread thread = new Spider(numOfThreads++, limit, this);
			threads[n] = thread;
			thread.start();
		}

		for (Thread t : threads)
			try {
				if (t != null)
					t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}
	
	public synchronized void startThreads() {
		links.add(startingPoint);

		Thread[] threads = new Thread[maxThreads];

		Spider spider = new Spider(-1, 1, this);
		spider.run();

		for (int n = 0; n < maxThreads; n++) {
			Thread thread = new Spider(numOfThreads++, limit, this);
			threads[n] = thread;
			thread.start();
		}

		for (Thread t : threads)
			try {
				if (t != null)
					t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}

	
}
