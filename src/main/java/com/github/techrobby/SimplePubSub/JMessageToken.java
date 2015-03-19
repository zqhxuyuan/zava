package com.github.techrobby.SimplePubSub;

public class JMessageToken<TMessage extends JMessage> {

    private JMessageReceiver handler;
    private Class<TMessage> messageClass;

    JMessageToken(JMessageReceiver<?> handler, Class<TMessage> messageClass) {
        this.handler = handler;
        this.messageClass = messageClass;
    }

    JMessageReceiver getHandler() {
        return handler;
    }

    Class<TMessage> getMessageClass() {
        return messageClass;
    }

}