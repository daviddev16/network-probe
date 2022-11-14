package com.networkprobe.threading.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ExitWaiter implements Runnable {

	public static final int WAIT_SECOUNDS = 3; 

	private Thread waiterThread;
	private AtomicBoolean done;

	public ExitWaiter() {
		this.waiterThread = new Thread(this, "waiter-thread");
		this.waiterThread.setDaemon(true);
		this.done = new AtomicBoolean(false);
	}

	public void start() {
		if(!waiterThread.isAlive()) {
			this.done.set(false);
			waiterThread.start();
		}
	}

	public void cancel() {
		if(waiterThread.isAlive()) {
			done.set(true);
			try {
				waiterThread.join();
			} catch (InterruptedException e) { /* ignore */ }
		}
	}

	@Override
	public void run() {
		int counter = 0;
		while (!done.get()) {
			if(counter >= WAIT_SECOUNDS) {
				System.out.println("NO_RESPONSE");
				Runtime.getRuntime().exit(-1);
			}
			counter++;
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Thread getThread() {
		return waiterThread;
	}

}
