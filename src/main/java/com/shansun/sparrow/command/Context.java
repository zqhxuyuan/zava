package com.shansun.sparrow.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理器上下文
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-5-3
 */
public class Context implements Cloneable{

    private static final long serialVersionUID = 1L;

    protected final static Logger logger = LoggerFactory.getLogger(Context.class);

    /**
     * 合作伙伴在初始化数据的时候，存放的一些扩展性信息
     * <p/>
     * 所有的 可以 ContextMapKey
     */
    private Map<String, Object> properties = new HashMap<String, Object>();

    private List<String> messages = new ArrayList<String>();

    public Object getProperty(String key) {

        Object value = properties.get(key.trim());
        if (value == null)
            return null;
        return value;
    }

    public Boolean test(String key) {
        try {
            Boolean property = getBooleanProperty(key);
            if (property != null) {
                return property;
            } else {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            logger.warn("获取上下文内容异常", e);
            return Boolean.FALSE;
        }
    }

    public Boolean getBooleanProperty(String key) {
        return (Boolean) getProperty(key);
    }

    public Integer getIntProperty(String key) {
        return (Integer) getProperty(key);
    }

    public Long getLongProperty(String key) {
        return (Long) getProperty(key);
    }

    public String getStringProperty(String key) {
        return (String) getProperty(key);
    }

    public Context addProperty(String key, Object value) {

        return addSerializableExtend(key, value);
    }

    protected Context addSerializableExtend(String key, Object value) {

        if (value == null || key == null)
            return this;
        properties.put(key.trim(), value);
        return this;
    }

    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public Context clone() {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(byteOut);
            oos.writeObject(this);
            ois = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
            return (Context) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("克隆上下文时出错.", e);
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                // Do Nothing
            }
        }
    }

    public Map<String, Object> getProperties() {

        return properties;
    }

    public void setProperties(Map<String, Object> properties) {

        this.properties = properties;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessages(String message) {
        if (messages == null) {
            messages = new ArrayList<String>();
        }
        messages.add(message);
    }

}