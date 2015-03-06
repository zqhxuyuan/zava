package com.interview.design.pattern.behavioral;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午4:05
 *
 * Memento pattern is used to reduce where we want to restore state of an object to a previous state.
 *
 * 备忘录（Memento）模式又称标记（Token）模式。
 * GOF给备忘录模式的定义为：在不破坏封装性的前提下，捕获一个对象的内部状态，并在该对象之外保存这个状态。这样以后就可将该对象恢复到原先保存的状态。
 *
 * 使用了备忘录模式来实现保存对象的历史状态可以有效地保持封装边界。
 * 使用备忘录可以避免暴露一些只应由“备忘发起角色”管理却又必须存储在“备忘发起角色”之外的信息。
 * 把“备忘发起角色”内部信息对其他对象屏蔽起来, 从而保持了封装边界。
 * 但是如果备份的“备忘发起角色”存在大量的信息或者创建、恢复操作非常频繁，则可能造成很大的开销。
 *
 * GOF在《设计模式》中总结了使用备忘录模式的前提：
 * 1) 必须保存一个对象在某一个时刻的(部分)状态, 这样以后需要时它才能恢复到先前的状态。
 * 2) 如果一个用接口来让其它对象直接得到这些状态，将会暴露对象的实现细节并破坏对象的封装性。
 *
 */
public class MementoPattern {
    static class Memento {
        private String state;

        public Memento(String state){
            this.state = state;
        }

        public String getState(){
            return state;
        }
    }

    static class Originator {
        private String state;

        public void setState(String state){
            this.state = state;
        }

        public String getState(){
            return state;
        }

        public Memento saveStateToMemento(){
            return new Memento(state);
        }

        public void getStateFromMemento(Memento Memento){
            state = Memento.getState();
        }
    }

    static class CareTaker {
        private List<Memento> mementoList = new ArrayList<Memento>();

        public void add(Memento state){
            mementoList.add(state);
        }

        public Memento get(int index){
            return mementoList.get(index);
        }
    }

    public static void main(String[] args) {
        Originator originator = new Originator();
        CareTaker careTaker = new CareTaker();
        originator.setState("State #1");
        originator.setState("State #2");
        careTaker.add(originator.saveStateToMemento());
        originator.setState("State #3");
        careTaker.add(originator.saveStateToMemento());
        originator.setState("State #4");

        System.out.println("Current State: " + originator.getState());
        originator.getStateFromMemento(careTaker.get(0));
        System.out.println("First saved State: " + originator.getState());
        originator.getStateFromMemento(careTaker.get(1));
        System.out.println("Second saved State: " + originator.getState());
    }
}
