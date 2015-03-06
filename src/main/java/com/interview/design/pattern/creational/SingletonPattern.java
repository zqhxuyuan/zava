package com.interview.design.pattern.creational;

/**
 * Created_By: stefanie
 * Date: 14-12-2
 * Time: 下午9:14
 *
 * 单例对象能保证在一个JVM中，该对象只有一个实例存在
 *
 * The difference between Singleton and Static Method
 * 首先，静态类不能实现接口。（从类的角度说是可以的，但是那样就破坏了静态了。因为接口中不允许有static修饰的方法，所以即使实现了也是非静态的）
 * 其次，单例可以被延迟初始化，静态类一般在第一次加载是初始化。之所以延迟加载，是因为有些类比较庞大，所以延迟加载有助于提升性能。
 * 再次，单例类可以被继承，他的方法可以被覆写。但是静态类内部方法都是static，无法被覆写。
 * 最后一点，单例类比较灵活，毕竟从实现上只是一个普通的Java类，只要满足单例的基本需求，你可以在里面随心所欲的实现一些其它功能，但是静态类不行。
 */
public class SingletonPattern {

    static class SingletonI{
        public static SingletonI INSTANCE = new SingletonI();      //have a problem, if go exception in constructor, then INSTANCE will always be null,
                                                                    // better to put in getINSTANCE.
        private SingletonI(){

        }
    }

    static class SingletonII{
        private static SingletonII INSTANCE;

        private SingletonII(){

        }

        public static SingletonII getInstance(){
            if(INSTANCE == null){
                INSTANCE = new SingletonII();
            }
            return INSTANCE;
        }
    }

    //Singleton Pattern for Multi-Thread Accessing
    static class SingletonIII{
        private static SingletonIII INSTANCE;
        private SingletonIII() {
        }

        private static synchronized void syncInit() {  //separate creation and make it synchronize
            if (INSTANCE == null) {
                INSTANCE = new SingletonIII();
            }
        }

        public static SingletonIII getINSTANCE() {
            if (INSTANCE == null) {
                syncInit();
            }
            return INSTANCE;
        }
    }
}
