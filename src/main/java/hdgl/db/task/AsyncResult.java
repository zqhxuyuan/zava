package hdgl.db.task;

        import java.util.concurrent.Future;

/**
 * 异步访问接口，用于封装异步执行结果。需要注意的是获取此接口之后该任务还未开始，
 * 需要调用run或sync方法之后才会开始任务。
 *
 * @author elm
 *
 * @param <T> 结果的返回类型
 */
public interface AsyncResult<T> extends Future<T> {

    /**
     * 开始执行此异步任务
     */
    public void start();

    /**
     * 返回该任务是否支持Cancel操作
     */
    public boolean supportCancel();

    /**
     * 向当前任务添加回调接口
     * @param callback 回调函数接口
     */
    public void addCallback(AsyncCallback<T> callback);

    /**
     * 移除某一个回调接口
     * @param callback 回调函数接口
     */
    public void removeCallback(AsyncCallback<T> callback);

}
