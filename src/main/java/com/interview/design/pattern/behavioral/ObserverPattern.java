package com.interview.design.pattern.behavioral;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午2:56
 *
 * Observer pattern is used when there is one to many relationship between objects.
 * such as if one object is modified, the objects depended on it are to be notified automatically.
 *
 * 当一个对象变化时，其它依赖该对象的对象都会收到通知，并且随着变化！对象之间是一种一对多的关系
 * 类似Event Handler
 */
public class ObserverPattern {

    static abstract class Observer {
        protected Subject subject;
        public abstract void update();
    }

    static class Subject {

        private List<Observer> observers
                = new ArrayList<Observer>();
        private int state;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
            notifyAllObservers();
        }

        public void attach(Observer observer){
            observers.add(observer);
        }

        public void notifyAllObservers(){
            for (Observer observer : observers) {
                observer.update();
            }
        }
    }

    static class BinaryObserver extends Observer{

        public BinaryObserver(Subject subject){
            this.subject = subject;
            this.subject.attach(this);
        }

        @Override
        public void update() {
            System.out.println( "Binary String: "
                    + Integer.toBinaryString( subject.getState() ) );
        }
    }

    static class OctalObserver extends Observer{

        public OctalObserver(Subject subject){
            this.subject = subject;
            this.subject.attach(this);
        }

        @Override
        public void update() {
            System.out.println( "Octal String: "
                    + Integer.toOctalString( subject.getState() ) );
        }
    }

    static class HexaObserver extends Observer{

        public HexaObserver(Subject subject){
            this.subject = subject;
            this.subject.attach(this);
        }

        @Override
        public void update() {
            System.out.println( "Hex String: "
                    + Integer.toHexString( subject.getState() ).toUpperCase() );
        }
    }

    public static void main(String[] args) {
        Subject subject = new Subject();

        new HexaObserver(subject);
        new OctalObserver(subject);
        new BinaryObserver(subject);

        System.out.println("First state change: 15");
        subject.setState(15);
        System.out.println("Second state change: 10");
        subject.setState(10);
    }
}
