package gov.ornl.seda;

public class SEDAFuture
{
  private SEDAJob job;

  public SEDAFuture(SEDAJob paramSEDAJob)
  {
    this.job = paramSEDAJob;
  }

  public boolean isDone()
  {
    return this.job.getStatus() == 2;
  }

  public Object get()
  {
    this.job.lockStatus();
    while (!isDone())
      this.job.waitForOutput();
    this.job.unlockStatus();
    return this.job.getOutput();
  }
}

/* Location:
 * Qualified Name:     gov.ornl.seda.SEDAFuture
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.6.1-SNAPSHOT
 */