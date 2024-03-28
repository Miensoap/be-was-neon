package application.handler;

import application.db.interfaces.SessionDB;
import application.db.interfaces.UserDB;
import application.db.memoryDB.MemUserDB;
import application.db.memoryDB.MemSessionDB;
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

import static webserver.HttpMessage.constants.WebServerConst.*;
import static webserver.HttpMessage.constants.eums.ResponseStatus.FOUND;

public class LoginHandler implements Handler {

    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);
    private final UserDB userDB;
    private final SessionDB sessionDB;

    public LoginHandler(UserDB userDB, SessionDB sessionDB){
        this.userDB = userDB;
        this.sessionDB = sessionDB;
    }

    @PostMapping(path = "/login")
    public Response login(Request request) {
        MessageBody requestBody = request.getBody();
        User user = userDB.findUserById(requestBody.getContentByKey(USER_ID));
        responseHeader = MessageHeader.builder().field(LOCATION, "/").build();

        try {
            if (user.isCorrectPassword(requestBody.getContentByKey(USER_PW))) {
                String cookie;
                while (true) {
                    cookie = responseHeader.addCookie(10, "sid");
                    if (sessionDB.getSession(cookie) == null) break;
                }

                sessionDB.addSession(new Session(cookie, user.getUserId()));
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
        String cookie = getSid(request);
        sessionDB.removeSession(cookie);
        log.info("logout");

        startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        responseHeader = MessageHeader.builder()
                .field(LOCATION, "/")
                .field("Set-Cookie" , "name=sid; max-age=1")
                .build();
        return new Response(startLine).header(responseHeader);
    }

    @GetMapping(path = "/")
    public Response loginUser(Request request) {
        ResourceHandler resourceHandler = new ResourceHandler();
        String path = request.getStartLine().getUri();

        if (verifySession(request)) {
            User user = userDB.findUserById(sessionDB.getSession(getSid(request)));
            // 로그아웃 하지 않고 서버를 종료해서 쿠키는 남아있는데 MemDB에 유저가 없는 경우 처리
            if(user == null) return resourceHandler.responseGet(request);

            Response mainIndex = resourceHandler.responseGet(new Request("GET /main " + HTTP_VERSION));

            String loginUserIndexPage = new String(mainIndex.getBody()).replace("<!--UserName-->", user.getName());
            responseBody = new MessageBody(loginUserIndexPage, FileType.HTML);
            responseHeader = writeContentResponseHeader(responseBody);
            log.info("welcome Logged-in user : " + user.getName());

            return mainIndex.header(responseHeader).body(responseBody);
        }

        return resourceHandler.responseGet(request);
    }
}
