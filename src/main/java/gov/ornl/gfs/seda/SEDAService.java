package gov.ornl.gfs.seda;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class SEDAService
  implements Runnable
{
  private final Thread executor = new Thread(this);
  private final LinkedBlockingQueue<SEDAJob> queue = new LinkedBlockingQueue();
  private float hcap = 0.0F;
  private float hdev = 0.0F;
  private int MAX_CAPACITY = 100;

  public SEDAService()
  {
    this.executor.start();
  }

  public void run()
  {
    while (true)
      try
      {
        SEDAJob localSEDAJob = (SEDAJob)this.queue.take();
        localSEDAJob.setStatus(1);
        startJob(localSEDAJob);
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
  }

  private void setCapacity(int paramInt)
  {
    this.MAX_CAPACITY = paramInt;
  }

  public float capacity()
  {
    return this.queue.size() / this.MAX_CAPACITY;
  }

  public float getHistoricalCapacity()
  {
    return this.hcap;
  }

  public float getHistoricalDeviation()
  {
    return this.hdev;
  }

  public SEDAFuture addJob(SEDAJob paramSEDAJob)
  {
    if (this.queue.size() == this.MAX_CAPACITY)
      return null;
    SEDAFuture localSEDAFuture = new SEDAFuture(paramSEDAJob);
    this.queue.add(paramSEDAJob);
    return localSEDAFuture;
  }

  protected abstract void startJob(SEDAJob paramSEDAJob);
}

/* Location:
 * Qualified Name:     gov.ornl.gfs.seda.SEDAService
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.6.1-SNAPSHOT
 */