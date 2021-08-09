package org.home.mazi.parallelconcurrentprogramming2.chapter01;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

class CellPhone extends Thread {

	private static final Semaphore charger = new Semaphore(4);

	public CellPhone(String threadName) {
		super(threadName);
	}

	@Override
	public void run() {
		try {
			charger.acquire();
			System.out.println(this.getName() + " is charging...");
			Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println(this.getName() + " is DONE charging");
			charger.release();
		}
	}
}

public class SemaphoreDemo {
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			new CellPhone("Phone-" + i).start();
		}
	}
}
