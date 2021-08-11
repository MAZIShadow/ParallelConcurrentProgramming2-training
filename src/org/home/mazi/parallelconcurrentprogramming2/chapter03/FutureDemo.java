package org.home.mazi.parallelconcurrentprogramming2.chapter03;

import java.util.concurrent.*;

class HowManyVegetables implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
		System.out.println("Olivia is counting vegetables...");
		Thread.sleep(3000);

		return 42;
	}
}

// 2 DEMO
public class FutureDemo {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("Barron ask Olivia how many vegetables are in the pantry");
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Integer> result = executorService.submit(new HowManyVegetables());
		System.out.println("Barron can do other things while he waits for the results...");
		System.out.println("Olivia responded with " + result.get());
		executorService.shutdown();

	}
}
