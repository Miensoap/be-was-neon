package application.handler;

import application.db.interfaces.SessionDB;
import application.db.interfaces.UserDB;
import application.handler.utils.HtmlMaker;
import application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpHandler.Handler;
import webserver.HttpHandler.ResourceHandler;
import webserver.HttpMessage.*;
import webserver.HttpHandler.Mapping.GetMapping;
import webserver.HttpHandler.Mapping.PostMapping;
import webserver.HttpMessage.constants.eums.FileType;

import java.util.ArrayList;
import java.util.List;

import static webserver.HttpMessage.constants.WebServerConst.HTTP_VERSION;
import static webserver.HttpMessage.constants.WebServerConst.LOCATION;
import static webserver.HttpMessage.constants.eums.ResponseStatus.FOUND;
import static webserver.HttpMessage.constants.eums.ResponseStatus.OK;

public class UserHandler implements Handler , Authorizer{

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);

    // Response
    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;

    // DB
    private final UserDB userDB;
    private final SessionDB sessionDB;

    public UserHandler(UserDB userDB, SessionDB sessionDB){
        this.userDB = userDB;
        this.sessionDB = sessionDB;
    }

    /**
     * 회원 가입 요청에 응답
     * form 에 담긴 정보를 유저 DB에 저장하고 로그인 페이지로 리다이렉트
     * 이미 존재하는 유저 ID라면 가입에 실패하고 회원가입
     * @param request
     * @return
     */
    @PostMapping(path = "/registration")
    public Response createUser(Request request) {
        MessageBody reqBody = request.getBody();

        try {
            User user = new User(
                    reqBody.getContentByKey("userId"),
                    reqBody.getContentByKey("password"),
                    reqBody.getContentByKey("name"),
                    reqBody.getContentByKey("email")
            );

            userDB.addUser(user);
            log.info("User Created : " + user.getUserId());
             return redirectToLogin();
        } catch (IllegalArgumentException fail) {
            log.info("Fail to create new user : " + fail.getMessage());
            redirectTo("/registration");
        }

        startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        responseHeader = MessageHeader.builder().field(LOCATION , "/login").build();
        return new Response(startLine).header(responseHeader);
    }

    /**
     * 유저 리스트 조회 요청에 응답
     * 로그인 된 상태라면 회원가입 된 유저들의 닉네임을 리스트로 보여줌
     * 로그인 하지 않은 상태라면 로그인 페이지로 리다이렉트
     * @param request
     * @return
     */
    @GetMapping(path = "/user/list")
    public Response userList(Request request) {
        if (sessionDB.getSession(getSid(request)).isEmpty()) {
            return redirectToLogin();
        }

        startLine = new ResponseStartLine(HTTP_VERSION, OK);

        List<User> users = new ArrayList<>(userDB.findAll());
        responseBody = new MessageBody(HtmlMaker.getListHtml(users), FileType.HTML);
        responseHeader = writeContentResponseHeader(responseBody);

        return new Response(startLine).header(responseHeader).body(responseBody);
    }
}
