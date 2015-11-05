import java.net.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class PortThread implements Runnable{
	private Thread t;
	private String address;
	private int portNum;
	private long startTime;
	Semaphore s;
	AtomicInteger nIP, uIP, rIP;
	
	PortThread(int firstBlock, int secondBlock, int port, Semaphore inSem, AtomicInteger newIPs, AtomicInteger updatedIPs,
			AtomicInteger removedIPs, long sTime) throws InterruptedException {
		address = "161.6." + firstBlock + "." + secondBlock;
		
		switch(port){
		case 0:
			portNum = 80;
			break;
		case 1:
			portNum = 443;
			break;
		}
		
		s = inSem;
		
		nIP = newIPs;
		uIP = updatedIPs;
		rIP = removedIPs;
		
		startTime = sTime;
	}
	
	@Override
	public void run() {
		boolean listening = serverListening(address, portNum);
		
		if (listening) {
			//download website data
			//parse to determine what kind of site
			//add to database
			int addType = DatabaseManager.addIP(address + ":" + portNum, true);
			if (addType == 0) {
				nIP.incrementAndGet();
			} else if (addType == 1) {
				uIP.incrementAndGet();
			}
		} else {
			rIP.addAndGet(DatabaseManager.removeIP(address + ":" + portNum));
		}
		
		//output current time if not displaying progress bar
		if (startTime != -1) System.out.println((double)(System.nanoTime() - startTime)/1000000000.0);
		
		s.release();
	}
	
	public void start ()
	{
		if (t == null)
		{
			t = new Thread (this, address + ":" + portNum);
			t.start ();
		}
	}
	
	public static boolean serverListening(String host, int port)
    {
        Socket s = null;
        try
        {
            s = new Socket(host, port);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            if(s != null) {
                try {s.close();}
                catch(Exception e){}
            }
        }
    }
}
