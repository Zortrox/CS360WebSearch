import java.net.*;
import java.util.concurrent.Semaphore;

public class PortThread implements Runnable{
	private Thread t;
	private String address;
	private int portNum;
	Semaphore s;
	
	PortThread(int firstBlock, int secondBlock, int port, Semaphore inSem) throws InterruptedException {
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
	}
	
	@Override
	public void run() {
		boolean listening = serverListening(address, portNum);
		
		if (listening) {
			//download website data
			//parse to determine what kind of site
			//add to database
			DatabaseManager.addIP(address + ":" + portNum, true);
		} else {
			DatabaseManager.removeIP(address + ":" + portNum);
		}
		
		s.release();
	}
	
	public void start ()
	{
		if (t == null)
		{
			t = new Thread (this, address);
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
