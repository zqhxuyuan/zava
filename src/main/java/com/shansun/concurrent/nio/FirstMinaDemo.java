package com.shansun.concurrent.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * <p>
 * </p>
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-25
 */
public class FirstMinaDemo {

    private static final String	QUIT	= "quit";

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new Server().start();
        new Client().start();
    }

    static class Server {
        public void start() throws IOException {
            IoAcceptor acceptor = new NioSocketAcceptor();
            acceptor.getSessionConfig().setReadBufferSize(2048);
            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

            // 添加过滤器
            acceptor.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));

            // 设置处理器
            acceptor.setHandler(new IoHandler());

            acceptor.bind(new InetSocketAddress(9123));
        }

        static class IoHandler extends IoHandlerAdapter {

            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                String str = message.toString();
                System.err.println("Received message: " + message);
                if (str.equals(QUIT)) {
                    session.close(true);
                    return;
                }
            }

        }
    }

    static class Client {

        public void start() {
            IoConnector connector = new NioSocketConnector();
            connector.setConnectTimeoutMillis(30000);
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));

            connector.setHandler(new IoHandler("你好!\r\n大家好!"));

            connector.connect(new InetSocketAddress("localhost", 9123));
        }

        static class IoHandler extends IoHandlerAdapter {

            private String	values;

            public IoHandler(String values) {
                super();
                this.values = values;
            }

            @Override
            public void sessionOpened(IoSession session) throws Exception {
                session.write(values);
            }
        }
    }
}