package gov.ornl.seda;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SEDAJob
{
  public static final int IN_QUEUE = 0;
  public static final int PROCESSING = 1;
  public static final int DONE = 2;
  private int method;
  private List<Object> args;
  private volatile int status = 0;
  private volatile Object output = null;
  private final Lock statusLock = new ReentrantLock();
  private final Condition statusCond = this.statusLock.newCondition();

  public SEDAJob(int paramInt, List<Object> paramList)
  {
    this.method = paramInt;
    this.args = paramList;
  }

  public void setStatus(int paramInt)
  {
    this.status = paramInt;
    if (this.status == 2)
    {
      this.statusLock.lock();
      this.statusCond.signal();
      this.statusLock.unlock();
    }
  }

  public int getStatus()
  {
    return this.status;
  }

  public int getMethod()
  {
    return this.method;
  }

  public List<Object> getArgs()
  {
    return this.args;
  }

  public void setOutput(Object paramObject)
  {
    this.output = paramObject;
  }

  public Object getOutput()
  {
    return this.output;
  }

  protected void lockStatus()
  {
    this.statusLock.lock();
  }

  protected void unlockStatus()
  {
    this.statusLock.unlock();
  }

  protected void waitForOutput()
  {
    try
    {
      this.statusCond.await();
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
  }
}

/* Location:
 * Qualified Name:     gov.ornl.seda.SEDAJob
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.6.1-SNAPSHOT
 */