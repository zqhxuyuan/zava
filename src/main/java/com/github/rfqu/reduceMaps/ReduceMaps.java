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

        //将Map加入队列中. 队列采用LinkedList实现
        public synchronized void put(Map map) {
            queue.add(map);
            //队列中有数据,通知其他线程取数据
            notifyAll();
        }

        //停止生成数据,但是队列中不一定就没有数据了.只是说从此开始,队列不会有新数据生成了
        public synchronized void stop() {
            genStopped = true;
            notifyAll();
        }

        public synchronized Map takeFirst() {
            for (;;) {
                //取队列的第一个元素. LinkedList链表. 最开始加入的元素在表头.
                //所以最先加入的,会被最先取出来, 实现了队列的FIFO功能
                Map res = queue.poll();
                if (res != null) {
                    threadcount++;
                    return res;
                }
                if (genStopped) {
                    System.out.println("takeFirst failed");
                    return null;
                }
                //如果队列中没有数据且生成器没有停止生成数据,则等待.直到队列中有数据时,会触发通知,这里不再等待
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public synchronized Map takeNext(Map myMap) {
            for (;;) {
                //
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
                //模拟产生Map数据,并把多个Map加入到List中. mapQueue中一旦有Map产生,就会通知其他线程取数据
                mapQueue.put(map);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //停止生成数据
            mapQueue.stop();
        }

    }

    class MapReducer implements Runnable {
        Map myMap;
        int threadNum=threadNums.incrementAndGet();

        void print(String msg, Map... maps) {
            StringBuilder sb = new StringBuilder();
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
            //取队列的第一个元素
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
