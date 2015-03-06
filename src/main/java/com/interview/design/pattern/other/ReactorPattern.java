package com.interview.design.pattern.other;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午10:01
 *
 * 多线程模式在用户负载增加时，性能将下降非常的快。我们需要重新寻找一个新的方案，保持数据处理的流畅，
 * 很显然，事件触发机制是最好的解决办法，当有事件发生时，会触动handler,然后开始数据的处理。
 *
 * Reactor模式参与者
 * 1.Reactor 负责响应IO事件，一旦发生，广播发送给相应的Handler去处理,这类似于AWT的thread
 * 2.Handler 是负责非堵塞行为，类似于AWT ActionListeners；同时负责将handlers与event事件绑定，类似于AWT addActionListener
 *
 * Java的NIO为reactor模式提供了实现的基础机制，它的Selector当发现某个channel有数据时，
 * 会通过SlectorKey来告知我们，在此我们实现事件和handler的绑定。
 */
public class ReactorPattern {
    static class Reactor implements Runnable{
        Selector selector;
        ServerSocketChannel serverSocket;
        public Reactor(int port) throws IOException {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(),port);
            serverSocket.socket().bind(address);

            serverSocket.configureBlocking(false);
            //向selector注册该channel
            SelectionKey sk =serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("-->Start serverSocket.register!");

            //利用sk的attache功能绑定Acceptor 如果有事情，触发Acceptor
            sk.attach(new Acceptor());
            System.out.println("-->attach(new Acceptor()!");
        }


        public void run() { // normally in a new Thread
            try {
                while (!Thread.interrupted()) {
                    selector.select();
                    Set selected = selector.selectedKeys();
                    Iterator it = selected.iterator();
                    //Selector如果发现channel有OP_ACCEPT或READ事件发生，下列遍历就会进行。
                    while (it.hasNext()) {
                        //来一个事件 第一次触发一个accepter线程
                        //以后触发SocketReadHandler
                        dispatch((SelectionKey) (it.next()));
                        selected.clear();
                    }
                }
            } catch (IOException e) {
                System.out.println("reactor stop!" + e.getLocalizedMessage());
            }
        }

        //运行Acceptor或SocketReadHandler
        void dispatch(SelectionKey k) {
            Runnable r = (Runnable)(k.attachment());
            if (r != null){
                r.run();
            }
        }

        class Acceptor implements Runnable { // inner
            public void run() {
                try {
                    System.out.println("-->ready for accept!");
                    SocketChannel c = serverSocket.accept();
                    if (c != null)
                        //调用Handler来处理channel
                        new SocketReadHandler(selector, c);
                } catch(IOException ex) {
                    System.out.println("accept stop!"+ex);
                }
            }
        }
    }

    static class SocketReadHandler implements Runnable {
        SocketChannel socket;
        SelectionKey sk;

        static final int READING = 0, SENDING = 1;
        int state = READING;

        public SocketReadHandler(Selector sel, SocketChannel c) throws IOException {

            socket = c;

            socket.configureBlocking(false);
            sk = socket.register(sel, 0);

            //将SelectionKey绑定为本Handler 下一步有事件触发时，将调用本类的run方法。
            //参看dispatch(SelectionKey k)
            sk.attach(this);

            //同时将SelectionKey标记为可读，以便读取。
            sk.interestOps(SelectionKey.OP_READ);
            sel.wakeup();
         }

        public void run() {
            try{
                // test.read(socket,input);
                readRequest() ;
            } catch(Exception ex){
                System.out.println("readRequest error" + ex);
            }
        }


        /**
         * 处理读取data
         * @param key
         * @throws Exception
         */
        private void readRequest() throws Exception {

            ByteBuffer input = ByteBuffer.allocate(1024);
            input.clear();
            try{
                int bytesRead = socket.read(input);
                //激活线程池 处理这些request
                System.out.println(input.toString());
                //requestHandle(new Request(socket,btt));
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

    }
}
