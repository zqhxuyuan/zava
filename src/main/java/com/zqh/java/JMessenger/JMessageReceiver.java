package com.zqh.java.JMessenger;

public interface JMessageReceiver<TMessage extends JMessage> {

    public void notifyReceiver(TMessage message);

}