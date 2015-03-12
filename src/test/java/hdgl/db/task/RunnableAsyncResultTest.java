package hdgl.db.task;
import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import hdgl.db.task.AsyncCallback;
import hdgl.db.task.CallableAsyncResult;

import org.junit.Test;


public class RunnableAsyncResultTest {

	static class NotARealException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = -7625593567261061605L;
		
	}
	
	static class StatisticsCallback extends AsyncCallback<String>{
		public int completedCount;
		public int exceptionCount;
		public int cancelledCount;
		public int startedCount;
		
		@Override
		public void cancelled() {
			cancelledCount++;
		}
		
		@Override
		public void exception(Throwable ex) {
			exceptionCount++;
		}
		 
		@Override
		public void started() {
			startedCount++;
		}
		
		@Override
		public void completed(String value) {
			completedCount++;
		}
		
		public void check(String endWith, boolean hasStarted){
			if(hasStarted){
				assertEquals("started "+(startedCount==0?"not called":"called more than expected"), 
						1, startedCount);
			}else{
				assertEquals("started "+(startedCount==0?"not called":"called more than expected"), 
						0, startedCount);
			}
			if(endWith.equals("exception")){
				assertEquals("exception "+(exceptionCount==0?"not called":"called more than expected"), 
						1, exceptionCount);
				assertEquals("completed "+(completedCount==0?"not called":"called more than expected"), 
						0, completedCount);
				assertEquals("cancelled "+(cancelledCount==0?"not called":"called more than expected"), 
						0, cancelledCount);
			}else if(endWith.equals("completed")){
				assertEquals("exception "+(exceptionCount==0?"not called":"called more than expected"), 
						0, exceptionCount);
				assertEquals("completed "+(completedCount==0?"not called":"called more than expected"), 
						1, completedCount);
				assertEquals("cancelled "+(cancelledCount==0?"not called":"called more than expected"), 
						0, cancelledCount);
			}else if(endWith.equals("cancelled")){
				assertEquals("exception "+(exceptionCount==0?"not called":"called more than expected"), 
						0, exceptionCount);
				assertEquals("completed "+(completedCount==0?"not called":"called more than expected"), 
						0, completedCount);
				assertEquals("cancelled "+(cancelledCount==0?"not called":"called more than expected"), 
						1, cancelledCount);
			}else{
				fail("wrong check event name");
			}
		}
	}
	
	@Test
	public void get_succ() throws InterruptedException, ExecutionException{
		
			final CallableAsyncResult<String> t1 = new CallableAsyncResult<String>(new Callable<String>() {
				@Override
				public String call() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					return "Return value";
				}
			});			
			t1.start();
			try{
				t1.get(1, TimeUnit.SECONDS);
				fail("Timeout error.");
			}catch(TimeoutException ex){
				
			}
			assertEquals("get failed", "Return value", t1.get());
			assertEquals("get failed", "Return value", t1.get());
			assertEquals("get failed", "Return value", t1.get());
	}
	
	@Test
	public void get_cancel() throws InterruptedException, ExecutionException{
			final CallableAsyncResult<String> t2 = new CallableAsyncResult<String>(new Callable<String>() {
				@Override
				public String call() {					
					return null;
				}
			});
			assertTrue("cancel return false", t2.cancel(true));
			try{
				t2.get();
				fail("cancel error");
			}catch(CancellationException ex){
				
			}
			assertEquals("isDone error", t2.isDone(), true);
			assertEquals("isCancelled error", t2.isCancelled(), true);
	}
	
	@Test
	public void get_exception() throws Exception{
			final CallableAsyncResult<String> t3 = new CallableAsyncResult<String>(new Callable<String>() {
				@Override
				public String call() throws Exception {					
					throw new NotARealException();
				}
			});
			t3.start();
			try{
				t3.get();
				fail("Exception error");
			}catch(ExecutionException ex){
				assertEquals("Exception error", NotARealException.class, ex.getCause().getClass());
			}
	}
	
	@Test
	public void listener_succ() throws InterruptedException, ExecutionException{			
			final CallableAsyncResult<String> t4 = new CallableAsyncResult<String>(new Callable<String>() {
				@Override
				public String call() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					return "Return value";
				}
			});
			StatisticsCallback t4l=new StatisticsCallback();
			t4.addCallback(t4l);
			t4.start();
			t4.start();
			t4.start();
			assertEquals("get failed", "Return value", t4.get());
			assertEquals("get failed", "Return value", t4.get());
			assertEquals("get failed", "Return value", t4.get());
			t4l.check("completed",true);
	}
	
	@Test
	public void listener_exception() throws Exception{
			final CallableAsyncResult<String> t = new CallableAsyncResult<String>(new Callable<String>() {
				@Override
				public String call() throws Exception {					
					throw new NotARealException();
				}
			});
			StatisticsCallback tl=new StatisticsCallback();
			t.addCallback(tl);
			t.start();
			t.start();
			try{
				t.get();
				fail("exception error");
			}catch(ExecutionException ex){
				assertEquals("exception error", NotARealException.class, ex.getCause().getClass());
			}
			tl.check("exception",true);
		
	}
	
	@Test
	public void listener_cancel() throws Exception{
			final CallableAsyncResult<String> t = new CallableAsyncResult<String>(new Callable<String>() {
				@Override
				public String call() throws Exception {					
					throw new NotARealException();
				}
			});
			StatisticsCallback tl=new StatisticsCallback();
			t.addCallback(tl);
			assertTrue("cancel return false", t.cancel(true));
			assertTrue("cancel return false", t.cancel(true));
			t.start();
			t.start();
			try{
				t.get();
				fail("cancel error");
			}catch(CancellationException ex){
				
			}
			tl.check("cancelled",false);
		
	}

}
