package com.github.atemerev.pms.examples.routine;

import com.github.atemerev.pms.examples.helloworld.Evening;
import com.github.atemerev.pms.examples.helloworld.HelloWorldHandler;
import com.github.atemerev.pms.examples.helloworld.Morning;
import com.github.atemerev.pms.listeners.MessageListenerDelegate;
import com.github.atemerev.pms.listeners.dispatch.DispatchListener;

/**
 * 广播: 消息发送给多个监听器
 * @author Alexander Temerev
 * @version $Id$
 */
public class Main {
    public static void main(String[] args) {
        //testLife();
        testDelegate();
    }

    public static void testLife(){
        //消息分发路由器: 在Life内部引用了MessageListenerDelegate.
        //添加监听器,处理消息的逻辑都会交给MessageListenerDelegate去执行
        Life life = new Life();

        //MessageListenerDelegate要依赖于DispatchListener
        //否则无法回调到自定义Handler类的事件处理逻辑.
        //1.DispatchListener的实例化可以是接收一个类作为参数
        DispatchListener helloWorld = new DispatchListener(new HelloWorldHandler());
        //2.或者直接继承:DailyRoutineHandler继承了DispatchListener
        DailyRoutineHandler dailyRoutine = new DailyRoutineHandler();

        //加入listeners列表中的是MessageListener监听器实现类
        //一个事件发给多个监听器, 每个监听器都会对事件做出响应
        life.listeners().add(helloWorld);
        life.listeners().add(dailyRoutine);

        life.processMessage(new Morning());
        System.out.println("============");
        life.processMessage(new Evening());
    }

    public static void testDelegate(){
        MessageListenerDelegate delegate = new MessageListenerDelegate();

        DispatchListener helloWorld = new DispatchListener(new HelloWorldHandler());
        DailyRoutineHandler dailyRoutine = new DailyRoutineHandler();

        delegate.listeners().add(helloWorld);
        delegate.listeners().add(dailyRoutine);

        delegate.processMessage(new Morning());
        System.out.println("================");
        delegate.processMessage(new Evening());
    }
}
