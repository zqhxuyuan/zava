package com.ibm.jactors.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ibm.jactors.Actor;
import com.ibm.jactors.DefaultActorManager;
import com.ibm.jactors.DefaultMessage;
import com.ibm.jactors.Message;

/**
 * An actor that sends messages while counting down a send count. 
 * 
 * @author BFEIGENB
 *
 */
public class TestActor extends TestableActor {

	@Override
	public void activate() {
		logger.trace("TestActor activate: %s", this);
		super.activate();
	}

	@Override
	public void deactivate() {
		logger.trace("TestActor deactivate: %s", this);
		super.deactivate();
	}

    // runBody 方法是在收到任何消息之前、首次创建 actor 的时候调用的。它通常用于将第一批消息引导至 actor
	@Override
	protected void runBody() {
		// logger.trace("TestActor:%s runBody: %s", getName(), this);
		DefaultActorTest.sleeper(1);
		DefaultMessage m = new DefaultMessage("init", 8);
		getManager().send(m, null, this);
	}

    // 在 actor 即将收到消息时调用；这里 actor 可拒绝或接受消息
    @Override
    protected Message testMessage(){
        return super.testMessage();
    }

    // 在 actor 收到一条消息时调用。在通过较短延迟来模拟某种一般性处理之后，才开始处理该消息
	@Override
	protected void loopBody(Message m) {
		// logger.trace("TestActor:%s loopBody %s: %s", getName(), m, this);
		DefaultActorTest.sleeper(1);
		String subject = m.getSubject();

        // 如果消息为 “repeat”，那么 actor 基于 count 参数开始发送另外 N-1 条消息。
        // 这些消息通过调用 actor 管理器的 send 方法发送给一个随机 actor
		if ("repeat".equals(subject)) {
			int count = (Integer) m.getData();
			logger.trace("TestActor:%s repeat(%d) %s: %s", getName(), count, m, this);
			if (count > 0) {
				m = new DefaultMessage("repeat", count - 1);
				// logger.trace("TestActor loopBody send %s: %s", m, this);
				String toName = "actor" + DefaultActorTest.nextInt(DefaultActorTest.TEST_ACTOR_COUNT);
				Actor to = actorTest.getTestActors().get(toName);
				if (to != null) {
					getManager().send(m, this, to);
				} else {
					logger.warning("repeat:%s to is null: %s", getName(), toName);
				}
			}
		} else if ("init".equals(subject)) {
            // 如果消息为 “init”，那么 actor 通过向随机选择的 actor 或一个属于 common 类别的 actor 发送两组消息，启动 repeat 消息队列。
			int count = (Integer) m.getData();
			count = DefaultActorTest.nextInt(count) + 1;
			logger.trace("TestActor:%s init(%d): %s", getName(), count, this);
			for (int i = 0; i < count; i++) {
				DefaultActorTest.sleeper(1);
				m = new DefaultMessage("repeat", count);
				// logger.trace("TestActor runBody send %s: %s", m, this);
				String toName = "actor" + DefaultActorTest.nextInt(DefaultActorTest.TEST_ACTOR_COUNT);
				Actor to = actorTest.getTestActors().get(toName);
				if (to != null) {
					getManager().send(m, this, to);
				} else {
					logger.warning("init:%s to is null: %s", getName(), toName);
				}
				DefaultMessage dm = new DefaultMessage("repeat", count);
				dm.setDelayUntil(new Date().getTime() + (DefaultActorTest.nextInt(5) + 1) * 1000);
				getManager().send(dm, this, this.getClass().getSimpleName());
			}
		} else {
			logger.warning("TestActor:%s loopBody unknown subject: %s", getName(), subject);
		}
	}

    public static void main(String[] args) {
        // 在 common 类别中创建了 2 个 actor，在 default 类别中创建了 5 个 actor，然后启动它们
        DefaultActorManager am = DefaultActorManager.getDefaultInstance();

        Map<String, Actor> testActors = new HashMap<String, Actor>();
        for (int i = 0; i < 2; i++) {
            Actor a = am.createActor(TestActor.class, "common" + i);
            a.setCategory("common");
            testActors.put(a.getName(), a);
        }
        for (int i = 0; i < 5; i++) {
            Actor a = am.createActor(TestActor.class, "actor" + i);
            testActors.put(a.getName(), a);
        }
        for (String key : testActors.keySet()) {
            am.startActor(testActors.get(key));
        }
        for (int i = 120; i > 0; i--) {
            if (i < 10 || i % 10 == 0) {
                System.out.printf("main waiting: %d...%n", i);
            }
            DefaultActorTest.sleeper(1);
        }
        am.terminateAndWait();
    }
}