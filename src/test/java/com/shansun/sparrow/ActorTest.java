package com.shansun.sparrow;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.junit.Test;

import com.shansun.sparrow.actor.api.Actor;
import com.shansun.sparrow.actor.api.Message;
import com.shansun.sparrow.actor.builder.ActorManagerBuilder;
import com.shansun.sparrow.actor.internal.SimpleMessage;
import com.shansun.sparrow.actor.spi.AbstractActor;
import com.shansun.sparrow.actor.spi.ActorManager;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-21
 */
public class ActorTest extends TestCase {

	final static AtomicLong	sendCount		= new AtomicLong(0);
	final static AtomicLong	processedCount	= new AtomicLong(0);

	static class Demo1Actor extends AbstractActor {

		@Override
		public String getName() {
			return "demo-1-actor";
		}

		@Override
		public String getCategory() {
			return "default";
		}

		@Override
		public boolean process(Message message) {
			System.out.println("Demo1Actor received " + message);

			// processedCount.incrementAndGet();

			return true;
		}
	}

	static class Demo2Actor extends AbstractActor {

		@Override
		public String getName() {
			return "demo-2-actor";
		}

		@Override
		public String getCategory() {
			return "default";
		}

		@Override
		public boolean process(Message message) {
//			processedCount.incrementAndGet();
			
			long sum = 0;
			for(int i = 0; i < 100; i++) {
				sum += i;
			}
			
//			try {
//				Thread.sleep(5);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			// System.out.println("Demo2Actor received " + message);

			// Actors.send(message, this, "demo-3-actor");

			// sendCount.incrementAndGet();

			// System.out.println("Redirect message to demo-3-actor");

			return sum > 0;
		}

	}

	static class Demo3Actor extends AbstractActor {

		@Override
		public String getName() {
			return "demo-3-actor";
		}

		@Override
		public String getCategory() {
			return "default";
		}

		@Override
		public boolean process(Message message) {
			// processedCount.incrementAndGet();

			System.out.println("Demo3Actor received " + message);

			return true;
		}
	}

	@Test
	public void testActor() throws InterruptedException, IOException {
		try {
			ExecutorService service = Executors.newFixedThreadPool(20);

			final ActorManager manager = ActorManagerBuilder.newBuilder().withThreadCount(10).build();
			
			manager.initialize();
			
			final Actor actor1 = manager.createAndStartActor(Demo1Actor.class);

			manager.createAndStartActor(Demo2Actor.class);
			manager.createAndStartActor(Demo3Actor.class);

			for (int i = 0; i < 10; i++) {
				service.submit(new Runnable() {

					@Override
					public void run() {
						while (true) {
							manager.send(new SimpleMessage(System.currentTimeMillis()), actor1, "demo-2-actor");

							sendCount.incrementAndGet();
						}
					}
				});
			}

			while (true) {
				Thread.sleep(1000);

				long cnt = sendCount.get();
				System.err.println(cnt);
				
				sendCount.addAndGet(-cnt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		new ActorTest().testActor();
	}
}
