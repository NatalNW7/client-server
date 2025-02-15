package com.clientserver.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.clientserver.app.common.MessageUtils;

public class Server implements Runnable{
    ServerSocket serverSocket;
    volatile boolean keepProcessing = true;

    public Server(int port, int milisecondsTimeout) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(milisecondsTimeout);
    }

    public void run() {
        System.out.println("Server starting");

        while (keepProcessing) {
            try {
                System.out.println("acceptin client");
                Socket socket = serverSocket.accept();
                System.out.println("got client");
                process(socket);
            } catch (Exception e){
                handle(e);
            }
        }
    }

    private void process(Socket socket) {
        if (socket == null){
            return;
        }

        try {
            System.out.println("Server: getting message");
            String message = MessageUtils.getMessage(socket);
            System.out.printf("Server: got message: %s\n", message);
            Thread.sleep(1000);
            System.out.printf("Server: sending reply: %s\n", message);
            MessageUtils.sendMessage(socket, "Processed: " + message);
            System.out.println("Server: sent");
            closeIgnoringException(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    private void handle(Exception e) {
        if(!(e instanceof SocketException)){
            e.printStackTrace();
        }
    }

    public void stopProcessing() {
        keepProcessing = false;
        closeIgnoringException(serverSocket);
    }

    private void closeIgnoringException(ServerSocket serverSocket){
        if(serverSocket != null){
            try {
                serverSocket.close();
            } catch (Exception e) {
                
            }
        }
    }

    private void closeIgnoringException(Socket socket){
        if(socket != null){
            try {
                socket.close();
            } catch (Exception e) {
                
            }
        }
    }
}
