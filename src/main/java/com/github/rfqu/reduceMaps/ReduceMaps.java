package com.github.rfqu.reduceMaps;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * http://stackoverflow.com/questions/23179438/multithreaded-merge-best-java-threading-practices-recommended
 * 
 * @author rfq
 *
 */
public class ReduceMaps {
    AtomicInteger threadNums = new AtomicInteger();

    MapQueue mapQueue = new MapQueue();

    static class MapQueue {
        LinkedList<Map> queue = new LinkedList<Map>();
        int threadcount = 0;
        boolean genStopped = false;

        public synchronized void put(Map map) {
            queue.add(map);
            notifyAll();
        }

        public synchronized void stop() {
            genStopped = true;
            notifyAll();
        }
        public synchronized Map takeFirst() {
            for (;;) {
                Map res = queue.poll();
                if (res != null) {
                    threadcount++;
                    return res;
                }
                if (genStopped) {
                    System.out.println("takeFirst failed");
                    return null;
                }
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public synchronized Map takeNext(Map myMap) {
            for (;;) {
                Map res=queue.poll();
                if (res != null) {
                    return res;
                }
                if (genStopped) {
                    threadcount--;
                    if (threadcount == 0) {
                        // all done - push the resulting map further
                        System.out.println("all done");
                    } else {
                        put(myMap);
                    }
                    notifyAll();
                    return null;
                }
                try {
                    wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    class MapGenerator implements Runnable {

        @Override
        public void run() {
            for (int k = 0; k < 10; k++) {
                HashMap map = new HashMap();
                map.put("n", k);
                System.out.println("put "+k);
                mapQueue.put(map);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mapQueue.stop();
        }

    }

    class MapReducer implements Runnable {
        Map myMap;
        int threadNum=threadNums.incrementAndGet();

        void print(String msg, Map... maps) {
            StringBuilder sb=new StringBuilder();
            sb.append("thr: ").append(threadNum).append(" :").append(msg);
            for (Map map: maps) {
                sb.append(" #");
                if (map==null) {
                    sb.append("null");
                } else {
                    Integer n = ((Integer) map.get("n"));
                    sb.append(n);
                }
            }
            System.out.println(sb.toString());
        }
        
        @Override
        public void run() {
            myMap=mapQueue.takeFirst();
            if (myMap==null) {
                print("takeFirst failed");
                return;
             }
             print("takeFirst success", myMap);
             for (;;) {
                Map other=mapQueue.takeNext(myMap);
                if (other==null) {
                    print("exiting", myMap);
                    return;
                }
                print("takeNext success", other);
                merge(other);
            }

        }

        void merge(Map other) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            print("merged", myMap, other);
        }
    }

    void test() {
        new Thread(new MapGenerator()).start();
        for (int k = 0; k < 3; k++) {
            new Thread(new MapReducer()).start();
        }
    }

    public static void main(String[] args) {
        new ReduceMaps().test();
    }

}
