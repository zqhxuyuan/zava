package com.github.martinprillard.shavadoop.master.tasktracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.github.martinprillard.shavadoop.util.Constant;

/**
 * 
 * @author martin prillard
 * 
 */
public class CheckStateSlave extends Thread {

    private Socket socket;
    private StateSlaveManager stateSlaveManager;

    public CheckStateSlave(StateSlaveManager _stateSlaveManager, Socket _socket) {
        stateSlaveManager = _stateSlaveManager;
        socket = _socket;
    }

    public void run() {
        if (!stateSlaveManager.getTaskFinished()) {

            String hostClient = socket.getRemoteSocketAddress().toString();

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // send request
                sendRequestStateSlave(socket);

                // get the state slave
                String slaveAliveString = in.readLine();

                if (slaveAliveString != null && !slaveAliveString.equalsIgnoreCase(Constant.ANSWER_TASKTRACKER_REQUEST_OK)) {
                    if (Constant.MODE_DEBUG)
                        System.out.println("TASK_TRACKER receive slave's state : " + slaveAliveString + " (" + hostClient + ")");
                }

                if (slaveAliveString != null && slaveAliveString.equalsIgnoreCase(Constant.ANSWER_TASKTRACKER_REQUEST_TASK_FINISHED)) {
                    stateSlaveManager.caseWorkerTaskIsFinished();
                    // else get the slave's state
                } else if (slaveAliveString == null) {
                    stateSlaveManager.caseWorkerDied();
                }

            } catch (IOException e) {
                // slave too long to answer
                if (Constant.MODE_DEBUG)
                    System.out.println("TASK_TRACKER slave " + hostClient + " not respond...");
                stateSlaveManager.caseWorkerDied();
            }
        }

    }

    /**
     * Send request to the slave to know his state
     * 
     * @param socket
     */
    public void sendRequestStateSlave(Socket socket) {
        // request slave state
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            // send state
            out.println(Constant.MESSAGE_TASKTRACKER_REQUEST);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
