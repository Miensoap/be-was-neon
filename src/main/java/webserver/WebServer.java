package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpHandler.Handler;
import webserver.HttpHandler.Mapping.MappingMatcher;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public static final String staticSourcePath = "./src/main/resources/static";
    private final MappingMatcher mappingMatcher;

    public WebServer(List<Handler> appHandlers) {
        this.mappingMatcher = new MappingMatcher(appHandlers);
    }

    /**
     * 웹 서버를 시작한다
     *
     * @param args 사용할 포트 번호 , 지정하지 않으면 8080 사용
     * @throws Exception
     */
    public void startServer(String[] args) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
//            while ((connection = listenSocket.accept()) != null) {
//                CompletableFuture<Void> thread = CompletableFuture.runAsync(new SocketMessageHandler(connection, mappingMatcher));
//                thread.get();
//            }

            while ((connection = listenSocket.accept()) != null) {
                SocketMessageHandler socketMessageHandler = new SocketMessageHandler(connection, mappingMatcher);
                executorService.execute(socketMessageHandler);
            }
        } finally {
            executorService.shutdown();
        }
    }
}

