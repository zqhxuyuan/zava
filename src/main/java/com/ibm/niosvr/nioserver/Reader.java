package com.ibm.niosvr.nioserver;

import java.util.List;
import java.util.LinkedList;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * <p>Title: ���߳�</p>
 * <p>Description: ���߳����ڶ�ȡ�ͻ������</p>
 * @author starboy
 * @version 1.0
 */

public class Reader extends Thread {
    private static List pool = new LinkedList();
    private static Notifier notifier = Notifier.getNotifier();

    public Reader() {
    }

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

                // ��ȡ���
                read(key);
            }
            catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * ��ȡ�ͻ��˷����������
     * @param sc �׽�ͨ��
     */
    private static int BUFFER_SIZE = 1024;
    public static byte[] readRequest(SocketChannel sc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int off = 0;
        int r = 0;
        byte[] data = new byte[BUFFER_SIZE * 10];

        while ( true ) {
            buffer.clear();
            r = sc.read(buffer);
            if (r == -1) break;
            if ( (off + r) > data.length) {
                data = grow(data, BUFFER_SIZE * 10);
            }
            byte[] buf = buffer.array();
            System.arraycopy(buf, 0, data, off, r);
            off += r;
        }
        byte[] req = new byte[off];
        System.arraycopy(data, 0, req, 0, off);
        return req;
    }

    /**
     * ����������ݶ�ȡ
     * @param key SelectionKey
     */
    public void read(SelectionKey key) {
        try {
            // ��ȡ�ͻ������
            SocketChannel sc = (SocketChannel) key.channel();
            byte[] clientData =  readRequest(sc);

            Request request = (Request)key.attachment();
            request.setDataInput(clientData);

            // ����onRead
            notifier.fireOnRead(request);

            // �ύ�����߳̽���д����
            Server.processWriteRequest(key);
        }
        catch (Exception e) {
            notifier.fireOnError("Error occured in Reader: " + e.getMessage());
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

    /**
     * ��������
     * @param src byte[] Դ�������
     * @param size int ���ݵ�������
     * @return byte[] ���ݺ������
     */
    public static byte[] grow(byte[] src, int size) {
        byte[] tmp = new byte[src.length + size];
        System.arraycopy(src, 0, tmp, 0, src.length);
        return tmp;
    }
}
