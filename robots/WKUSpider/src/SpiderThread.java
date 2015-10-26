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
	
	public synchronized void startThreads(){
			links.add(startingPoint);
			
			Thread[] threads = new Thread[maxThreads];
			
			Spider spider = new Spider(-1,1,this);
			spider.run();

			for (int n = 0; n < maxThreads; n++) {
				Thread thread = new Spider(numOfThreads++,limit,this);
				threads[n] = thread;
				thread.start();
			}
			
			for (Thread t : threads)
				try {
					if(t!=null)
						t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
	
}
