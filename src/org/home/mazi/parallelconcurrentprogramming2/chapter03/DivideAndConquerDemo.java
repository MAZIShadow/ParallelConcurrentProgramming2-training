package org.home.mazi.parallelconcurrentprogramming2.chapter03;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

class RecursiveSum extends RecursiveTask<Long> {

	private long lo;
	private long hi;

	public RecursiveSum(long lo, long hi) {
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	protected Long compute() {

		if (hi - lo <= 100_000) { // base case threshold
			return LongStream.rangeClosed(lo, hi).sum();
		}

		long mid = (hi + lo) / 2;
		RecursiveSum left = new RecursiveSum(lo, mid);
		RecursiveSum right = new RecursiveSum(mid + 1, hi);
		left.fork(); // forked thread computes left half

		return right.compute() + left.join(); // current thread computes right half
	}
}

// 3 DEMO (Fork/Join Framework)
public class DivideAndConquerDemo {
	public static void main(String[] args) {
		ForkJoinPool pool = ForkJoinPool.commonPool();
		Long invoke = pool.invoke(new RecursiveSum(0, 1_000_000_000));
		pool.shutdown();
		System.out.println("Total sum is: " + invoke);
	}
}
