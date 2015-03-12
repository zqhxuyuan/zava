package hdgl.db.query;

import hdgl.db.exception.HdglException;
import hdgl.db.server.bsp.BSPRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class RegionQueryContext {
	
	private QueryContext context;
	private BSPRunner runner;
	private Map<Integer, Vector<long[]>> result = new HashMap<Integer, Vector<long[]>>();
	private int neededResultLength;
	private boolean complete;
	private Throwable error;
	private Object resultMutex = new Object();
	private Object needMutex;
	
	public RegionQueryContext(QueryContext context, BSPRunner runner) {
		super();
		this.context = context;
		this.runner = runner;
	}

	public void waitNeed(Object mutex, int len) throws InterruptedException{
		while(neededResultLength<len){
			needMutex = mutex;
			synchronized (needMutex) {
				needMutex.wait();
			}
		}
	}
	
	public void waitResult(int len) throws InterruptedException{
		while(!complete && error == null && !result.containsKey(len+1)){
			synchronized (resultMutex) {
				resultMutex.wait();
			}
		}
	}
	
	public synchronized void addResult(long[] path){
		int len=path.length;
		assert len%2==1;
		len=(len+1)/2;
		Vector<long[]> container;
		if(result.containsKey(len)){
			container = result.get(len);
		}else{
			container = new Vector<long[]>();
			result.put(len, container);
		}
		container.add(path);
		synchronized (resultMutex) {
			resultMutex.notifyAll();
		}
	}
	
	public Map<Integer, Vector<long[]>> getResults() {
		if(error!=null){
			throw new HdglException("error during query processing: " + error.getMessage(), error);
		}
		return result;
	}

	public int getNeededResultLength() {
		return neededResultLength;
	}

	public void setNeededResultLength(int neededResultLength) {
		if(neededResultLength>this.neededResultLength){
			this.neededResultLength = neededResultLength;
			if(needMutex!=null){
				synchronized (needMutex) {
					needMutex.notifyAll();
				}
			}
		}
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete() {
		this.complete = true;
		synchronized (resultMutex) {
			resultMutex.notifyAll();
		}
		if(needMutex!=null){
			synchronized(needMutex){
				needMutex.notifyAll();
			}
		}
	}
	
	public Throwable getError() {
		return error;
	}

	public void setError(Throwable err) {
		this.error = err;
		setComplete();
	}

	public QueryContext getContext() {
		return context;
	}

	public BSPRunner getRunner() {
		return runner;
	}
	
	
}
