package com.ibm.niosvr.nioserver;

import java.util.List;
import java.util.LinkedList;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

/**
 * <p>Title: ��Ӧ�߳�</p>
 * <p>Description: ������ͻ��˷�����Ϣ</p>
 * @author starboy
 * @version 1.0
 */

public final class Writer extends Thread {
    private static List pool = new LinkedList();
    private static Notifier notifier = Notifier.getNotifier();

    public Writer() {
    }

    /**
     * SMS�����߳����ط��񷽷�,����������������
     */
    public void run() {
        while (true) {
            try {
                SelectionKey key;
                synchronized (pool) {
                    while (pool.isEmpty()) {
                        pool.wait();
                    }
                    key = (SelectionKey) pool.remove(0);
                }

                // ����д�¼�
                write(key);
            }
            catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * ������ͻ��������
     * @param key SelectionKey
     */
    public void write(SelectionKey key) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            Response response = new Response(sc);

            // ����onWrite�¼�
            notifier.fireOnWrite((Request)key.attachment(), response);

            // �ر�
            sc.finishConnect();
            sc.socket().close();
            sc.close();

            // ����onClosed�¼�
            notifier.fireOnClosed((Request)key.attachment());
        }
        catch (Exception e) {
            notifier.fireOnError("Error occured in Writer: " + e.getMessage());
        }
    }

    /**
     * ����ͻ�����,�����û��������,�����Ѷ����е��߳̽��д���
     */
    public static void processRequest(SelectionKey key) {
        synchronized (pool) {
            pool.add(pool.size(), key);
            pool.notifyAll();
        }
    }
}
