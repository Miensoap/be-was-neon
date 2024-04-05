package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpHandler.Mapping.MappingMatcher;
import webserver.HttpMessage.*;
import webserver.HttpMessage.utils.InputReadHelper;

import java.io.*;
import java.net.Socket;

public class SocketMessageHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SocketMessageHandler.class);
    private final Socket connection;
    private final MappingMatcher matcher;

    public SocketMessageHandler(Socket connectionSocket, MappingMatcher mappingMatcher) {
        this.connection = connectionSocket;
        this.matcher = mappingMatcher;
    }

    /**
     * HTTP 요청을 받아 요청에 맞는 HTTP 응답을 전송
     */
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);

            Request request = InputReadHelper.readRequestMessage(in);
            log.debug(request.toString());

            Response response = matcher.getResponse(request);

            dos.writeBytes(response.toString());

            byte[] responseBody;
            if (response.messageBody() != null) {
                responseBody = response.messageBody().getBody();
                dos.write(responseBody);
            }
            dos.flush();
            log.debug("Send : " + response.getStartLine().toString() + " for " + request.getStartLine().toString());

        } catch (Exception e) {
            log.error(e.getMessage());
//            e.printStackTrace();
        }
    }
}