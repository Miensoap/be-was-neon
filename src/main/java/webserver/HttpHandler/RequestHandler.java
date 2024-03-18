package webserver.HttpHandler;

import db.Database;
import db.SessionStore;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpMessage.*;
import webserver.Mapping.GetMapping;
import webserver.Mapping.PostMapping;
import webserver.eums.FileType;
import webserver.eums.ResponseStatus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static webserver.WebServer.staticSourcePath;
import static webserver.eums.ResponseStatus.*;

public class RequestHandler {
    ResponseStartLine startLine;
    MessageHeader header = new MessageHeader(new HashMap<>());
    MessageBody body;

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @PostMapping(path = "/create")
    public Response createUser(Request request) {
        MessageBody reqBody = request.getBody();
        User user = new User(
                reqBody.getContentByKey("userId"),
                reqBody.getContentByKey("password"),
                reqBody.getContentByKey("name"),
                reqBody.getContentByKey("email")
        );

        try {
            Database.addUser(user);
            log.info("User Created : " + user.getUserId());
        } catch (IllegalArgumentException alreadyExists) {
            log.info("Fail to create new user : " + alreadyExists.getMessage());
        }

        startLine = new ResponseStartLine("HTTP/1.1", FOUND);
        writeResponseHeader(FOUND, FileType.NONE, 0);

        return new Response(startLine).header(header);
    }

    @PostMapping(path = "/login")
    public Response login(Request request) {
        MessageBody reqBody = request.getBody();
        User user = Database.findUserById(reqBody.getContentByKey("userId"));

        try {
            if (user.getPassword().equals(reqBody.getContentByKey("password"))) {
                String cookie = user.getUserId();
                SessionStore.addSession(cookie, user, 60000);
                log.info("login : " + user.getName());

                header.addHeaderField("Location", "/main");
                header.addHeaderField("Set-Cookie", "sid=123456; Path=/");
            } else {
                log.info("login failed : password mismatch");
                header.addHeaderField("Location", "/");
            }
        } catch (NullPointerException notExistUser) {
            log.info("login failed : notExistUser");
            header.addHeaderField("Location", "/");
        }

        startLine = new ResponseStartLine("HTTP/1.1", FOUND);
        return new Response(startLine).header(header);
    }

    @PostMapping(path = "/logout")
    public Response logout(Request request) {
        MessageBody body = request.getBody();
        SessionStore.removeSession(body.getContentByKey("cookie"));
        log.info("logout" );

        startLine = new ResponseStartLine("HTTP/1.1", FOUND);
        header.addHeaderField("Location", "/");
        return new Response(startLine).header(header);
    }

    @GetMapping(path = "/")
    public Response responseGet(Request request) {
        String path = request.getStartLine().getUri();
        log.debug("path : " + path);

        File file = new File(getFilePath(path));
        log.debug("filepath : " + getFilePath(path));
        try {
            body = new MessageBody(file);
            startLine = new ResponseStartLine("HTTP/1.1", OK);
            writeResponseHeader(OK, body.getContentType(), body.getContentLength());
        } catch (IOException noSuchFile) { // 해당 경로의 파일이 없을 때 getFileBytes 에서 예외 발생 , 로그 출력 후 던짐
            // 404 페이지 응답
            body = new MessageBody(NotFound.getMessage(), FileType.TXT);
            startLine = new ResponseStartLine("HTTP/1.1", NotFound);
            writeResponseHeader(NotFound, body.getContentType(), body.getContentLength());
        }

        return new Response(startLine).header(header).body(body);
    }

    private String getFilePath(String path) {
        String[] splitPath = path.split("/");
        if (splitPath.length == 0 || !splitPath[splitPath.length - 1].contains(".")) {
            // 파일이 아니라 경로라면 그 경로의 index.html 을 요청한 것으로 간주
            return staticSourcePath + path + "/index.html";
        }
        return staticSourcePath + path;
    }

    private void writeResponseHeader(ResponseStatus status, FileType contentType, long contentLength) {
        if (status == FOUND) header.addHeaderField("Location", "/index.html");

        else {
            header.addHeaderField("Content-Type", contentType.getMimeType());
            header.addHeaderField("Content-Length", contentLength + "");
        }

    }
}
