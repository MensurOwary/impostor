package com.impostor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpServer {
    private final ServerSocket serverSocket;
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private BlockingQueue<ClientThread> threads;

    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.threads = new LinkedBlockingQueue<>();
    }

    public void start() throws IOException {
        while (!stopped.get()) {
            Socket acceptedConnection = serverSocket.accept();
            final ClientThread clientThread = new ClientThread(acceptedConnection);
            this.threads.add(clientThread);
            clientThread.start();
        }
    }

    public void stop() throws IOException {
        stopped.set(true);

        for (ClientThread thread : threads) {
            thread.stopExecution();
        }

        serverSocket.close();
        System.out.println("Server socket closed...");
    }
}
