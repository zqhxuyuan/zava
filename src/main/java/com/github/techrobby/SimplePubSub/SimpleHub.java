package com.github.techrobby.SimplePubSub;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by syynth on 6/22/14.
 */
public class SimpleHub implements JMessageHub {

    //消息类型->订阅了这个类型的消息的所有订阅者
    private HashMap<Class<?>, ArrayList<JMessageReceiver>> mapHandlers;

    //初始化
    public SimpleHub() {
        mapHandlers = new HashMap<>();
    }

    //发布
    @Override
    public <TMessage extends JMessage> void publish(Class<TMessage> messageClass, TMessage message) {
        // we can do nothing if no subscribers are available
        // 向某个消息类型发布一条消息. 首先要确保消息类型有订阅者
        if (mapHandlers.containsKey(messageClass)) {
            //取出所有订阅了这种类型的消息的订阅者
            ArrayList<JMessageReceiver> list = mapHandlers.get(messageClass);
            for (JMessageReceiver rec : list) {
                //同步回调
                //所以订阅者都要事先JMessageReceiver的notifyReceiver方法.
                rec.notifyReceiver(message);
            }
        }
    }

    //订阅
    @Override
    public <TMessage extends JMessage, TRec extends JMessageReceiver<TMessage>> JMessageToken<TMessage>
    subscribe(Class<TMessage> messageClass, TRec handler) {
        ArrayList<JMessageReceiver> list;
        if (mapHandlers.containsKey(messageClass)) {
            list = mapHandlers.get(messageClass);
        } else {
            //第一个订阅者
            list = new ArrayList<>();
            list.add(handler);
        }
        mapHandlers.put(messageClass, list);
        //返回封装了消息类型和订阅者具体实现类的Token
        return new JMessageToken<TMessage>(handler, messageClass);
    }

    @Override
    public <TMessage extends JMessage> void unsubsribe(JMessageToken<TMessage> token) {
        if (mapHandlers.containsKey(token.getMessageClass())) {
            ArrayList<JMessageReceiver> list = mapHandlers.get(token.getMessageClass());
            list.remove(token.getHandler());
        }
    }
}