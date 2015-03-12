package hdgl.db.task;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * 该类用于将一个确定的值包装为AsyncResult
 * @author hadoop
 *
 */
public class DefiniteAsyncResult<T> implements AsyncResult<T> {

    T value;
    Vector<AsyncCallback<T>> callbacks = new Vector<AsyncCallback<T>>();

    public DefiniteAsyncResult(T value){
        this.value = value;
    }

    @Override
    public boolean cancel(boolean arg0) {
        return false;
    }

    @Override
    public T get(){
        return value;
    }

    @Override
    public T get(long arg0, TimeUnit arg1){
        return value;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void start() {
        for(AsyncCallback<T> callback :callbacks){
            callback.started();
        }
        for(AsyncCallback<T> callback :callbacks){
            callback.progress(0);
        }
        for(AsyncCallback<T> callback :callbacks){
            callback.progress(1);
        }
        for(AsyncCallback<T> callback :callbacks){
            callback.completed(value);
        }
    }

    @Override
    public boolean supportCancel() {
        return false;
    }

    @Override
    public void addCallback(AsyncCallback<T> callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeCallback(AsyncCallback<T> callback) {
        callbacks.remove(callback);
    }

}
