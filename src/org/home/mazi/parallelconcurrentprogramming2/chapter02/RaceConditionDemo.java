package org.home.mazi.parallelconcurrentprogramming2.chapter02;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Shopper extends Thread {
	public static int bagsOfChips = 1;
	private static final Lock pencil = new ReentrantLock();

	public Shopper(String threadName) {
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
		} else {
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

// 1 DEMO
public class RaceConditionDemo {
	public static void main(String[] args) throws InterruptedException {
		Shopper[] shoppers = new Shopper[10];
		for (int i = 0; i < shoppers.length/2; i++) {
			shoppers[2*i] = new Shopper("Barron-" + i);
			shoppers[2*i + 1] = new Shopper("Olivia-" + i);
		}

		for (Shopper s : shoppers	 ) {
			s.start();
		}

		for (Shopper s : shoppers	 ) {
			s.join();
		}

		System.out.println("We need to buy " + Shopper.bagsOfChips + " bags of chips.");
	}
}
