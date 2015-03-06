package com.zqh.java.JMessenger;

public interface JMessageHub {

    public <TMessage extends JMessage> void publish(Class<TMessage> messageClass, TMessage message);

    public <TMessage extends JMessage, TReceiver extends JMessageReceiver<TMessage>> JMessageToken<TMessage>
    subscribe(Class<TMessage> messageClass, TReceiver handler);

    public <TMessage extends JMessage> void unsubsribe(JMessageToken<TMessage> token);

}