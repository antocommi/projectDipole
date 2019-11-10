package pente;

public class TimerThread extends Thread {
	Thread t;
	
	public TimerThread(Thread t){
		super();
		this.t=t;
	}
	public void run(){
		try {
			Thread.sleep(950);

		
		t.interrupt();
		}catch(Exception e){}
	}
}

