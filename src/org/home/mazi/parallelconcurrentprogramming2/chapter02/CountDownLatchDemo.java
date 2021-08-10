package org.home.mazi.parallelconcurrentprogramming2.chapter02;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ShopperCountDownLatch extends Thread {
	public static int bagsOfChips = 1;
	private static final Lock pencil = new ReentrantLock();
	private static final CountDownLatch fistBump = new CountDownLatch(5);

	public ShopperCountDownLatch(String threadName) {
		super(threadName);
	}

	@Override
	public void run() {
		if (this.getName().contains("Olivia")) {
			pencil.lock();
			try {
				bagsOfChips += 3;
				System.out.println(this.getName() + " ADDED three bags of chips.");
			} finally {
				pencil.unlock();
			}
			fistBump.countDown();
		} else {
			try {
				fistBump.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			pencil.lock();
			try {
				bagsOfChips *= 2;
				System.out.println(this.getName() + " DOUBLED the bags of chips.");
			} finally {
				pencil.unlock();
			}
		}
	}
}

// 3 DEMO
public class CountDownLatchDemo {
	public static void main(String[] args) throws InterruptedException {
		ShopperCountDownLatch[] shoppers = new ShopperCountDownLatch[10];
		for (int i = 0; i < shoppers.length/2; i++) {
			shoppers[2*i] = new ShopperCountDownLatch("Barron-" + i);
			shoppers[2*i + 1] = new ShopperCountDownLatch("Olivia-" + i);
		}

		for (ShopperCountDownLatch s : shoppers	 ) {
			s.start();
		}

		for (ShopperCountDownLatch s : shoppers	 ) {
			s.join();
		}

		System.out.println("We need to buy " + ShopperCountDownLatch.bagsOfChips + " bags of chips.");
	}
}
