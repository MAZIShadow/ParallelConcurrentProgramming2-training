package org.home.mazi.parallelconcurrentprogramming2.chapter01;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class SoupProducer extends Thread {

	private final BlockingQueue<String> servingLine;

	public SoupProducer(BlockingQueue<String> servingLine) {
		this.servingLine = servingLine;
	}

	@Override
	public void run() {
		for (int i = 0; i < 20; i++) {
			try {
				servingLine.add("Bowl #" + i);
				System.out.format("Served Bowl #%d - remaining capacity: %d\n", i, servingLine.remainingCapacity());
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		servingLine.add("no soup for you!");
		servingLine.add("no soup for you!");
	}
}

class SoupConsumer extends Thread {

	private final BlockingQueue<String> servingLine;

	public SoupConsumer(BlockingQueue<String> servingLine) {
		this.servingLine = servingLine;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String bowl = servingLine.take();

				if (bowl.equals("no soup for you!")) {
					return;
				}

				System.out.format("Ate %s\n", bowl);
				Thread.sleep(300);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

public class ProducerConsumerDemo {
	public static void main(String[] args) {
		BlockingQueue<String> servingLine = new ArrayBlockingQueue<>(5);
		new SoupConsumer(servingLine).start();
		new SoupConsumer(servingLine).start();
		new SoupProducer(servingLine).start();
	}
}
