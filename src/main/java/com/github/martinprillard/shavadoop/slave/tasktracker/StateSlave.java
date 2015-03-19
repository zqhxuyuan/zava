package com.github.martinprillard.shavadoop.slave.tasktracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.github.martinprillard.shavadoop.slave.Slave;
import com.github.martinprillard.shavadoop.util.Constant;

/**
 * 
 * @author martin prillard
 * 
 */
public class StateSlave extends Thread {

    private int portTaskTracker;
    private String hostMaster;
    private boolean run = true;
    private Slave slave;

    public StateSlave(Slave _slave, String _hostMaster, int _portTaskTracker) {
        slave = _slave;
        portTaskTracker = _portTaskTracker;
        hostMaster = _hostMaster;
    }

    private void stopStateSlave() {
        run = false;
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(hostMaster, portTaskTracker);
            while (run) {
                waitRequestMaster(socket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Wait the request state'slave from the task tracker
     * 
     * @param socket
     */
    public void waitRequestMaster(Socket socket) {

        // get the return message from the server
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // get the request from the task tracker
            String request = in.readLine();

            if (request.equalsIgnoreCase(Constant.MESSAGE_TASKTRACKER_REQUEST)) {
                // if the task is already finished
                if (slave.isTaskFinished()) {
                    sendTaskFinished(socket);
                } else {
                    // send the slave state
                    sendState(socket, slave.isState());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send if the worker is working or not
     * 
     * @param state
     */
    public void sendState(Socket socket, boolean state) {

        // request slave state
        PrintWriter out = null;

        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            // send state
            String stateString = null;
            if (state) {
                stateString = Constant.ANSWER_TASKTRACKER_REQUEST_OK;
                out.println(stateString);
                out.flush();
            } else {
                stateString = slave.getMsgError();
                out.println(stateString);
                out.flush();
                stopStateSlave();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send if the worker's task is finished
     * 
     * @param socket
     */
    public void sendTaskFinished(Socket socket) {
        // request slave state
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.println(Constant.ANSWER_TASKTRACKER_REQUEST_TASK_FINISHED);
            out.flush();
            stopStateSlave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
