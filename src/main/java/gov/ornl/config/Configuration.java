package gov.ornl.config;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Configuration
  implements Iterable<Map.Entry<String, ConfigEntry>>
{
  private List<File> paths = new ArrayList();
  private Map<String, ConfigEntry> config = new HashMap();

  public Configuration()
  {
  }

  public void merge(Configuration paramConfiguration)
  {
    if (paramConfiguration != null)
    {
      Iterator localIterator = paramConfiguration.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry<String, ConfigEntry> localEntry = (Map.Entry)localIterator.next();
        this.config.put(localEntry.getKey(), localEntry.getValue());
      }
    }
  }

  protected void addPath(File paramFile)
  {
    this.paths.add(paramFile);
  }

  public ConfigEntry get(String paramString)
  {
    return (ConfigEntry)this.config.get(paramString);
  }

  protected void set(String paramString, ConfigEntry paramConfigEntry)
  {
    this.config.put(paramString, paramConfigEntry);
  }

  public Iterator<Map.Entry<String, ConfigEntry>> iterator()
  {
    return this.config.entrySet().iterator();
  }

  public void write(OutputStream paramOutputStream)
  {
    try
    {
      paramOutputStream.write("<configuration>\n".getBytes());
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        paramOutputStream.write("<property>\n".getBytes());
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((ConfigEntry)localEntry.getValue()).write(paramOutputStream);
        paramOutputStream.write("</property>\n".getBytes());
      }
      paramOutputStream.write("</configuration>\n".getBytes());
      paramOutputStream.flush();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}

/* Location:
 * Qualified Name:     gov.ornl.config.Configuration
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.6.1-SNAPSHOT
 */