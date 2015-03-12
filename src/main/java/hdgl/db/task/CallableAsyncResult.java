package hdgl.db.task;

        import java.util.Vector;
        import java.util.concurrent.Callable;
        import java.util.concurrent.CancellationException;
        import java.util.concurrent.ExecutionException;
        import java.util.concurrent.TimeUnit;
        import java.util.concurrent.TimeoutException;

/**
 *
 * @author elm
 *
 * @param <T>
 */
public class CallableAsyncResult<T> implements Runnable, Callable<T>, AsyncResult<T> {

    Callable<T> callable;
    Vector<AsyncCallback<T>> listeners = new Vector<AsyncCallback<T>>();
    boolean cancelled;
    boolean done;
    boolean started;
    Throwable exception;
    T result;
    Thread runningThread;

    public CallableAsyncResult(){
        this.started = false;
        this.cancelled = false;
        this.done = false;
    }

    public CallableAsyncResult(Callable<T> callable){
        this.callable = callable;
        this.started = false;
        this.cancelled = false;
        this.done = false;
    }

    @Override
    public void run() {
        try{
            call();
        }catch(ExecutionException ex){
            //该异常已被处理，此处忽略即可
        }
    }

    @Override
    public T call() throws ExecutionException {
        boolean succ;
        synchronized(this){
            started = true;
        }
        for(AsyncCallback<T> callback:listeners){
            callback.started();
            callback.progress(0);
        }
        try{
            if(callable!=null){
                setValue(callable.call());
            }
            succ = true;
        }catch(Throwable ex){
            setException(ex);
            succ = false;
        }
        synchronized(this){
            this.done = true;
            this.notifyAll();
        }
        if(succ){
            for(AsyncCallback<T> callback:listeners){
                callback.progress(1);
                callback.completed(result);
            }
            return result;
        }else{
            for(AsyncCallback<T> callback:listeners){
                callback.exception(exception);
            }
            throw new ExecutionException(exception);
        }
    }

    @Override
    public boolean supportCancel() {
        return true;
    }

    @Override
    public void addCallback(AsyncCallback<T> callback) {
        listeners.add(callback);
    }

    @Override
    public void removeCallback(AsyncCallback<T> callback) {
        listeners.remove(callback);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean result;
        synchronized(this){
            if(cancelled){
                result = false;
            }else if(!this.started){
                this.started = true;
                this.cancelled = true;
                this.done = true;
                result = true;
            }else{
                if(mayInterruptIfRunning){
                    if(runningThread!=null){
                        runningThread.interrupt();
                    }
                    this.cancelled = true;
                    this.done = true;
                    result = true;
                }else{
                    result = false;
                }
            }
        }
        if(result){
            for(AsyncCallback<T> callback:listeners){
                callback.cancelled();
            }
            synchronized(this){
                this.notifyAll();
            }
        }
        return cancelled||result;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        boolean starting = false;
        synchronized(this){
            if(!started){
                starting = true;
            }
            if(cancelled){
                throw new CancellationException();
            }else if(exception!=null){
                throw new ExecutionException(exception);
            }else if(done){
                return result;
            }
        }
        if(starting){
            start();
        }
        synchronized(this){
            this.wait();
            if(cancelled){
                throw new CancellationException();
            }else if(exception!=null){
                throw new ExecutionException(exception);
            }else if(done){
                return result;
            }else{
                throw new InterruptedException();
            }
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        boolean starting = false;
        synchronized(this){
            if(!started){
                starting = true;
            }
            if(cancelled){
                throw new CancellationException();
            }else if(exception!=null){
                throw new ExecutionException(exception);
            }else if(done){
                return result;
            }
        }
        if(starting){
            start();
        }
        synchronized(this){
            unit.timedWait(this, timeout);
            if(cancelled){
                throw new CancellationException();
            }else if(exception!=null){
                throw new ExecutionException(exception);
            }else if(done){
                return result;
            }else{
                throw new TimeoutException();
            }
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public void setValue(T val){
        synchronized(this){
            this.done = true;
            this.result = val;
            this.notifyAll();
        }
    }

    public void setException(Throwable ex){
        synchronized(this){
            this.exception = ex;
            this.notifyAll();
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void start() {
        boolean starting = !started;
        synchronized(this){
            starting =! started;
            if(!started){
                started = true;
            }
        }
        if(starting){
            runningThread = new Thread(this);
            runningThread.setDaemon(false);
            runningThread.start();
        }
    }


}
