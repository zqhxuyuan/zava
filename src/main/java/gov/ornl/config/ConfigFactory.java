package gov.ornl.config;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigFactory
{
  private int configIDs = 0;
  private Map<Integer, Configuration> configs = new HashMap();
  private Lock configLock = new ReentrantLock();
  private ConfigFactory.XMLReader reader = new ConfigFactory.XMLReader(this);

  public ConfigFactory()
  {
  }

  public Configuration getConfig(String paramString)
  {
    String str;
    if (paramString.charAt(0) == System.getProperty("file.separator").charAt(0))
      str = paramString;
    else
      str = "config" + System.getProperty("file.separator") + paramString;
    File localFile = new File(str);
    if (localFile.exists())
    {
      int i = createConfiguration();
      addResource(i, localFile);
      return getConfiguration(i);
    }
    return null;
  }

  private int createConfiguration()
  {
    this.configLock.lock();
    int i = this.configIDs++;
    this.configs.put(Integer.valueOf(i), new Configuration());
    this.configLock.unlock();
    return i;
  }

  private void addResource(int paramInt, File paramFile)
  {
    this.configLock.lock();
    Configuration localConfiguration = (Configuration)this.configs.get(Integer.valueOf(paramInt));
    if (localConfiguration != null)
    {
      localConfiguration.addPath(paramFile);
      this.reader.parseConfig(localConfiguration, paramFile);
    }
    this.configLock.unlock();
  }

  public Configuration getConfiguration(int paramInt)
  {
    this.configLock.lock();
    Configuration localConfiguration = (Configuration)this.configs.get(Integer.valueOf(paramInt));
    if (localConfiguration != null)
      this.configs.remove(Integer.valueOf(paramInt));
    this.configLock.unlock();
    return localConfiguration;
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      ConfigFactory localConfigFactory = new ConfigFactory();
      int i = localConfigFactory.createConfiguration();
      localConfigFactory.addResource(i, new File("../../config/cluster-global.xml"));
      Configuration localConfiguration = localConfigFactory.getConfiguration(i);
      localConfiguration.write(System.out);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

    class XMLReader
    {
        private SAXBuilder builder = new SAXBuilder();

        public XMLReader(ConfigFactory paramConfigFactory)
        {
        }

        private Map<String, ConfigEntry> parseProperty(List<?> paramList)
        {
            HashMap localHashMap = new HashMap();
            Iterator localIterator = paramList.iterator();
            while (localIterator.hasNext())
            {
                ConfigEntry localConfigEntry = null;
                Element localElement = (Element)localIterator.next();
                List localList = localElement.getChildren();
                Object localObject;
                if (localList.size() > 0)
                {
                    localObject = parseProperty(localList);
                    if (((Map)localObject).size() > 0)
                        localConfigEntry = new ConfigEntry((Map)localObject);
                }
                else
                {
                    localObject = new ArrayList();
                    String[] arrayOfString1 = localElement.getTextNormalize().split(",");
                    for (String str1 : arrayOfString1)
                    {
                        String str2 = str1.trim();
                        if (!str2.equals(""))
                            ((List)localObject).add(str1);
                    }
                    if (((List)localObject).size() > 0)
                        localConfigEntry = new ConfigEntry((List)localObject);
                }
                if (localConfigEntry != null)
                    localHashMap.put(localElement.getName(), localConfigEntry);
            }
            return localHashMap;
        }

        protected void parseConfig(Configuration paramConfiguration, File paramFile)
        {
            try
            {
                Document localDocument = this.builder.build(paramFile);
                Element localElement = localDocument.getRootElement();
                if (localElement.getName().equals("configuration"))
                {
                    List localList = localElement.getChildren();
                    Iterator localIterator = localList.iterator();
                    while (localIterator.hasNext())
                    {
                        localElement = (Element)localIterator.next();
                        if (localElement.getName().equals("property"))
                        {
                            Map localMap = parseProperty(localElement.getChildren());
                            if (localMap.get("name") != null)
                            {
                                ConfigEntry localConfigEntry = new ConfigEntry(localMap);
                                paramConfiguration.set(((ConfigEntry)localMap.get("name")).getValue(0), localConfigEntry);
                            }
                        }
                    }
                }
            }
            catch (Exception localException)
            {
                localException.printStackTrace();
            }
        }
    }
}

/* Location:
 * Qualified Name:     gov.ornl.config.ConfigFactory
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.6.1-SNAPSHOT
 */