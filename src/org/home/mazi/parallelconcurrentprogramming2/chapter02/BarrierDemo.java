package org.home.mazi.parallelconcurrentprogramming2.chapter02;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ShopperBarrier extends Thread {
	public static int bagsOfChips = 1;
	private static final Lock pencil = new ReentrantLock();
	private static CyclicBarrier fistBump = new CyclicBarrier(10);

	public ShopperBarrier(String threadName) {
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
			try {
				fistBump.await();
			}
			catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		} else {
			try {
				fistBump.await();
			}
			catch (InterruptedException | BrokenBarrierException e) {
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

// 2 DEMO
public class BarrierDemo {
	public static void main(String[] args) throws InterruptedException {
		ShopperBarrier[] shoppers = new ShopperBarrier[10];
		for (int i = 0; i < shoppers.length/2; i++) {
			shoppers[2*i] = new ShopperBarrier("Barron-" + i);
			shoppers[2*i + 1] = new ShopperBarrier("Olivia-" + i);
		}

		for (ShopperBarrier s : shoppers	 ) {
			s.start();
		}

		for (ShopperBarrier s : shoppers	 ) {
			s.join();
		}

		System.out.println("We need to buy " + ShopperBarrier.bagsOfChips + " bags of chips.");
	}
}
