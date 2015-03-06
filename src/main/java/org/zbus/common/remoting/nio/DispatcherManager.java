package org.zbus.common.remoting.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
 

public abstract class DispatcherManager {
	private static final Logger log = LoggerFactory.getLogger(DispatcherManager.class);
	private final Codec codec; 
	
	private ExecutorService executor;
	
	private final int dispatcherCount;
	private final Dispatcher[] dispatchers;
	private AtomicInteger dispactherIndex = new AtomicInteger(0);
	private final String dispatcherNamePrefix;
	
	protected volatile boolean started = false; 
	
	public DispatcherManager(
			Codec codec,  
			ExecutorService executor, 
			int dispatcherCount, 
			String dispatcherNamePrefix) throws IOException{
		
		this.dispatcherCount = dispatcherCount; 
		this.codec = codec; 
		this.executor = executor;
		this.dispatcherNamePrefix = dispatcherNamePrefix;
		
		this.dispatchers = new Dispatcher[this.dispatcherCount];
		
		for(int i=0;i<this.dispatcherCount;i++){
			String dispatcherName = String.format("%s%d", dispatcherNamePrefix, i);
			this.dispatchers[i] = new Dispatcher(this, dispatcherName);
		}
	}
	
	public DispatcherManager(
			Codec codec, 
			ExecutorService executor,
			int dispactherCount) throws IOException{
		this(codec, executor, dispactherCount,"Dispatcher");
	}
	
	public DispatcherManager(Codec codec) throws IOException{
		this(codec, newDefaultExecutor(), defaultDispatcherSize());
	}
	
	public Dispatcher getDispatcher(int index){
		if(index <0 || index>=this.dispatcherCount){
			throw new IllegalArgumentException("Dispatcher index should >=0 and <"+this.dispatcherCount);
		}
		return this.dispatchers[index];
	}
	
	public Dispatcher nextDispatcher(){
		return this.dispatchers[this.dispactherIndex.getAndIncrement()%this.dispatcherCount];
	}

	public void registerSession(int ops, Session sess) throws IOException{
		if(sess.dispatcherManager() != this){
			throw new IOException("Unmatched DispatcherManager");
		}
		this.nextDispatcher().registerSession(ops, sess);
	}
	
	
	public Dispatcher getDispatcher(SelectionKey key){
		for(Dispatcher e : this.dispatchers){
			if(key.selector() == e.selector){
				return e;
			}
		}
		return null;
	}
	
	public synchronized void start() {
		if (this.started) {
			return;
		}
		 
		this.started = true;
		for (Dispatcher dispatcher : this.dispatchers) {
			dispatcher.start();
		} 
		log.info("%s(DispatcherCount=%d) started", this.dispatcherNamePrefix, this.dispatcherCount);
	}
	
	public synchronized void stop() {
		if (!this.started)
			return;

		this.started = false;
		for (Dispatcher dispatcher : this.dispatchers) {
			dispatcher.interrupt();
		} 
	} 
	
	public boolean isStarted(){
		return this.started;
	}
	
	public Codec getCodec() {
		return codec;
	} 
	
	public ExecutorService getExecutor() {
		return executor;
	}

 
	public static int defaultDispatcherSize() { 
		int processors = Runtime.getRuntime().availableProcessors();
		return processors > 8 ? 4 + (processors * 5 / 8) : processors + 1;
	}
	
	public static ExecutorService newDefaultExecutor(){
		return new ThreadPoolExecutor(4, 
				256, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	} 
	
	public abstract EventAdaptor buildEventAdaptor();
	
}
