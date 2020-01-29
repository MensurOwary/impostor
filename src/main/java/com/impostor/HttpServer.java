package com.impostor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private ServerSocket serverSocket;
    private boolean stopped = false;

    public HttpServer() throws IOException {
        this.serverSocket = new ServerSocket(8181);
    }

    public void start() throws IOException {
        while (!stopped) {
            Socket acceptedConnection = serverSocket.accept();
            new ClientThread(acceptedConnection).start();
        }
    }

    public void stop() throws IOException {
        stopped = true;
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        new HttpServer().start();
    }

}
