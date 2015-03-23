package com.zqh.cache;

/**
 * Created by zqhxuyuan on 15-3-23.
 */
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * http://www.sccoder.com/algorithm/consistenthashing.html
 * http://weblogs.java.net/blog/2007/11/27/consistent-hashing
 * 利用JDK中TreeMap的排序功能进行hash环的映射
 * @author 亦凡 一致性hash算法的实现
 */
public class ConsistencyHash<T> {

    /**
     * 存储服务器信息，key为服务器的hash值，TreeMap根据key进行了排序，便于查找映射节点
     * @uml.property  name="circle"
     * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Long" qualifier="valueOf:java.lang.Long java.lang.Object"
     */
    private TreeMap<Long, T> circle = null;
    /**
     * @uml.property  name="numberOfReplicas"
     */
    private int numberOfReplicas;

    public ConsistencyHash() {
        circle = new TreeMap<Long, T>();
    }

    public ConsistencyHash(T[] servers) {
        circle = new TreeMap<Long, T>();
        for (T server : servers) {
            addServer(server);
        }
    }

    public ConsistencyHash(T[] servers, int numberOfReplicas) {
        circle = new TreeMap<Long, T>();
        this.numberOfReplicas = numberOfReplicas;
        for (T server : servers) {
            addServer(server);
        }
    }

    /**
     * 映射服务器到hash环上
     */
    public void addServer(T server) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hash(server.toString() + i), server);
        }
    }

    /**
     * 从hash环上移除服务器
     */
    public void removeServer(T server) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hash(server.toString() + i));
        }
    }

    /**
     * 根据key的hash值在hash环上的映射查找key映射的服务器
     *
     * @param keyHash
     * @return
     */
    public T getServerNode(Long keyHash) {
        if (circle == null || circle.isEmpty()) {
            return null;
        }
        SortedMap<Long, T> tailMap = circle.tailMap(keyHash);
        if (tailMap.isEmpty()) {
            keyHash = circle.firstKey();
        } else {
            keyHash = tailMap.firstKey();
        }
        return circle.get(keyHash);
    }

    /**
     * 打印server节点映射顺序
     */
    public void printServerMapOrder() {
        System.out.println(circle);
    }

    /**
     * 计算hash
     *
     * @param obj
     * @return
     */
    public static long hash(Object obj) {
        byte[] data = DigestUtils.md5(obj.toString().getBytes());
        return data[0] | ((long) data[1] << 8) | ((long) data[2] << 16)
                | ((long) data[3] << 24) | ((long) data[4] << 32)
                | ((long) data[5] << 40) | ((long) data[6] << 48)
                | ((long) data[7] << 56);
    }

    /**
     * byte数组转化为long数组
     *
     * @param byteArray
     * @return
     */
    public static long byteToLong(byte[] byteArray) {
        return Long.parseLong(new String(byteArray));
    }

    public static void main(String[] args) {

        String[] servers = new String[] { "Server 1:192.168.1.1",
                "Server 2:192.168.1.2", "Server 3:192.168.1.3",
                "Server 4:192.168.1.4", "Server 5:192.168.1.5" };

        ConsistencyHash<String> consHash = new ConsistencyHash<String>(servers);
        System.out.println("服务器映射信息：");
        consHash.printServerMapOrder();
        System.out.println("数据映射信息：");
        showDataMap(consHash);
        // 移除server2
        consHash.removeServer(servers[2]);
        System.out.println("移除server 3后数据映射信息：");
        showDataMap(consHash);

    }

    public static void showDataMap(ConsistencyHash<String> consHash) {
        for (int i = 0; i < 5; i++) {
            System.out.println("Data" + i + " mapped at " + consHash.getServerNode(ConsistencyHash.hash("Data" + i)));
        }
    }

}