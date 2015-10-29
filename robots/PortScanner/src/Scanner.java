import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Scanner {

    public static void main(String[] args) throws InterruptedException {
    	long startTime = System.nanoTime();
    	Semaphore s = new Semaphore(255);
    	int numIPScanned = 0;
    	final int maxIPs = 65535*2; //131070
    	AtomicInteger newIPs = new AtomicInteger(0);
    	AtomicInteger updatedIPs = new AtomicInteger(0);
    	AtomicInteger removedIPs = new AtomicInteger(0);
    	
    	//newline for neatness
    	System.out.println("");
    	
    	DatabaseManager.Initialize();
    	
    	for (int i=0; i<256; i++) {
    		for (int j=0; j<256; j++) {
    			for (int p=0; p<2; p++) {
	    			s.acquire();
	    			drawProgressBar(++numIPScanned, maxIPs);
	    			Runnable r = new PortThread(i, j, p, s, newIPs, updatedIPs, removedIPs);
	    			new Thread(r).start();
    			}
    		}
    	}
    	
    	//newline for progress bar
    	System.out.println("");
    	
    	long totalTime = (System.nanoTime() - startTime);
    	long timeHours = TimeUnit.NANOSECONDS.toHours(totalTime);
    	long timeMinutes = TimeUnit.NANOSECONDS.toMinutes(totalTime) - timeHours * 60;
    	long timeSeconds = TimeUnit.NANOSECONDS.toSeconds(totalTime) - timeHours * 360 - timeMinutes * 60;
    	
    	//get number of actual web servers from database
    	
    	DatabaseManager.Exit();
    	
    	System.out.println("\nDONE -- ");
    	System.out.println("65535 IP addresses w/ 2 ports parsed in "
    			+ timeHours + " hours " + timeMinutes + " minutes " + timeSeconds + " seconds.");
    	System.out.println(newIPs.get() + " new IP addresses added.");
    	System.out.println(updatedIPs.get() + " updated IP addresses.");
    	System.out.println(removedIPs.get() + " IP addresses removed.");
    }
    
    //maybe put in an async function to count seconds
    public static void drawProgressBar(int current, int max) {
    	char numOfChars = 30;
    	double percent = current*1.0/max;
    	
    	String bar = "";
    	boolean inProgress = true;
    	for (int i=0; i<numOfChars; i++){
    		if (i < Math.floor(percent*numOfChars)) bar += "=";
    		else {
    			if (inProgress) {
    				bar += ">";
    				inProgress = false;
    			} else {
    				bar += " ";
    			}
    		}
    	}
    	
    	System.out.print("   [" + bar + "] " + (int) Math.floor(percent*100) + "% completed.\r");
    }
}