package com.interview.flag.g;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created_By: stefanie
 * Date: 15-1-3
 * Time: 下午11:01
 */
public class G19_InternetGomokuGame {
    class Move{
        int x;
        int y;
        int playerID;
    }
    class Board{
        int rows;
        int cols;
        int[][] board;
        int successCount;
        public boolean update(Move move){
            board[move.x][move.y] = move.playerID;
            return isSuccess(move.x, move.y, move.playerID);
        }

        private boolean isSuccess(int x, int y, int flag){
            return continuousRows(x, y, flag) || continuousCols(x, y, flag)
                    || continuousDiagonally(x, y, flag);
        }

        private boolean continuousRows(int x, int y, int flag){
            int count = 1;
            for(int i = x + 1; i < rows && board[i][y] == flag; i++) count++;
            for(int i = x - 1; i >= 0 && board[i][y] == flag; i--) count++;
            return count >= successCount;
        }

        private boolean continuousCols(int x, int y, int flag){
            int count = 1;
            for(int j = y + 1; j < cols && board[x][j] == flag; j++) count++;
            for(int j = y - 1; j >= 0 && board[x][j] == flag; j--) count++;
            return count >= successCount;
        }
        private boolean continuousDiagonally(int x, int y, int flag){
            int count = 1;
            for(int i = 1; x+i < rows && y+i < cols && board[x+i][y+i] == flag; i++) count++;
            for(int i = 1; x-i >= 0 && y-i >= 0 && board[x-i][y-i] == flag; i++) count++;
            if(count >= successCount) return true;
            count = 1;
            for(int i = 1; x+i < rows && y-i >= 0 && board[x+i][y-i] == flag; i++) count++;
            for(int i = 1; x-i >= 0 && y+i < cols && board[x-i][y+i] == flag; i++) count++;
            if(count >= successCount) return true;
            return false;
        }
    }
    class Message{
        boolean start;
        int sender;
        boolean isFinished;
        int winner;

        Move lastMove;
        public Message(){

        }
        public Message(String input){
            //de-serialize from input
        }

        public String toString(){
            //serialize to output
            return "";
        }

    }
    class Display{
        public void showFailMessage(){

        }
        public void showSuccessMessage(){

        }
        public void paint(Board board){

        }
    }
    class ClientConnection{
        String serverIP;
        int serverPort;
        Player player;

        Socket socket;
        PrintWriter out;
        BufferedReader in;
        public ClientConnection(String serverIP, int serverPort, Player player){
            this.serverIP = serverIP;
            this.serverPort = serverPort;
            this.player = player;
        }
        public void connect(){
            try {
                this.socket = new Socket(serverIP, serverPort);
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String input;
                while ((input = in.readLine()) != null) {
                    Message message = new Message(input);
                    player.receiveMessage(message);
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(Message message){
            out.println(message.toString());
        }
    }
    class Player{
        ClientConnection conn;
        int playerID;
        Board board;
        Display display;

        public void joinGame(String serverIP, int serverPort){
            conn = new ClientConnection(serverIP, serverPort, this);
            Message message = new Message();
            message.sender = playerID;
            conn.sendMessage(message);
        }

        public void receiveMessage(Message message){
            if(message.isFinished){
                if(message.winner == playerID) display.showSuccessMessage();
                else display.showFailMessage();
            } else {
                if(!message.start){
                    board.update(message.lastMove);
                }
                display.paint(board);
                Move move = getUserMove();
                message.lastMove = move;
                sendMessage(message);
            }
        }

        private Move getUserMove() {    //blocking wait for user's input
            return null;
        }

        public void sendMessage(Message message){
            message.sender = playerID;
            conn.sendMessage(message);
        }
    }

    class ServerConnection{
        private int portNumber;
        private Server server;

        private ServerSocket serverSocket;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ServerConnection(int portNumber, Server server){
            this.portNumber = portNumber;
            this.server = server;
            start();
        }

        public void start(){
            try{
                serverSocket = new ServerSocket(portNumber);
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String input;
                while ((input = in.readLine()) != null) {
                    Message message = new Message(input);
                    server.receiveMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void sendMessage(Message message){
            out.println(message.toString());
        }
    }
    class Server{
        int serverIdentify;
        int currentPlayer;
        Board board;
        int[] players;
        ServerConnection[] conns;

        public Server(){
            conns = initConnections();
        }

        private ServerConnection[] initConnections() {
            ServerConnection[] conns = new ServerConnection[2];
            //init conns based on config files
            return conns;
        }

        private void joinGame(String playerID){
            //add playerID in players;
            if(players[0] != 0 || players[1] != 0) startPlay();
        }

        public void startPlay(){
            this.currentPlayer = 0;
            Message message = new Message();
            message.start = true;
            sendMessage(message, currentPlayer);
        }

        public void receiveMessage(Message message){
            if(message.sender != players[currentPlayer]) return;
            boolean isSuccess = board.update(message.lastMove);
            if(isSuccess){
                message.isFinished = true;
                message.winner = players[currentPlayer];
                for(int i = 0; i < players.length; i++){
                    sendMessage(message, players[i]);
                }
            } else {
                currentPlayer = players[(currentPlayer + 1)% players.length];
                sendMessage(message, currentPlayer);
            }
        }

        public void sendMessage(Message message, int player){
            message.sender = serverIdentify;
            //send message to player
            conns[player].sendMessage(message);
        }
    }
}
