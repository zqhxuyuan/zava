package com.interview.design.pattern.other;

import java.util.Date;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午9:06
 *
 * Archetype设计模式的目的是将业务处理逻辑和具体实现分离，所以至少需要两个参与者：Decorator和Delegate，
 * 它们都实现同一个接口，Decorator负责处理业务逻辑，而Delegate负责具体的实现，在Decorator的通用业务逻辑
 * 处理过程中，会把具体实现委派给Delegate。
 *
 * http://www.cnblogs.com/west-link/archive/2011/06/16/2082422.html
 */
public class ArchetypePattern {

    static interface EventRecorder{
        // 记录事件的内容
        public void record(String event);
    }

    static abstract class EventRecorderDecorator implements EventRecorder{
        protected EventRecorderDelegate delegate;

        public void setDelegate(EventRecorderDelegate delegate){
            this.delegate = delegate;
        }

        public String ellipseText(String msg){
            if(msg.length() > 100) return msg.substring(0, 100) + "...";
            else return msg;
        }
    }

    static abstract class EventRecorderDelegate implements EventRecorder{

    }

    // 简单的事件记录类
    static class SimpleEventRecorder extends EventRecorderDecorator{

        public void record(String event){
            // 附加当前的日期到事件的内容中
            event = "[" + new Date().toString() + "]" + event;
            // 当内容过长时省略显示
            event = ellipseText(event);
            // 记录事件的内容
            delegate.record(event);
        }
    }

    // 复杂的事件记录类
    static class ComplicateEventRecorder extends EventRecorderDecorator{

        public void record(String event){
            // 附加当前的日期到事件的内容中
            event = "[" + new Date().toString() + "]" + event;
            // 附加当前异常信息到事件的内容中
            event = event+getExceptionText();
            // 附加当前的内存、CPU占用率到事件的内容中
            event = event+getMachineStatus();
            // 当内容过长时省略显示
            event = ellipseText(event);
            // 记录事件的内容
            delegate.record(event);
        }

        public String getExceptionText(){
            return "\n Exception Detail: .....";
        }

        public String getMachineStatus(){
            return "\n Machine Status Detail: .....";
        }
    }

    static class RecordEventToDatabase extends EventRecorderDelegate{
        public void record(String event){
            System.out.println("Save Record to DB");
            System.out.println(event);
        }
    }

    static class RecordEventToFile extends EventRecorderDelegate{
        public void record(String event){
            System.out.println("Save Record to File");
            System.out.println(event);
        }
    }

    static class RecordEventByEmail extends EventRecorderDelegate{
        public void record(String event){
            System.out.println("Save Record to Email");
            System.out.println(event);
        }
    }

    static class EventRecorderFactory{

        public static EventRecorder create(int type, int flag){
            EventRecorderDelegate delegate = null;
            EventRecorderDecorator decorator = null;

            if(type == 0)
                decorator = new SimpleEventRecorder();
            if(type == 1)
                decorator = new ComplicateEventRecorder();

            if(flag == 0)
                delegate = new RecordEventToDatabase();
            if(flag == 1)
                delegate = new RecordEventToFile();
            if(flag == 2)
                delegate = new RecordEventByEmail();
            // 设置代理
            decorator.setDelegate(delegate);
            return decorator;
        }
    }

    public static void main(String[] args){
        EventRecorder recorder = EventRecorderFactory.create(0, 0);
        recorder.record("Sample Event1");

        recorder = EventRecorderFactory.create(1, 2);
        recorder.record("Sample Event2");
    }
}
