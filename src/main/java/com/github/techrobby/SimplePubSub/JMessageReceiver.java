package com.github.techrobby.SimplePubSub;

// 监听器接口, 订阅者要实现监听器接口, 在消息到达时, 做出响应
public interface JMessageReceiver<TMessage extends JMessage> {

    // 订阅者实现该方法,完成消息到达时的通知处理.
    public void notifyReceiver(TMessage message);

}