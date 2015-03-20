/*
 * Copyright 2008 Niels Peter Strandberg.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.Allogy.simplemq;

import com.github.Allogy.simplemq.config.MessageQueueConfig;
import com.github.Allogy.simplemq.config.PersistentMessageQueueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.ref.WeakReference;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MessageQueueImp implements MessageQueue, Serializable {
	private static final long serialVersionUID = 4688999278205140460L;
    private static Logger log = LoggerFactory.getLogger(MessageQueue.class);

    //???: wouldn't a timer be better here?
    private transient final Timer timer;
    //Use Memory support like BlockingQueue or Here use memory database.
    //内存数据库和内存不同的是: 数据库将对象表示成表的形式, 而内存直接存放对象. --> like ORM mapping need db as middle man
    private transient Connection conn;

    private final MessageQueueConfig queueConfig;
    private final String queueName;

    //should we use volatile for multi thread?
    private boolean shutdown;
    private boolean deleted;
    private transient final Lock lock = new ReentrantLock();
    //TODO: maybe make this configurable per-queue?
    public static boolean INSTALL_SHUTDOWN_HOOK = true;

    /**
     * The shutdown thread for the queue. We have to unregistrer it
     * when we shutdown the database, or else the GC cannot remove it.
     */
    private transient Thread shutdownHook;

    /**
     * Constructs a message queue instance that is not controled by
     * the {@link MessageQueueService}. Will normaly only be used
     * by a remoting framework. For at better JVM local usage, use the
     * MessageQueueService.
     *
     * @see MessageQueueService
     */
    public MessageQueueImp() {
        this("noname", new MessageQueueConfig());
    }


    /**
     * Constructs a message queue instance. Normaly only use by the {@link MessageQueueService}.
     * For message queues that is JVM local, use the {@link MessageQueueService}.
     *
     * @param queueName - should be unique in the MessageQueueService
     * @see MessageQueueService
     */
    public MessageQueueImp(String queueName) {
        this(queueName, new MessageQueueConfig());
    }


    /**
     * Constructs a message queue instance. Normaly only use by the {@link MessageQueueService}.
     * For message queues that is JVM local, use the {@link MessageQueueService}.
     *
     * @param queueName   - should be unique in the MessageQueueService
     * @param queueConfig - configur the message queue
     * @see MessageQueueService
     */
    public MessageQueueImp(String queueName, MessageQueueConfig queueConfig) {
        this.queueName = queueName;
        this.queueConfig = (MessageQueueConfig) Utils.copy(queueConfig);
        this.timer = new Timer(queueName+"-message-queue-timer", true);
        boolean hsqldbapplog  = queueConfig.getHsqldbapplog();
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            //WTF: 内存和磁盘都采用hsqldb: 不同的是jdbc:hsqldb:file和jdbc:hsqldb:mem
            if (queueConfig instanceof PersistentMessageQueueConfig) {
                PersistentMessageQueueConfig pqc = (PersistentMessageQueueConfig) queueConfig;
                String cacheDirectory = (pqc.getDatabaseDirectory() == null) ? "" : pqc.getDatabaseDirectory();

                conn = DriverManager.getConnection("jdbc:hsqldb:file:" + cacheDirectory
                        + "queues/" + queueName + "/" + queueName
                        + (hsqldbapplog ? ";hsqldb.applog=1" : ""), "sa", "");
            } else {
                conn = DriverManager.getConnection("jdbc:hsqldb:mem:" + queueName
                        + (hsqldbapplog ? ";hsqldb.applog=1" : ""), "sa", "");
            }
            //初始化数据库
            createOrMigrateDatabaseStructure();
        } catch (Exception e) {
            throw new RuntimeException("unable to initialize "+queueName+" message queue", e);
        }

        setWriteDelay();
        startQueueMaintainers();

        //In some cases, this same queue is needed for another shutdown hook, or is otherwise unwanted.
        if (INSTALL_SHUTDOWN_HOOK) {
            // 'shutdownhook' is called when the JVM shuts down.
            // Used here to make sure we shutdown properly.
            shutdownHook = new RelayVMShutdown(this);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
    }

    public String getQueueName()
    {
        return queueName;
    }

    //关闭线程
    private static class RelayVMShutdown extends Thread {
        //弱引用
        final WeakReference<MessageQueueImp> messageQueue;

        RelayVMShutdown(MessageQueueImp messageQueue) {
            this.messageQueue = new WeakReference<MessageQueueImp>(messageQueue);
        }

        public void run() {
            log.info("VM is shutting down, relaying to HSQL DB");
            //在内部类了, 要获得外部类, 使用对象的get方法. 而不能用this.
            MessageQueueImp mq = messageQueue.get();
            if (mq == null) {
                log.info("already gone away");
            } else {
                //Set shutdown to null so that shutdown will not try and unregister it (esp. since we can't, at this point)
                mq.shutdownHook=null;
                mq.shutdown();
            }
        };
    }

    private void mustNotBeShutdown() {
        if (this.shutdown) {
            throw new IllegalStateException("message queue is shutdown");
        }
    }

    //发送一批消息
    public void send(Collection<Message> messageInputs) {
        mustNotBeShutdown();
        RuntimeException exception=null;
        for (Message messageInput : messageInputs) {
            try {
                send(messageInput);
            } catch (RuntimeException e) {
                if (exception==null) {
                    exception = e;
                } else {
                    log.error("error sending multiple messages", e);
                }
            }
        }
        if (exception!=null) {
            throw exception;
        }
    }

    //发送一条消息
    public void send(Message messageInput) {
        mustNotBeShutdown();
        if (messageInput == null) {
            throw new NullPointerException("The messageInput cannot be 'null'");
        }

        //序列化消息
        byte[] b = null;
        if (messageInput.getObject() != null) {
            b = Utils.serialize(messageInput.getObject());
        }

        long startDelay=messageInput.getStartDelay();
        if (startDelay<0) startDelay=0;

        long time=System.currentTimeMillis()+startDelay;

        //重复的key, 表示队列中已经存在这个消息了.
        String dupeKey=messageInput.getDuplicateSuppressionKey();
        //有冲突了,怎么解决?
        OnCollision onCollision=messageInput.getDuplicateSuppressionAction();

        //有重复的消息, 这个是旧的消息
        MessageWrapper existing=null;

        lock.lock();

        try {
            if (dupeKey!=null) {
                //找出已经存在的旧的消息. peek按照队列是取队列头, 在这里因为用内存数据库,直接select!
                existing = peek(dupeKey);

                if (existing!=null) switch (onCollision) {
                    //降级: 如何实现将消息移动到队列尾部:
                    //更新时间为当前时间,查询时按照时间升序排列,在队列尾部的消息排序后在查询结果的最后
                    case DEMOTE : //new message is dropped, but update existing with new time
                        touchMessage(existing, time);
                        //fall through...

                    //丢弃新消息, 不处理
                    case DROP   : //no-op, new message is dropped.
                        log.debug("onCollision({}) drops new message on {}", onCollision, dupeKey);
                        return; //no new message

                    //排除新旧消息, 新消息不处理, 删除旧的消息
                    case EXCLUDE:
                        log.debug("onCollision(EXCLUDE) drops both messages: {}", dupeKey);
                        delete(existing);
                        return; //no new message

                    case REPLACE:
                        //will delete existing later
                        break;

                    //交换: 更新记录的时间
                    case SWAP:
                        time=existing.getTime();
                        //will delete existing later
                        break;
                }
            }

            try {
                //发送一条消息,往数据库中添加一条记录. 标记read=false, 并且time字段表示消息的到达时间
                PreparedStatement ps = conn.prepareStatement("INSERT INTO message (body, time, read, object, dupeKey, onCollision) VALUES(?, ?, ?, ?, ?, ?)");
                ps.setString(1, messageInput.getBody());
                ps.setLong(2, time);
                ps.setBoolean(3, false);
                if (b == null) {
                    ps.setNull(4, Types.BLOB);
                } else {
                    ps.setBinaryStream(4, new ByteArrayInputStream(b), b.length);
                }
                ps.setString(5, messageInput.getDuplicateSuppressionKey());
                if (onCollision==null) {
                    ps.setNull(6, Types.VARCHAR);
                } else {
                    ps.setString(6, onCollision.toString());
                }
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException("unable to save message", e);
            }

            //删除旧消息的冲突状态包括了: EXCLUDE(前面已经处理过了), SWAP, REPLACE
            if (existing!=null && (onCollision==OnCollision.SWAP || onCollision==OnCollision.REPLACE)) {
                log.debug("onCollision({}) deletes existing message: {}", onCollision, dupeKey);
                delete(existing);
            }
        } finally {
            lock.unlock();
        }
    }

    //touch: touch is non free operation. u touched, so will update the message's time
    //Like touch a file in linux, if file not exist, will create a file
    //if file already exist, it will update the file recently update time
    //When u touch a girl. It's also non-free. u touch her, u get back the girl's response.well,the resp depends.
    private void touchMessage(MessageWrapper existing, long time) {
        try {
            //更新消息的时间, 可以表示为消息的到达时间,用于模拟队列怎么按照时间取出数据处理
            PreparedStatement updateInventory = conn.prepareStatement("UPDATE message SET time=? WHERE id=?");
            updateInventory.setLong(1, time);
            updateInventory.setLong(2, existing.getId());
            updateInventory.executeUpdate();
            updateInventory.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Message receiveAndDelete() {
        mustNotBeShutdown();
        List<Message> messages = this.receiveInternal(1, true);
        if (messages.size() > 0) {
            return messages.get(0);
        } else {
            return null;
        }
    }

    public List<Message> receiveAndDelete(int limit) {
        mustNotBeShutdown();
        return receiveInternal(limit, true);
    }

    public Message receive() {
        mustNotBeShutdown();
        List<Message> messages = this.receiveInternal(1, false);
        if (messages.size() > 0) {
            return messages.get(0);
        } else {
            return null;
        }
    }

    public List<Message> receive(int limit) {
        mustNotBeShutdown();
        return receiveInternal(limit, false);
    }

    public Message peek() {
        mustNotBeShutdown();
        List<Message> messages = this.peekInternal(1);
        if (messages.size() > 0) {
            return messages.get(0);
        } else {
            return null;
        }
    }

    public List<Message> peek(int limit) {
        mustNotBeShutdown();
        return peekInternal(limit);
    }

    private List<Message> peekInternal(int limit) {
        if (limit < 1) limit = 1;
        List<Message> messages = new ArrayList<Message>(limit);
        try {
            //选择还没读过的消息,说明消息还没处理
            // 'ORDER BY time' depends on that the host computer times is always right.
            // 'ORDER BY id' what happens with the 'id' when we hit Long.MAX_VALUE?
            PreparedStatement ps = conn.prepareStatement("SELECT LIMIT 0 " + limit
                    +MessageWrapper.RS_FIELDS+ " FROM message WHERE read=false ORDER BY time");

            // The lock is making sure, that a SELECT and DELETE/UPDATE is only
            // done by one thread at a time. No update means no lock is needed
            //lock.lock();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MessageWrapper mw=MessageWrapper.fromResultSet(rs);

                if (!queueTimeIsInTheFuture(mw)) {
                    messages.add(mw);
                }
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //lock.unlock();
        }
        return messages;
    }

    private boolean queueTimeIsInTheFuture(MessageWrapper mw) {
        long now=System.currentTimeMillis();
        return (mw.getTime() > now);
    }

    /**
     * Returns the first/only message with the given duplicate-suppression key,
     * even if it's delayed-start time has not yet passed.
     *
     * @param dupeKey
     * @return
     */
    public MessageWrapper peek(String dupeKey) {
        mustNotBeShutdown();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT LIMIT 0 1"
                + MessageWrapper.RS_FIELDS+ " FROM message WHERE dupeKey = ?");
            ps.setString(1, dupeKey);
            ResultSet rs = ps.executeQuery();
            MessageWrapper mw=null;
            if (rs.next()) {
                mw=MessageWrapper.fromResultSet(rs);
            }
            rs.close();
            ps.close();
            return mw;
        } catch (SQLException e) {
            throw new RuntimeException("could not locate message by duplicate suppression key: "+e, e);
        }
    }

    private List<Message> receiveInternal(int limit, boolean delete) {
        if (limit < 1) limit = 1;
        List<Message> messages = new ArrayList<Message>(limit);
        try {
            // 'ORDER BY time' depends on that the host computer times is always right, but it lets us use delayed messages, etc...
            // 'ORDER BY id' what happens with the 'id' when we hit Long.MAX_VALUE? ( the heat-death of the universe? )
            PreparedStatement ps = conn.prepareStatement("SELECT LIMIT 0 " + limit
                    + MessageWrapper.RS_FIELDS + "FROM message WHERE read=false ORDER BY time");

            // The lock is making sure, that a SELECT and DELETE/UPDATE is only
            // done by one thread at a time.
            lock.lock();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MessageWrapper mw=MessageWrapper.fromResultSet(rs);
                if (queueTimeIsInTheFuture(mw)) continue;
                long id = mw.getId();

                //处理完消息,是否要从队列中删除消息
                if (delete) {
                    PreparedStatement updateInventory = conn.prepareStatement("DELETE FROM message WHERE id=?");
                    updateInventory.setLong(1, id);
                    updateInventory.executeUpdate();
                } else {
                    //如果没有删除,则要标记read=true,表示已经处理过该消息了
                    PreparedStatement updateInventory = conn.prepareStatement("UPDATE message SET read=? WHERE id=?");
                    updateInventory.setBoolean(1, true);
                    updateInventory.setLong(2, id);
                    updateInventory.executeUpdate();
                }
                messages.add(mw);
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return messages;
    }

    public boolean delete(List<Message> messages) {
        mustNotBeShutdown();
        try {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            for (Message message : messages) {
                if (!(message instanceof MessageWrapper)) {
                    throw new IllegalArgumentException("This instance of 'Message' is not valid.");
                }
                stmt.addBatch("DELETE FROM message WHERE id=" + message.getId());
            }

            int[] updateCounts = stmt.executeBatch();

            // Have we deleted them all
            if (updateCounts.length == messages.size()) {
                return true;
            } else {
                log.error("Not all Messages was deleted! Only {} out of {} were deleted!", updateCounts.length, messages.size());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                log.error("unable to restore autocommit", e);
            }
        }
        return false;
    }

    public boolean delete(Message message) {
        mustNotBeShutdown();
        // The Message instances this queue returns is and instance of 'MessageWrapper', so we only accept them.
        if (!(message instanceof MessageWrapper)) {
            throw new IllegalArgumentException("This instance of 'Message' is not valid.");
        }
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM message WHERE id=?");
            ps.setLong(1, message.getId());
            int i = ps.executeUpdate();
            ps.close();
            return (i > 0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteQueue() {
        if (deleted) return;
        deleted = true;
        shutdown();

        // if we have a persistent queue, delete the files.
        if (queueConfig instanceof PersistentMessageQueueConfig) {
            PersistentMessageQueueConfig pqc = (PersistentMessageQueueConfig) queueConfig;
            String cacheDirectory = (pqc.getDatabaseDirectory() == null) ? "" : pqc.getDatabaseDirectory();

            File queues=new File(cacheDirectory + "queues");
            Utils.deleteDirectory(new File(queues, queueName));

            String[] remaining=queues.list();

            if (remaining==null || remaining.length==0) {
                queues.delete();
            }
        }
    }

    public MessageQueueConfig getMessageQueueConfig() {
        return queueConfig;
    }

    public boolean deleted() {
        return deleted;
    }

    public long messageCount() {
        return unreadMessageCount();
    }

    public int unreadMessageCount() {
        mustNotBeShutdown();
        int count = -1;
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(id) FROM message WHERE read=?");
            ps.setBoolean(1, false);

            ResultSet rs = ps.executeQuery();
            rs.next();
            count = rs.getInt(1);
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public int totalMessageCount() {
        mustNotBeShutdown();
        int count = -1;
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(id) FROM message");
            ResultSet rs = ps.executeQuery();
            rs.next();
            count = rs.getInt(1);
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }


    public boolean isPersistent() {
        return (queueConfig instanceof PersistentMessageQueueConfig);
    }

    private void createOrMigrateDatabaseStructure() throws SQLException {
        Statement st = conn.createStatement();
        int currentVersion=readDatabaseVersion(st);
        String cached = "";

        //NB: once set, the CACHED will be indelible, until the queue is deleted. An original design/flaw.
        if (queueConfig instanceof PersistentMessageQueueConfig) {
            PersistentMessageQueueConfig pqc = (PersistentMessageQueueConfig) queueConfig;
            if (pqc.isCached()) {
                cached = "CACHED ";
            }
        }

        final int LATEST=1400;
        log.debug("db on disk is version {}, latest is {}", currentVersion, LATEST);
        switch (currentVersion) {
            case 0:
                st.execute("CREATE " + cached + "TABLE message (id BIGINT IDENTITY PRIMARY KEY, object LONGVARBINARY, body VARCHAR, time BIGINT, read BOOLEAN)");
                st.execute("CREATE INDEX id_index ON message(id)");
                st.execute("CREATE INDEX time_index ON message(time)");
                st.execute("CREATE INDEX read_index ON message(read)");
            case 1330:
                st.execute("CREATE TABLE meta (version INTEGER);");
            case 1331:
                st.execute("INSERT INTO meta (version) VALUES (1340);");
            case 1340:
                st.execute("ALTER TABLE message ADD COLUMN dupeKey VARCHAR;");
                st.execute("ALTER TABLE message ADD COLUMN onCollision VARCHAR;");
                st.execute("CREATE INDEX dupe_index ON message(dupeKey)");

                /*
                 ---------------------------------------------------------------------------------------
                 new migrations go above this line, with *current* value of LATEST, and should fall-through.
                 Remember to then update LATEST to resemble the current version number (plus a digit for
                 some wiggle-room between versions).
                 ---------------------------------------------------------------------------------------
                 */
                st.execute("UPDATE meta SET version = "+LATEST+";");
                log.info("simple-mq database updated to version {}", LATEST);
                break;
            case LATEST:
                log.debug("simple-mq database is up-to-date");
                break;
            default:
                log.warn("simple-mq database (on-disk) is not a supported version: {}, this is version {}", currentVersion, LATEST);
                //TODO: maybe have an option to die here, I favor continuing...
        }
        st.close();
    }

    private int readDatabaseVersion(Statement st) throws SQLException {
        if (tableExists("meta")) {
            log.debug("meta table exists");
            /*
            read the integer
            --
            For reasons that are not quite clear, the "LIMIT 1" syntax (which *is* a bit redundant)
            fails to parse:

            java.sql.SQLException: Unexpected token: 1 in statement [1]
                at org.hsqldb.jdbc.Util.sqlException(Unknown Source)
	            at org.hsqldb.jdbc.jdbcStatement.fetchResult(Unknown Source)
	            at org.hsqldb.jdbc.jdbcStatement.executeQuery(Unknown Source)
	            at MessageQueueImp.readDatabaseVersion(MessageQueueImp.java:648)
	            at MessageQueueImp.createOrMigrateDatabaseStructure(MessageQueueImp.java:581)
	            at MessageQueueImp.<init>(MessageQueueImp.java:111)
	            ... 109 more

             */
            //ResultSet resultSet = st.executeQuery("SELECT version FROM meta LIMIT 1");
            ResultSet resultSet = st.executeQuery("SELECT version FROM meta");
            try {
                if (resultSet.next()) {
                    int retval=resultSet.getInt(1);
                    log.debug("on-disk simple-mq database is at version {}", retval);
                    return retval;
                } else {
                    return 1331; //aka... need the meta row.
                }
            } finally {
                resultSet.close();
            }
        }
        else if (tableExists("message")) {
            log.debug("meta table does not exist, but message table does");
            //pre-meta table
            return 1330;
        }
        else {
            log.debug("database appears to be empty");
            //empty database
            return 0;
        }
    }

    // To check if a 'tableName' table allready exsists in the db.
    private boolean tableExists(String tableName) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet results = null;
        try {
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName);
            results = stmt.executeQuery();
            return true;  // if table does exist, no rows will ever be returned
        }
        catch (SQLException e) {
            return false;  // if table does not exist, an exception will be thrown
        } finally {
            if (results != null) {
                results.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void setWriteDelay() {
        if (queueConfig instanceof PersistentMessageQueueConfig) {
            PersistentMessageQueueConfig pqc = (PersistentMessageQueueConfig) queueConfig;
            try {
                Statement st = conn.createStatement();
                st.execute("SET WRITE_DELAY " + pqc.getDatabaseWriteDelay() + " MILLIS");
                st.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // A 'SHUTDOWN' command is send to the db, so that
    // it can flush changes and cleanup before the JVM halts.
    public void shutdown() {
        if (shutdown) {
            log.debug("{} message queue already shutdown", queueName);
            return;
        }
        shutdown=true;
        timer.cancel();
        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
            shutdownHook = null;
        }
        try {
            Statement st = conn.createStatement();
            st.execute("SHUTDOWN");
            st.close();
            conn.close();
        } catch (SQLException e) {
            log.error("could not shutdown hsqldb", e);
        }
        try {
            conn.isClosed();
        } catch (SQLException e) {
            log.warn("can not close connection", e);
        }
    }

    @Override
    public void decommissionQueue(MessageVisitor messageVisitor) {
        if (messageVisitor==null) throw new NullPointerException();
        this.shutdown=true;
        timer.cancel();
        List<Message> messages = new ArrayList<Message>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT " + MessageWrapper.RS_FIELDS + " FROM message ORDER BY time");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MessageWrapper mw=MessageWrapper.fromResultSet(rs);
                messages.add(mw);
            }

            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            messageVisitor.visit(messages);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        deleteQueue();
    }

    // 'QueueMaintainers' are 2 background threads. 队列维护有2个后台线程. 一个用于删除太旧的消息. 一个用于恢复已经读过,还没删除的消息
    // One deletes 'too old' messages, and the other 'revives' messages that has been read, but not deleted.
    private void startQueueMaintainers() {
        long oldDelay=TimeUnit.SECONDS.toMillis(queueConfig.getDeleteOldMessagesThreadDelay());
        timer.schedule(new DeleteOldTimerTask(this), oldDelay, oldDelay);

        long threadDelay=TimeUnit.SECONDS.toMillis(queueConfig.getReviveNonDeletedMessagsThreadDelay());
        timer.schedule(new ReviveTimerTask(this), threadDelay, threadDelay);
    }

    private void deleteOldMessages() {
        log.debug("delete old messages");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM message WHERE time<?");

            ps.setLong(1, System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(queueConfig.getMessageRemoveTime()));

            ResultSet rs = ps.executeQuery();
            List<Long> ids = new ArrayList<Long>();

            while (rs.next()) {
                ids.add(rs.getLong(1));
            }

            ps.close();
            ps = conn.prepareStatement("DELETE FROM message WHERE id=?");

            for (Long id : ids) {
                ps.setLong(1, id);
                ps.executeUpdate();
                log.info("message #{} has expired", id);
            }

            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void reviveStaleMessage() {
        log.debug("reviving stale messages");

        //!!!: This operation is technically unsafe, couldn't it be optimized to a single update command? would that make an ever-growing transaction log file?
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM message WHERE time<? AND read=true");
            ps.setLong(1, System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(queueConfig.getMessageReviveTime()));
            ResultSet rs = ps.executeQuery();
            List<Long> ids = new ArrayList<Long>();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
            ps.close();

            if (ids.isEmpty()) {
                log.debug("no {} messages to revive", queueName);
            } else {
                log.info("{} {} messages have been revived", ids.size(), queueName);
                ps = conn.prepareStatement("UPDATE message SET read=? WHERE id=?");
                for (Long id : ids) {
                    ps.setBoolean(1, false);
                    ps.setLong(2, id);
                    ps.executeUpdate();
                }
                ps.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // A Message returned by this queue, is an instance of MessageWrapper.
    private static class MessageWrapper implements Message, Serializable {
		//ivate static final long serialVersionUID = 7465209569623629016L;
        private static final long serialVersionUID = 7465209569623629017L;

		private String body;
        private long id;
        private long time;
        private Serializable object;
        private String duplicateSuppressionKey;
        private OnCollision duplicateSuppressionAction;

        private static final String RS_FIELDS=" id, time, body, dupeKey, onCollision, object ";

        private MessageWrapper() {};

        static MessageWrapper fromResultSet(ResultSet rs) throws SQLException {
            MessageWrapper mw = new MessageWrapper();
            mw.id = rs.getLong(1);
            mw.time = rs.getLong(2);
            mw.body = rs.getString(3);
            mw.duplicateSuppressionKey=rs.getString(4);

            String collision=rs.getString(5);
            if (collision!=null) {
                mw.duplicateSuppressionAction=OnCollision.valueOf(collision);
            }
            InputStream is = rs.getBinaryStream(6);
            if (is != null) {
                try {
                    mw.object = Utils.deserialize(is);
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        //okay.
                    }
                }
            }
            return mw;
        }

        public String getBody()
        {
            return body;
        }

        public Serializable getObject()
        {
            return object;
        }

        public long getId()
        {
            return id;
        }

        public String getDuplicateSuppressionKey()
        {
            return duplicateSuppressionKey;
        }

        public OnCollision getDuplicateSuppressionAction()
        {
            return duplicateSuppressionAction;
        }

        @Override public
        long getStartDelay()
        {
            return 0;
        }

        public long getTime()
        {
            return time;
        }
    }

    protected void finalize() throws Throwable {
        if (!shutdown) {
            log.error("{} message queue was dereferenced without being shutdown", queueName);
            shutdown();
        }
        super.finalize();
    }

    private static class DeleteOldTimerTask extends TimerTask {
        private final WeakReference<MessageQueueImp> messageQueue;

        public DeleteOldTimerTask(MessageQueueImp messageQueueImp) {
            messageQueue = new WeakReference<MessageQueueImp>(messageQueueImp);
        }

        @Override public void run() {
            MessageQueueImp messageQueueImp=messageQueue.get();

            if (messageQueueImp==null) {
                log.warn("message queue has gone away");
            } else {
                messageQueueImp.deleteOldMessages();
            }
        }
    }

    private static class ReviveTimerTask extends TimerTask {
        private final WeakReference<MessageQueueImp> messageQueue;

        public
        ReviveTimerTask(MessageQueueImp messageQueueImp) {
            messageQueue = new WeakReference<MessageQueueImp>(messageQueueImp);
        }

        @Override public void run() {
            MessageQueueImp messageQueueImp=messageQueue.get();

            if (messageQueueImp==null) {
                log.warn("message queue has gone away");
            } else {
                messageQueueImp.reviveStaleMessage();
            }
        }
    }

}
