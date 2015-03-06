package com.interview.design.pattern.behavioral;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午5:46
 *
 * Mediator pattern is used to reduce communication complexity between multiple objects or classes.
 * This pattern provides a mediator class which normally handles all the communications between different classes
 * and supports easy maintainability of the code by loose coupling.
 *
 * 中介者模式也是用来降低类类之间的耦合的，因为如果类类之间有依赖关系的话，不利于功能的拓展和维护，因为只要修改一个对象，其它关联的对象都得进行修改。
 * 如果使用中介者模式，只需关心和Mediator类的关系，具体类类之间的关系及调度交给Mediator就行
 */
public class MediatorPattern {

    static class Message{
        public static String EVERYONE = "all";
        public String from;
        public String to;
        public String message;

        Message(String from, String to, String message) {
            this.from = from;
            this.to = to;
            this.message = message;
        }
    }

    static interface ChatMediator {

        public void sendMessage(Message msg, User user);

        void addUser(User user);
    }

    static abstract class User {
        protected ChatMediator mediator;
        protected String name;

        public User(ChatMediator med, String name){
            this.mediator=med;
            this.name=name;
        }

        public abstract void send(String msg, String to);

        public abstract void receive(Message msg);
    }

    static class ChatMediatorImpl implements ChatMediator {

        private HashMap<String, User> users;

        public ChatMediatorImpl(){
            this.users=new HashMap<>();
        }

        @Override
        public void addUser(User user){
            this.users.put(user.name, user);
        }

        @Override
        public void sendMessage(Message msg, User user) {
            if(Message.EVERYONE.equalsIgnoreCase(msg.to)){
                for(User u : this.users.values()){
                    //message should not be received by the user sending it
                    if(u != user)   u.receive(msg);
                }
            } else {
                User u = users.get(msg.to);
                if(u != null) u.receive(msg);
            }
        }
    }


    static class UserImpl extends User {

        public UserImpl(ChatMediator med, String name) {
            super(med, name);
        }

        @Override
        public void send(String msg, String to){
            System.out.println(this.name+": Sending Message: "+msg);
            mediator.sendMessage(new Message(this.name, to, msg), this);
        }
        @Override
        public void receive(Message msg) {
            System.out.println(this.name+": Received Message from " +  msg.from + ": "+ msg.message);
        }

    }

    public static void main(String[] args) {
        ChatMediator mediator = new ChatMediatorImpl();
        User user1 = new UserImpl(mediator, "Pankaj");
        User user2 = new UserImpl(mediator, "Lisa");
        User user3 = new UserImpl(mediator, "Saurabh");
        User user4 = new UserImpl(mediator, "David");
        mediator.addUser(user1);
        mediator.addUser(user2);
        mediator.addUser(user3);
        mediator.addUser(user4);

        user1.send("Hi All", Message.EVERYONE);
        System.out.println();
        user1.send("Hello David", "David");
    }

}
