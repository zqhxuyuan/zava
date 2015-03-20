package com.github.atemerev.hollywood.office;

import com.github.atemerev.hollywood.Actor;
import com.github.atemerev.hollywood.StateChangedEvent;
import com.github.atemerev.hollywood.annotations.AllowedStates;
import com.github.atemerev.hollywood.annotations.Initial;
import com.github.atemerev.hollywood.annotations.State;
import com.github.atemerev.hollywood.future.CompletedEvent;
import com.github.atemerev.hollywood.future.Promise;
import com.github.atemerev.pms.Listener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public abstract class Secretary extends Actor {

    // Public interface 公共接口

    //[在家里]> 老婆,我要去上班了
    @AllowedStates(AtHome.class)
    public abstract void goToWork();

    //[在工作]> 一天又开始了,看看有什么邮件/文件要处理
    @AllowedStates(Working.class)
    public abstract void acceptLetter(Letter letter);

    // States 状态

    //初始状态: 在家中
    @Initial
    @State
    public static abstract class AtHome extends Secretary {

        //要去上班了, 动作发生前允许的状态是在家里, 动作发生后, 将状态设置为工作中
        @AllowedStates(AtHome.class)
        public synchronized void goToWork() {
            setState(Working.class);
        }
    }

    //工作中
    @State
    public static abstract class Working extends Secretary {

        //要接电话,也要发传真
        protected Phone phone;
        protected Fax fax;

        //好多电话要打出去啊: 先来的先处理, 不要让最先打电话的人等最久
        protected Queue<Call> callsOnHold = new LinkedList<Call>();
        //还有一堆信件要放到邮箱里
        protected Set<Letter> letters = new HashSet<Letter>();

        // Commands

        public void acceptLetter(Letter letter) {
            letters.add(letter);
        }

        // Behavior

        //刚进入工作状态:刚到公司,把电话和传真都准备好了,要不然一天都不要干活了
        public void onEnter() {
            phone = Phone.instance();
            fax = Fax.instance();
        }

        //刚好不在工作的前一刻,要准备回家了
        public void onExit() {
            //把电话都处理完了再走
            processOnHoldCalls();
            //下班前还有最后一件事:把信件都放到邮局门口的信箱里
            //调用该方法会减少latch计数器.邮件放入完毕,会触发OfficeTest在latch的等待状态开始往后执行
            PostOffice.instance().send(letters);
        }

        //一天的工作结束了, 当EndDayEvent事件到来时, 将状态设置为在家中
        @Listener
        public synchronized void $(EndDayEvent e) {
            setState(AtHome.class);
        }

        //有电话打进来
        @Listener
        public synchronized void $(Call call) {
            //做好接听电话的准备
            OnCall newState = prepareState(OnCall.class);
            //这个call就是要接听的电话
            newState.call = call;
            //将状态设置为OnCall,则自动会触发OnCall的onEnter方法.
            //因为call参数会设置到OnCall.call中. 这样确保了进入OnCall.onEnter时,call就是要处理的那个电话
            setState(newState);
        }

        //处理等待通电话的业务: 有好几个电话都打过来了, 赶紧处理下吧
        public synchronized void processOnHoldCalls() {
            if (callsOnHold.size() != 0) {
                System.out.println("Processing calls on hold...");
                me().processMessage(callsOnHold.poll());
            }
        }
    }

    // 待命状态: 正在通话中
    @State
    public static abstract class OnCall extends Working {

        protected Call call;

        //刚刚通话时:第二个参数是问候信息
        //第一个参数call表示一定能取到等待通话的记录. 如果processOnHoldCalls没有需要通话的记录,
        //则不会进入OnCall状态. 一旦进入OnCall状态,call一定是从processOnHoldCalls取出的记录,一定是有值的.
        public void onEnter() {
            phone.respond(call, "- Corporate accounts payable, Nina speaking. Just a moment...", this);
        }

        @Listener
        public synchronized void $(String phrase) {
            //phrase是Call的参数. 这里的处理逻辑只是简单地把客户的信息打印出来
            System.out.println("- " + phrase);
            //这就算处理完毕了. 通知客户: 我们注意到了你的问题,会及时解决.再见.
            phone.say("- We aware of your problem and will resolve it ASAP. Goodbye.");

            //特殊用户有带来特殊的事件
            if (phrase.contains("fax")) {
                //客户打电话要求发传真
                FaxMessage message = new FaxMessage("our price list");
                //准备发送传真, 做好发送传真的准备
                SendingFax newState = prepareState(SendingFax.class);
                //这个message就是要发送的传真
                newState.faxToSend = message;
                //当设置状态为SendingFax时,首先触发StateChangedEvent事件的调用,打印了事件前后的状态:[OnCall] -> [SendingFax]
                //因为状态现在变为SendingFax,所以会立即进入SendingFax这个Actor的onEnter方法,开始执行发送传真的动作
                setState(newState);
            } else if (phrase.contains("go home")) {
                //老板打电话说可以可以回家了
                setState(AtHome.class);
            } else {
                //这个电话接听完毕,准备接听下一个电话
                ((Working) setState(Working.class)).processOnHoldCalls();
            }
        }

        @Listener
        public synchronized void $(Call call) {
            callsOnHold.add(call);
            System.out.println("Incoming call taken on hold...");
        }

        public void onExit() {
            phone.hangUp();
        }
    }

    // 发送传真
    @State
    public static abstract class SendingFax extends Working {

        FaxMessage faxToSend;

        public void onEnter() {
            //传真的发送是一个比较耗时的动作,我们要确保发送一定成功
            Promise<Void> sendPromise = fax.send(faxToSend);
            //发送后,要监听是否成功:把当前事件加入到监听器列表中
            sendPromise.listeners().add(me());
        }

        //发送传真的时候, 有电话打进来. 把电话加入到等待处理的列表中
        //为什么两者不能并行处理? 因为传真和电话共用一个网络.一次只能处理一种业务.
        @Listener
        public synchronized void $(Call call) {
            callsOnHold.add(call);
            System.out.println("Incoming call taken on hold...");
        }

        //传真完成后,基础处理其他事情. 首先要把状态改为工作中.
        //为什么不像OnCall使用onExist方法. 而是传递CompleteEvent事件.
        //因为发送传真是个异步的过程.onEnter调用完,实际上就已经到达onExist.
        //而我们要确保传真发送成功,传递CompleteEvent才表示这个操作成功完成.
        @Listener
        public synchronized void $(CompletedEvent e) {
            //发送传真完毕后,要手动调用处理等待通话的事件.
            ((Working) setState(Working.class)).processOnHoldCalls();
        }
    }

    @Listener
    public void $(StateChangedEvent e) {
        System.out.println();
        System.out.println("\t\t\t\t\t\t\t\t[" + prevState() + "] -> [" + state() + "]");
    }
}
