import java.util.LinkedList;
import java.util.Queue;


public class SpiderThread implements Runnable{

	int numOfThreads, maxThreads, limit;
	String startingPoint;
	int numOfPages = 0;
	Queue<String> links = new LinkedList<String>();
	private Thread[] threads;
	
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
		
		// queue up all the links in the webserver list
		links.addAll(DatabaseManager.getWebServerList());
		
		// create all the threads and let them run
		Thread[] threads = new Thread[maxThreads];

//		Spider spider = new Spider(-1, 1, this);
//		spider.run();

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

	public void run() {
		while(!links.isEmpty() && limit ==-1){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for (int i = 0 ; i < threads.length ; i++){
				if(!threads[i].isAlive()){
					System.out.println("Thread "+i+" is dead: starting it up again");
					threads[i] = new Spider(i, limit, this);
					threads[i].start();
					try {
						threads[i].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}
		}
	}

	
}
