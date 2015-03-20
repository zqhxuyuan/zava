package com.github.Allogy.simplemq.test;

import com.github.Allogy.simplemq.*;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;


public class TestInMemoryQueue {

    private MessageQueue queue;
    private static final String TEST_DATABASE = "test-database";

    @Before
    public void setUp() {
        queue = MessageQueueService.getMessageQueue(TEST_DATABASE);
        assertFalse(queue.deleted());
    }

    @Test
    public void testQueueService() {
        Collection<String> queues = MessageQueueService.getMessageQueueNames();
        assertTrue(queues.contains(TEST_DATABASE));
    }

    @Test
    public void testMessageQueue() {
        assertNotNull(queue);
        assertTrue(queue instanceof Serializable);
    }

    @Test
    public void testAddAndRecieve() {

        queue.send(new MessageInput("hello"));

        MessageInput mi = new MessageInput();
        mi.setObject("there");
        queue.send(mi);

        assertEquals(2, queue.unreadMessageCount());

        // test that I get the same queue instance back
        queue = MessageQueueService.getMessageQueue(TEST_DATABASE);
        assertEquals(2, queue.unreadMessageCount());

        {
            Message msg = queue.receiveAndDelete();
            assertTrue(msg instanceof Serializable);
            assertEquals(msg.getBody(), "hello");
            assertEquals(1, queue.unreadMessageCount());
        }
        {
            Message msg = queue.receive();
            assertEquals(msg.getObject(), "there");
            queue.delete(msg);
            assertEquals(0, queue.unreadMessageCount());
        }
        {
            Message msg = queue.receive();
            assertNull(msg);
        }

        {
            Message msg = queue.receiveAndDelete();
            assertNull(msg);
        }

    }


    @Test(expected = NullPointerException.class)
    public void sendNullMessageInput() throws NullPointerException {
        queue.send((MessageInput) null);
    }


    @Test
    public void testSendListOfMessages() {

        List<Message> list = new ArrayList<Message>();
        list.add(new MessageInput("hello"));
        list.add(new MessageInput("hello2"));

        queue.send(list);

        assertEquals(2, queue.unreadMessageCount());

        List<Message> messages = queue.receiveAndDelete(2);

        assertEquals(0, queue.unreadMessageCount());

    }

    @Test
    public void testDeleteListOfMessages() {

        List<Message> list = new ArrayList<Message>();
        list.add(new MessageInput("hello"));
        list.add(new MessageInput("hello2"));

        queue.send(list);

        assertEquals(2, queue.unreadMessageCount());

        List<Message> messages = queue.receive(2);

        assertTrue(queue.delete(messages));

        assertNull(queue.peek());
        assertNull(queue.receive());

        assertEquals(queue.peek(3).size(), 0);
    }

    private void dupePush(String a, String b, String dupeKey, OnCollision onCollision) {
        dump();

        aFewRandomMessages();
        queue.send(new MessageInput(a).setDuplicateSuppressionKey(dupeKey, randomCollisionPolicy()));
        aFewRandomMessages();
        queue.send(new MessageInput(b).setDuplicateSuppressionKey(dupeKey, onCollision));
        aFewRandomMessages();
    }

    private void dump() {
        while (queue.receiveAndDelete() != null) ;
    }

    private void aFewRandomMessages() {
        int n = random.nextInt(4);
        for (int i = 0; i < n; i++) {
            queue.send(randomMessage());
        }
    }

    private MessageInput randomMessage() {
        return new MessageInput("not-relevant");
    }

    private final Random random = new Random();

    private OnCollision randomCollisionPolicy() {
        switch (random.nextInt(5)) {
            case 0:
                return OnCollision.DEMOTE;
            case 1:
                return OnCollision.DROP;
            case 2:
                return OnCollision.REPLACE;
            case 3:
                return OnCollision.SWAP;
            case 4:
                return OnCollision.EXCLUDE;
        }

        return null;
    }

