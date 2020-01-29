package com.impostor;

import com.impostor.exception.EndpointNotFoundException;
import com.impostor.http.RequestLine;
import com.impostor.model.config.EndpointConfig;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.impostor.utils.UtilityKt.isValidRequestLine;

public class ClientThread extends Thread {
    private final Socket socket;
    private final Map<String, String> requestHeaders;

    public ClientThread(Socket socket) {
        this.socket = socket;
        this.requestHeaders = new HashMap<>();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream());) {
            RequestLine requestLine = getRequestLine(in);
            try {
                if (requestLine != null) {
                    final EndpointConfig endpoint = ConfigProcessor.ENDPOINTS_CONFIG.getEndpoint(requestLine.getUri());
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: " + endpoint.getContentType());
                    out.println();
                    out.println(endpoint.getPayload());
                } else {
                    respond404(out);
                }
            } catch (EndpointNotFoundException nfx) {
                respond404(out);
            }

        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    private void respond404(PrintWriter out) {
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<h1>Not Found</h1>");
    }

    @Nullable
    private RequestLine getRequestLine(BufferedReader in) throws IOException {
        boolean skipFirstLine = false;
        RequestLine requestLine = null;
        while (true) {
            String line = in.readLine();
            String CRLF = "\r\n";
            if (line == null || line.equals(CRLF) || line.isEmpty()) {
                break;
            }
            if (!skipFirstLine && isValidRequestLine(line)) {
                requestLine = RequestLine.getRequestLine(line);
                skipFirstLine = true;
            } else {
                String[] split = line.split(":\\s+");
                if (split.length == 2) {
                    final String headerName = split[0];
                    final String headerValue = split[1];
                    requestHeaders.put(headerName, headerValue);
                }
            }
        }
        return requestLine;
    }

    public void stopExecution() throws IOException {
        socket.close();
    }
}
