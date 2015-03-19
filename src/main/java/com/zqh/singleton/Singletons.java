package com.zqh.singleton;

public class Singletons{

    /**
     * 最简单实现方案
     * 优点：初次使用时实例化单例，避免资源浪费；
     缺点：
     （1）如果实例初始化非常耗时，初次使用时可能造成性能问题；
     （2）非线程安全，多线程下可能会有多个实例被初始化。
     */
    static class SingletonA {
        private static SingletonA instance = null;

        private SingletonA() {
        }

        public static SingletonA getInstance() {
            if (null == instance) {
                instance = new SingletonA();
            }
            return instance;
        }
    }

    /**
     * 线程安全方案
     * 优点：与方法1相比实现了线程安全；
     缺点：
     （1）初始化过程可能给初次使用时可能造成性能问题；
     （2）方法getInstance上的同步锁造成性能问题
     */
    static class SingletonB {
        private static SingletonB instance = null;

        private SingletonB () {
        }

        public static synchronized SingletonB getInstance() {
            if (null == instance) {
                instance = new SingletonB();
            }
            return instance;
        }
    }

    /**
     * 双重检查加锁方案
     */
    static class SingletonC2 {
        private static SingletonC2 instance = null;

        private SingletonC2 () {
        }
        //方法锁
        public static synchronized void staticInit(){
            if (null == instance) {
                instance = new SingletonC2();
            }
        }

        public static SingletonC2 getInstance() {
            if (null == instance) {
                staticInit();
            }
            return instance;
        }
    }
    static class SingletonC {
        private static SingletonC instance = null;

        private SingletonC () {
        }

        public static SingletonC getInstance() {
            if (null == instance) {
                //对象锁
                synchronized (SingletonC.class) {
                    if (null == instance) {
                        instance = new SingletonC();
                    }
                }
            }
            return instance;
        }
    }

    /**
     * 预先初始化方案
     * 优点：线程安全
     缺点：非懒加载，如果单例较大，构造完不是及时使用，会导致资源浪费。
     */
    static class SingletonD {
        private static SingletonD instance = new SingletonD();

        private SingletonD () {
        }

        public static synchronized SingletonD getInstance() {
            return instance;
        }
    }

    /**
     * 优点： Java机制决定内部类SingletonHolder只有在getInstance()方法
     * 第一次调用的时候才会被加载，而且其加载过程是线程安全。
     */
    static class SingletonE {
        private static class SingletonHolder{
            private static SingletonE instance = new SingletonE();
        }

        private SingletonE () {
        }

        public static synchronized SingletonE getInstance() {
            return SingletonHolder.instance;
        }
    }
}