package application.handler;

import application.db.Database;
import application.db.SessionStore;
import application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpHandler.Handler;
import webserver.HttpHandler.Mapping.GetMapping;
import webserver.HttpHandler.Mapping.PostMapping;
import webserver.HttpHandler.ResourceHandler;
import webserver.HttpMessage.*;
import webserver.HttpMessage.constants.eums.FileType;

import static webserver.HttpMessage.constants.WebServerConst.*;
import static webserver.HttpMessage.constants.eums.ResponseStatus.FOUND;

public class LoginHandler implements Handler {

    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);

    @PostMapping(path = "/login")
    public Response login(Request request) {
        MessageBody requestBody = request.getBody();
        User user = Database.findUserById(requestBody.getContentByKey(USER_ID));

        responseHeader = MessageHeader.builder().field(LOCATION, "/").build();

        try {
            if (user.isCorrectPassword(requestBody.getContentByKey(USER_PW))) {
                String cookie = responseHeader.addCookie(10, "sid");
                SessionStore.addSession(cookie, user);
                log.info("login : " + user.getName());
            } else {
                log.info("login failed : password mismatch");
            }
        } catch (NullPointerException notExistUser) {
            log.info("login failed : notExistUser");
        }

        startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        return new Response(startLine).header(responseHeader);
    }

    @PostMapping(path = "/logout")
    public Response logout(Request request) {
        String cookie = request.getHeaderValue(COOKIE).split("sid=")[1];
        SessionStore.removeSession(cookie);
        log.info("logout");

        startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        responseHeader = MessageHeader.builder().field(LOCATION, "/").build();
        return new Response(startLine).header(responseHeader);
    }

    @GetMapping(path = "/")
    public Response loginUser(Request request) {
        ResourceHandler resourceHandler = new ResourceHandler();
        String path = request.getStartLine().getUri();

        if (verifySession(request)) {
            User user = getCookie(request);
            Response mainIndex = resourceHandler.responseGet(new Request("GET /main " + HTTP_VERSION));

            String loginUserIndexPage = new String(mainIndex.getBody()).replace("<!--UserName-->", user.getName());
            responseBody = new MessageBody(loginUserIndexPage, FileType.HTML);
            writeContentResponseHeader(responseBody);
            log.info("welcome Logged-in user : " + user.getName());

            return mainIndex.header(responseHeader).body(responseBody);
        }

        return resourceHandler.responseGet(request);
    }
}
