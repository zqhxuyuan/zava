package com.zqh.singleton;

import java.util.Vector;

/**
 * http://www.ibm.com/developerworks/cn/java/l-singleton/
 *
 */
public class GlobalConfig {
    private static GlobalConfig instance;
    private Vector properties = null;
    private boolean isUpdating = false;
    private int readCount = 0;

    private GlobalConfig() {
        //Load configuration information from DB or file Set values for properties
    }
    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new GlobalConfig();
        }
    }
    public static GlobalConfig getInstance() {
        if (instance==null) {
            syncInit();
        }
        return instance;
    }

    // 单例对象的属性更新同步
    //----------------------------------------------
    // 读者/写者的处理方式
    // 设置一个读计数器，每次读取配置信息前，将计数器加1，读完后将计数器减1。
    // 只有在读计数器为0时，才能更新数据，同时要阻塞所有读属性的调用
    public synchronized void update(String p_data) {
        syncUpdateIn();
        //Update properties
        properties.set(0, p_data);
    }
    private synchronized void syncUpdateIn() {
        while (readCount > 0) {
            try {
                wait();
            } catch (Exception e) {
            }
        }
    }
    private synchronized void syncReadIn() {
        readCount++;
    }
    private synchronized void syncReadOut() {
        readCount--;
        notifyAll();
    }
    public Vector getProperties() {
        syncReadIn();
        //Process data
        syncReadOut();
        return properties;
    }

    //----------------------------------------------
    // 影子实例: 在更新方法中，通过生成新的GlobalConfig的实例，从文件或数据库中得到最新配置信息，并存放到properties属性中
    public Vector getProperties2() {
        return properties;
    }
    public void updateProperties() {
        //Load updated configuration information by new a GlobalConfig object
        GlobalConfig shadow = new GlobalConfig();
        properties = shadow.getProperties();
    }
}