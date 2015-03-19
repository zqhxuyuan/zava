package com.github.techrobby.SimplePubSub;

// 提供给客户端进行订阅,发布,取消订阅等方法
public interface JMessageHub {

    //发布一条消息: 第一个参数代表了消息类型==主题, 第二个参数是消息内容
    public <T extends JMessage> void publish(Class<T> messageClass, T message);

    //订阅一个主题: 因为发布的消息类型是Class,所以订阅一个主题第一个参数要传递Class,第二个参数是订阅者自己的回调实现
    public <T extends JMessage, R extends JMessageReceiver<T>> JMessageToken<T>
        subscribe(Class<T> messageClass, R handler);

    //取消订阅
    public <T extends JMessage> void unsubsribe(JMessageToken<T> token);

}