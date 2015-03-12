package hdgl.db.task;

/**
 * AsyncResult的回调函数
 * <p>该类的回调函数都是已实现的，并且函数主体都为空。
 * 实现此类时只要选择性的覆盖部分回调方法即可。<p>
 * @author elm
 *
 */
public abstract class AsyncCallback<T> {

    /**
     * 当任务开始时发生
     */
    public void started(){

    }

    /**
     * 当任务成功完成，并获取到返回值时发生
     * @param value 返回值
     */
    public void completed(T value){

    }

    /**
     * 当任务进度更新时发生，progress是一个0-1的数字代表着当前任务的进度
     * @param progress 0-1的数字，代表着当前任务的进度
     */
    public void progress(double progress){

    }

    /**
     * 当任务执行过程中抛出异常并停止时发生。
     * @param ex 抛出的异常
     */
    public void exception(Throwable ex){

    }

    /**
     * 当任务被取消时触发。
     */
    public void cancelled(){

    }
}
