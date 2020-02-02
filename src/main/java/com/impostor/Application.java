package com.impostor;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

public class Application {

    @SneakyThrows
    public static void main(String[] args) {
        final int port = 8181;
        final HttpServer httpServer = new HttpServer(port);
        httpServer.start();
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(httpServer));
    }

    @RequiredArgsConstructor
    static class ShutdownThread extends Thread {
        private final HttpServer httpServer;

        @Override
        public synchronized void start() {
            try {
                System.out.println("Trying to stop the server...");
                httpServer.stop();
            } catch (Exception ex) {
                System.exit(0);
            }
        }
    }

}
