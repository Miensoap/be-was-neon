package webserver.HttpHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpMessage.*;
import webserver.HttpHandler.Mapping.GetMapping;
import webserver.HttpMessage.constants.eums.FileType;

import java.io.File;
import java.io.IOException;

import static webserver.WebServer.staticSourcePath;
import static webserver.HttpMessage.constants.WebServerConst.HTTP_VERSION;
import static webserver.HttpMessage.constants.eums.ResponseStatus.*;

public class ResourceHandler implements Handler {
    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);

    /**
     * 리소스 요청에 응답
     * 해당 경로에 파일이 없다면 404 페이지 응답
     * @param request
     * @return
     */
    public Response responseGet(Request request) {
        String path = request.getStartLine().getUri();

        log.debug("path : " + path);
        File file = new File(getFilePath(path));
        log.debug("filepath : " + getFilePath(path));
        try {
            responseBody = new MessageBody(file);
            startLine = new ResponseStartLine(HTTP_VERSION, OK);
        } catch (IOException noSuchFile) { // 해당 경로의 파일이 없을 때 getFileBytes 에서 예외 발생 , 로그 출력 후 던짐
            // 404 페이지 응답
            return new ErrorHandler().getErrorResponse(NotFound);
        }

        responseHeader = writeContentResponseHeader(responseBody);
        return new Response(startLine)
                .header(responseHeader)
                .body(responseBody);
    }

    private String getFilePath(String path) {
        String[] splitPath = path.split("/");
        if (splitPath.length == 0 || !splitPath[splitPath.length - 1].contains(".")) {
            // 파일이 아니라 경로라면 그 경로의 index.html 을 요청한 것으로 간주
            return staticSourcePath + path + "/index.html";
        }
        return staticSourcePath + path;
    }
}
