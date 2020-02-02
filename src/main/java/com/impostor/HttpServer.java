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
    private final int port;
    private BlockingQueue<RequestExecutionThread> threads;

    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.port = port;
        this.threads = new LinkedBlockingQueue<>();
    }

    public void start() throws IOException {
        System.out.println("Server is listening on port "+port);
        while (!stopped.get()) {
            Socket acceptedConnection = serverSocket.accept();
            final RequestExecutionThread requestExecutionThread = new RequestExecutionThread(acceptedConnection);
            this.threads.add(requestExecutionThread);
            requestExecutionThread.start();
        }
    }

    public void stop() throws IOException {
        stopped.set(true);

        for (RequestExecutionThread thread : threads) {
            thread.stopExecution();
        }

        serverSocket.close();
        System.out.println("Server socket closed...");
    }
}
