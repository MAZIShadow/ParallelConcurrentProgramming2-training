package org.home.mazi.parallelconcurrentprogramming2.challenge;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

class SequentialMatrixMultiplier {
	private int[][] a, b;
	private int numRowsA, numColsA, numRowsB, numColsB;

	public SequentialMatrixMultiplier(int[][] a, int[][] b) {
		this.a = a;
		this.b = b;

		numRowsA = a.length;
		numColsA = a[0].length;
		numRowsB = b.length;
		numColsB = b[0].length;

		if (numColsA != numRowsB) {
			throw new Error(String.format("Invalid dimensions; Cannot multiply %dx%d*%dx%d\n", numRowsA, numRowsB, numColsA, numColsB));
		}
	}

	public int[][] computeProduct() {
		int[][] c = new int[numRowsA][numColsB];
		for (int i = 0; i < numRowsA; i++) {
			for (int j = 0; j < numColsB; j++) {
				int sum = 0;
				for (int k = 0; k < numColsA; k++) {
					sum+= a[i][k] * b[k][j];
				}
				c[i][j] = sum;
			}
		}

		return c;
	}
}

class ParallelMatrixMultiplier {
	private int[][] a, b;
	private int numRowsA, numColsA, numRowsB, numColsB;

	public ParallelMatrixMultiplier(int[][] a, int[][] b) {
		this.a = a;
		this.b = b;

		numRowsA = a.length;
		numColsA = a[0].length;
		numRowsB = b.length;
		numColsB = b[0].length;

		if (numColsA != numRowsB) {
			throw new Error(String.format("Invalid dimensions; Cannot multiply %dx%d*%dx%d\n", numRowsA, numRowsB, numColsA, numColsB));
		}
	}

	private class ParallelWorker implements Callable<int[][]> {

		private int rowStartC, rowEndC;

		public ParallelWorker(int rowStartC, int rowEndC) {
			this.rowStartC = rowStartC;
			this.rowEndC = rowEndC;
		}

		public int[][] call() {
			int[][] partialC = new int[rowEndC-rowStartC][numColsB];
			for(int i=0; i<rowEndC-rowStartC; i++) {
				for(int k=0; k<numColsB; k++) {
					int sum = 0;
					for(int j=0; j<numColsA; j++) {
						sum += a[i+rowStartC][j]*b[j][k];
					}
					partialC[i][k] = sum;
				}
			}
			return partialC;
		}
	}

	public int[][] computeProduct() {
		int[][] c = new int[numRowsA][numColsB];
		int numWorkers = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(numWorkers);

		// submit tasks to calculate partial results
		int chunkSize = (int) Math.ceil((double) numRowsA / numWorkers);
		Future[] futures = new Future[numWorkers];
		for (int w=0; w<numWorkers; w++) {
			int start = Math.min(w * chunkSize, numRowsA);
			int end = Math.min((w + 1) * chunkSize, numRowsA);
			futures[w] = pool.submit(new ParallelWorker(start, end));
		}

		// merge partial results

		try {
			for (int w=0; w<numWorkers; w++) {
				// retrieve value from future
				int[][] partialC = (int[][]) futures[w].get();
				for (int i=0; i<partialC.length; i++)
					for (int j=0; j<numColsB; j++)
						c[i + (w * chunkSize)][j] = partialC[i][j];
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		pool.shutdown();

		return c;
	}
}

public class MatrixChallenge {

	public static int[][] generateRandomMatrix(int M, int N) {
		System.out.format("Generating random %d x %d matrix...\n", M, N);
		Random rand = new Random();
		int[][] output = new int[M][N];
		for (int i=0; i<M; i++)
			for (int j=0; j<N; j++)
				output[i][j] = rand.nextInt(100);
		return output;
	}

	public static void main(String[] args) {
		final int NUM_EVAL_RUNS = 5;
		final int[][] A = generateRandomMatrix(2000,2000);
		final int[][] B = generateRandomMatrix(2000,2000);
		System.out.println("Evaluating Sequential Implementation...");
		SequentialMatrixMultiplier smm = new SequentialMatrixMultiplier(A,B);
		int[][] sequentialResult = smm.computeProduct();
		double sequentialTime = 0;
		for(int i=0; i<NUM_EVAL_RUNS; i++) {
			long start = System.currentTimeMillis();
			smm.computeProduct();
			sequentialTime += System.currentTimeMillis() - start;
		}
		sequentialTime /= NUM_EVAL_RUNS;

		System.out.println("Evaluating Parallel Implementation...");
		ParallelMatrixMultiplier pmm = new ParallelMatrixMultiplier(A,B);
		int[][] parallelResult = pmm.computeProduct();
		double parallelTime = 0;
		for(int i=0; i<NUM_EVAL_RUNS; i++) {
			long start = System.currentTimeMillis();
			pmm.computeProduct();
			parallelTime += System.currentTimeMillis() - start;
		}
		parallelTime /= NUM_EVAL_RUNS;

		// display sequential and parallel results for comparison
		if (!Arrays.deepEquals(sequentialResult, parallelResult))
			throw new Error("ERROR: sequentialResult and parallelResult do not match!");
		System.out.format("Average Sequential Time: %.1f ms\n", sequentialTime);
		System.out.format("Average Parallel Time: %.1f ms\n", parallelTime);
		System.out.format("Speedup: %.2f \n", sequentialTime/parallelTime);
		System.out.format("Efficiency: %.2f%%\n", 100*(sequentialTime/parallelTime)/Runtime.getRuntime().availableProcessors());
	}
}