    @Test
    public void testDupeDROP() /* new message dies, old messages maintains it's place in the queue */ {
        dupePush("alpha", "beta", "drop", OnCollision.DROP);
        Message message = queue.peek("drop");
        assertNotNull(message);
        assertEquals("alpha", message.getBody());
    }

    @Test
    public void testDupeDEMOTE()  /* new message dies, but old message is moved to the end of the queue */ {
        dupePush("gamma", "delta", "demote", OnCollision.DEMOTE);
        Message message = queue.peek("demote");
        assertNotNull(message);
        assertEquals("gamma", message.getBody());
    }

    @Test
    public void testDupeREPLACE() /* old message dies, new message is placed at the end of the queue */ {
        dupePush("alpha", "beta", "replace", OnCollision.REPLACE);
        Message message = queue.peek("replace");
        assertNotNull(message);
        assertEquals("beta", message.getBody());
    }

    @Test
    public void testDupeSWAP() /* old message dies, but new message takes it's place in the queue (i.e. the queue time) */ {
        dupePush("gamma", "delta", "swap", OnCollision.SWAP);
        Message message = queue.peek("swap");
        assertNotNull(message);
        assertEquals("delta", message.getBody());
    }

    @Test
    public void testDupeEXCLUDE() /* both messages die */ {
        dupePush("alpha", "beta", "exclude", OnCollision.EXCLUDE);
        Message message = queue.peek("exclude");
        assertNull(message);
    }

    @Test
    public void testDelayedStart() throws InterruptedException {
        dump();

        queue.send(new MessageInput("alpha").setStartDelay(200));

        assertEquals(1, queue.unreadMessageCount());
        assertEquals(1, queue.totalMessageCount());

        assertNull(queue.peek());
        assertTrue(queue.peek(5).isEmpty());
        assertNull(queue.receive());
        assertTrue(queue.receive(5).isEmpty());

        assertEquals(1, queue.unreadMessageCount());
        assertEquals(1, queue.totalMessageCount());

        Thread.sleep(400);

        assertNotNull(queue.peek());
        assertFalse(queue.peek(5).isEmpty());
        assertNotNull(queue.receive());

        assertEquals(0, queue.unreadMessageCount());
        assertEquals(1, queue.totalMessageCount());
    }

    @Test
    public void testDecommissionEmpty() {
        dump();

        queue.decommissionQueue(new MessageVisitor() {
            @Override
            public void visit(List<Message> messages) throws Exception {
                Assert.assertEquals(0, messages.size());
            }
        });

        MessageQueueService.deleteMessageQueue(TEST_DATABASE);
        setUp();
    }


    @Test
    public void testDecommissionActive() {
        dump();
        queue.send(new MessageInput("alpha"));

        queue.decommissionQueue(new MessageVisitor() {
            @Override
            public void visit(List<Message> messages) throws Exception {
                Assert.assertEquals(1, messages.size());
            }
        });

        MessageQueueService.deleteMessageQueue(TEST_DATABASE);
        setUp();
    }

    @Test
    public void testDecommissionRead() {
        dump();
        queue.send(new MessageInput("alpha"));
        queue.receive();

        queue.decommissionQueue(new MessageVisitor() {
            @Override
            public void visit(List<Message> messages) throws Exception {
                Assert.assertEquals(1, messages.size());
            }
        });

        MessageQueueService.deleteMessageQueue(TEST_DATABASE);
        setUp();
    }

    @Test
    public void testDecommissionDelayed() {
        dump();
        queue.send(new MessageInput("alpha").setStartDelay(200));

        queue.decommissionQueue(new MessageVisitor() {
            @Override
            public void visit(List<Message> messages) throws Exception {
                Assert.assertEquals(1, messages.size());
            }
        });

        MessageQueueService.deleteMessageQueue(TEST_DATABASE);
        setUp();
    }


    @After
    public void tearDown() {
        queue.shutdown();

        assertFalse(queue.deleted());
        MessageQueueService.deleteMessageQueue(TEST_DATABASE);
        assertTrue(queue.deleted());

        Collection<String> queues = MessageQueueService.getMessageQueueNames();
        assertFalse(queues.contains(TEST_DATABASE));
    }

}
