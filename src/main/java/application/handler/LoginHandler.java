package application.handler;

import application.db.interfaces.SessionDB;
import application.db.interfaces.UserDB;
import application.model.Session;
import application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpHandler.Handler;
import webserver.HttpHandler.Mapping.GetMapping;
import webserver.HttpHandler.Mapping.PostMapping;
import webserver.HttpHandler.ResourceHandler;
import webserver.HttpMessage.*;
import webserver.HttpMessage.constants.eums.FileType;

import java.util.Optional;

import static webserver.HttpMessage.constants.WebServerConst.*;
import static webserver.HttpMessage.constants.eums.ResponseStatus.FOUND;

public class LoginHandler implements Handler, Authorizer {

    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);
    private final UserDB userDB;
    private final SessionDB sessionDB;

    public LoginHandler(UserDB userDB, SessionDB sessionDB) {
        this.userDB = userDB;
        this.sessionDB = sessionDB;
    }

    @PostMapping(path = "/login")
    public Response login(Request request) {
        MessageBody requestBody = request.getBody();

        // 존재하지 않는 유저 ID
        Optional<User> userOptional = userDB.findUserById(requestBody.getContentByKey(USER_ID));
        if (userOptional.isEmpty()) {
            return redirectToLogin();
        }

        responseHeader = MessageHeader.builder().field(LOCATION, "/").build();
        startLine = new ResponseStartLine(HTTP_VERSION, FOUND);

        // 비밀번호 확인
        User user = userOptional.get();
        if (user.isCorrectPassword(requestBody.getContentByKey(USER_PW))) {
            String cookie = setCookie();
            sessionDB.addSession(new Session(cookie, user.getUserId()));
            log.info("login : " + user.getName());
        } else {
            log.info("login failed : password mismatch");
            return redirectToLogin();
        }

        return new Response(startLine).header(responseHeader);
    }

    @PostMapping(path = "/logout")
    public Response logout(Request request) {
        String cookie = getSid(request);
        sessionDB.removeSession(cookie);
        log.info("logout");

        return redirectTo("/").addHeaderField("Set-Cookie", "sid=; max-age=1");
    }

    @GetMapping(path = "/")
    public Response loginUser(Request request) {
        ResourceHandler resourceHandler = new ResourceHandler();
        String path = request.getStartLine().getUri();

        if (sessionDB.getSession(getSid(request)).isPresent()) {
            String userName = sessionDB.getSession(getSid(request)).get();
            Response mainIndex = resourceHandler.responseGet(new Request("GET /main " + HTTP_VERSION));

            String loginUserIndexPage = new String(mainIndex.getBody()).replace("<!--UserName-->", userName);
            responseBody = new MessageBody(loginUserIndexPage, FileType.HTML);
            responseHeader = writeContentResponseHeader(responseBody);
            log.info("welcome Logged-in user : " + userName);

            return mainIndex.header(responseHeader).body(responseBody);
        }

        return resourceHandler.responseGet(request);
    }

    private String setCookie() {
        String cookie;
        while (true) {
            cookie = responseHeader.addCookie(10, "sid");
            if (sessionDB.getSession(cookie).isEmpty()) break;
        }
        return cookie;
    }
}
