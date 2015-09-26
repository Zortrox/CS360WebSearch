import java.net.*;
import java.util.concurrent.Semaphore;

public class PortThread implements Runnable{
	private Thread t;
	private String address;
	Semaphore s;
	
	PortThread(int firstBlock, int secondBlock, Semaphore inSem) throws InterruptedException {
		address = "161.6." + firstBlock + "." + secondBlock;
		s = inSem;
		if (secondBlock % 255 == 0) System.out.println("## - " +  address );
	}
	
	@Override
	public void run() {
		boolean normPort = serverListening(address, 80);
		boolean httpsPort = false;//serverListening(address, 443);
		
		if (normPort || httpsPort) {
			String port = "";
		
			//write each address and port to console
			if (normPort) {
				port = ":80";
				System.out.println(address + port);
			}
			if (httpsPort) {
				port = ":443";
				System.out.println(address + port);
			}
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
