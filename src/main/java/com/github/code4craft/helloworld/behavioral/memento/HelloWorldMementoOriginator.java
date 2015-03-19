package com.github.code4craft.helloworld.behavioral.memento;

import com.github.code4craft.helloworld.HelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldMementoOriginator implements HelloWorld{

    private String state;

    public HelloWorldMementoOriginator set(String state) {
        this.state = state;
        return this;
    }

    public Memento saveToMemento() {
        return new Memento(state);
    }

    public void restoreFromMemento(Memento memento) {
        state = memento.getSavedState();
    }

    @Override
    public String helloWorld() {
        return state;
    }

    public static class Memento {
        private final String state;

        public Memento(String stateToSave) {
            state = stateToSave;
        }

        public String getSavedState() {
            return state;
        }
    }
}
