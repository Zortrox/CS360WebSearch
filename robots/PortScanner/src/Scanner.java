import java.io.*;
import java.util.concurrent.*;

public class Scanner {

    public static void main(String[] args) throws IOException, InterruptedException {
    	PrintWriter writer = new PrintWriter("serverIPList.txt", "UTF-8");
    	
    	Semaphore s = new Semaphore(255);
    	
    	for (int i=0; i<256; i++) {
    		for (int j=0; j<256; j++) {
    			s.acquire();
    			Runnable r = new PortThread(i, j, s);
    			new Thread(r).start();
    		}
    	}
    	
    	System.out.println("DONE");
    	
    	writer.close();
    }
}