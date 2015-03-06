package com.interview.design.pattern.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午9:30
 *
 * Publish-Subscribe机制是个不错的选择，它的耦合性低，各个参与者之间毫无关联。
 * 每一个消息都有一个唯一标识，一般都用字符串来描述，
 *
 * 比如用户管理模块中新添加了一个用户，于是它发送了一个消息：/UserManagment/User/Add，
 * 消息的其他信息可以放置到一个Map中。
 *
 * In large scale system, it could be MessageQueue store all the messages, topic is Queue ID.
 *
 * http://www.cnblogs.com/west-link/archive/2011/11/21/2256788.html
 */
public class PublishSubscribePattern {
    static interface MessageSubscriber {
        public void onRecived(String message, Map params);
    }

    static class NewsLetterEmailSender implements MessageSubscriber{
        @Override
        public void onRecived(String message, Map params) {
            String name = params.get("name").toString();
            String email = params.get("email").toString();
            System.out.printf("Sending news letter to %s at email %s\n", name, email);
        }
    }

    static class NewsLetterInsiteSender implements MessageSubscriber{
        @Override
        public void onRecived(String message, Map params) {
            String id = params.get("id").toString();
            System.out.printf("Sending news letter to id %s by in-site message\n", id);
        }
    }

    static class MessagePublisher {
        private static MessagePublisher singleton;
        private static Map<String,ArrayList<MessageSubscriber>> subscribers;

        private MessagePublisher(){}

        public static MessagePublisher instance(){
            if(singleton == null)
                singleton = new MessagePublisher();
            return singleton;
        }

        public void register(String message, MessageSubscriber subscriber){
            if(subscriber == null)
                return;
            if(subscribers == null)
                subscribers = new HashMap<String, ArrayList<MessageSubscriber>>();
            ArrayList<MessageSubscriber> subscriberList = subscribers.get(message);
            if(subscriberList == null){
                subscriberList = new ArrayList<MessageSubscriber>();
                subscribers.put(message, subscriberList);
            }
            subscriberList.add(subscriber);
        }

        public void publish(String message, Map params){
            if(subscribers == null)
                return;
            ArrayList<MessageSubscriber> subscriberList = subscribers.get(message);
            if(subscriberList == null || subscriberList.isEmpty())
                return;
            for (MessageSubscriber topicSubscriber : subscriberList)
                topicSubscriber.onRecived(message,params);
        }
    }

    public static void main(String[] args){
        NewsLetterEmailSender emailSender = new NewsLetterEmailSender();
        MessagePublisher.instance().register("/UserManagment/User/Add", emailSender);
        NewsLetterInsiteSender messageSender = new NewsLetterInsiteSender();
        MessagePublisher.instance().register("/UserManagment/User/Add", messageSender);

        Map params = new HashMap();
        params.put("id", "summerzhao");
        params.put("name", "Summer Zhao");
        params.put("email", "******@gmail.com");
        MessagePublisher.instance().publish("/UserManagment/User/Add", params);


    }
}
