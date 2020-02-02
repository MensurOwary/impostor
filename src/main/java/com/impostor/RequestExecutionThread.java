package com.impostor;

import com.impostor.exception.EndpointNotFoundException;
import com.impostor.http.HttpMethod;
import com.impostor.http.RequestLine;
import com.impostor.model.DataType;
import com.impostor.model.config.Endpoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.impostor.utils.UtilityKt.isValidRequestLine;

public class RequestExecutionThread extends Thread {
    private final Socket socket;
    private final Map<String, String> requestHeaders;

    private final AtomicBoolean stopped;

    public RequestExecutionThread(Socket socket) {
        this.socket = socket;
        this.requestHeaders = new HashMap<>();
        this.stopped = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        try (BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out    = new PrintWriter(socket.getOutputStream()))
        {
            RequestLine requestLine = getRequestLine(in);
            try {
                if (requestLine != null) {
                    System.out.printf("Received:\t\t%s\t\t%s\n", requestLine.getMethod(), requestLine.getUri());
                    final var maybeEndpoint = ConfigProcessor.getEndpoints().getEndpoint(requestLine.getUri());
                    if (maybeEndpoint.isPresent()) {
                        final var endpoint = maybeEndpoint.get();
                        final Map<String, String> pathValues = extractPathParamValues(requestLine, endpoint);
                        String payload = endpoint.getPayload();

                        for (Map.Entry<String, String> entry : pathValues.entrySet()) {
                            payload = payload.replaceAll(Pattern.quote("${"+entry.getKey()+"}"), entry.getValue());
                        }

                        final HttpMethod method = endpoint.getMethod();
                        if (requestLine.getMethod() == method) {
                            out.println("HTTP/1.1 200 OK");
                            out.println("Content-Type: " + endpoint.getContentType());
                            out.println();
                            out.println(payload);
                        } else {
                            out.println("HTTP/1.1 405 Method Not Allowed");
                            out.println("Content-Type: text/html");
                            out.println();
                            out.println("<h1>Method Not Allowed</h1>");
                        }
                    } else {
                        respond404(out);
                    }
                } else {
                    respond404(out);
                }
                out.flush(); // submitting everything first
                stopExecution(); // then closing the socket
            } catch (EndpointNotFoundException nfx) {
                respond404(out);
            }

        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    @NotNull
    private Map<String, String> extractPathParamValues(RequestLine requestLine, Endpoint endpoint) {
        final List<String> pathParams = endpoint.getPath()
                .getRegexUrl()
                .matcher(requestLine.getUri())
                .results()
                .flatMap(result -> {
                    List<String> tempParams = new ArrayList<>();
                    for (int i = 1; i <= result.groupCount(); i++) {
                        final String group = result.group(i);
                        tempParams.add(group);
                    }
                    return tempParams.stream();
                }).collect(Collectors.toList());

        final Map<String, String> pathValues = new HashMap<>();
        int i = 0;
        for (Map.Entry<String, DataType> entry : endpoint.getPath().getPathParams().entrySet()) {
            final String value = pathParams.get(i++);
            final String key = entry.getKey();
            pathValues.put(key, value);
        }
        return pathValues;
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
        if (!stopped.get()) {
            socket.close();
            stopped.set(true);
        }
    }

    private void respond404(PrintWriter out) {
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<h1>Not Found</h1>");
    }
}
