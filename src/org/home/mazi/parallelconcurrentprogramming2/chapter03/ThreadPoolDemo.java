package org.home.mazi.parallelconcurrentprogramming2.chapter03;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class VegetableChopper extends Thread {
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " chopped a vegetable!");
	}
}

// 1 DEMO
public class ThreadPoolDemo {
	public static void main(String[] args) {
		int numProcs = Runtime.getRuntime().availableProcessors();
		System.out.println(numProcs);
		ExecutorService pool = Executors.newFixedThreadPool(numProcs);

		for (int i = 0; i < 100; i++) {
			//new VegetableChopper().start();
			pool.submit(new VegetableChopper());
		}

		pool.shutdown();
	}
}
