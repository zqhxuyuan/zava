package gov.ornl.config;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigEntry
{
  private boolean finalEntry = false;
  private List<String> values;
  private Map<String, ConfigEntry> config;

  public ConfigEntry(List<String> paramList)
  {
    this.values = paramList;
    this.config = null;
  }

  public ConfigEntry(Map<String, ConfigEntry> paramMap)
  {
    this.config = paramMap;
    this.values = null;
  }

  protected void setFinal(boolean paramBoolean)
  {
    this.finalEntry = paramBoolean;
  }

  public boolean isFinal()
  {
    return this.finalEntry;
  }

  public boolean isAtomic()
  {
    return this.values != null;
  }

  public String getValue(int paramInt)
  {
    if (this.values != null)
      return (String)this.values.get(paramInt);
    return null;
  }

  public List<String> getValues()
  {
    return this.values;
  }

  public ConfigEntry getEntry(String paramString)
  {
    if (this.config != null)
      return (ConfigEntry)this.config.get(paramString);
    return null;
  }

  public Collection<ConfigEntry> getEntries()
  {
    if (this.config != null)
      return this.config.values();
    return null;
  }

  public void write(OutputStream paramOutputStream)
  {
    try
    {
      Iterator localIterator;
      String str;
      if (this.config != null)
      {
        localIterator = this.config.keySet().iterator();
        while (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          ConfigEntry localConfigEntry = (ConfigEntry)this.config.get(str);
          paramOutputStream.write(String.format("<%s>\n", new Object[] { str }).getBytes());
          localConfigEntry.write(paramOutputStream);
          paramOutputStream.write(String.format("</%s>\n", new Object[] { str }).getBytes());
        }
      }
      else
      {
        int i = 0;
        localIterator = this.values.iterator();
        while (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          paramOutputStream.write(String.format("%s", new Object[] { str }).getBytes());
          if (i++ < this.values.size() - 1)
            paramOutputStream.write(String.format(",\n", new Object[] { str }).getBytes());
          else
            paramOutputStream.write(String.format("\n", new Object[] { str }).getBytes());
        }
      }
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
}

/* Location:
 * Qualified Name:     gov.ornl.config.ConfigEntry
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.6.1-SNAPSHOT
 */