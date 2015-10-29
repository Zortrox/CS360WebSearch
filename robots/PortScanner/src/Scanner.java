import java.util.concurrent.*;

public class Scanner {

    public static void main(String[] args) throws InterruptedException {
    	long startTime = System.nanoTime();
    	
    	DatabaseManager.Initialize();
    	
    	Semaphore s = new Semaphore(255);
    	
    	for (int i=0; i<256; i++) {
    		for (int j=0; j<256; j++) {
    			for (int p=0; p<2; p++) {
	    			s.acquire();
	    			Runnable r = new PortThread(i, j, p, s);
	    			new Thread(r).start();
    			}
    		}
    	}
    	
    	long totalTime = (System.nanoTime() - startTime);
    	
    	//get number of actual web servers from database
    	
    	DatabaseManager.Exit();
    	
    	System.out.println("DONE -- ");
    	System.out.println("65535 IP addresses parsed in "
    			+ "" + TimeUnit.NANOSECONDS.toHours(totalTime) + " hours "
				+ "" + TimeUnit.NANOSECONDS.toMinutes(totalTime) + " minutes "
				+ "" + TimeUnit.NANOSECONDS.toSeconds(totalTime) + " seconds.");
    }
}