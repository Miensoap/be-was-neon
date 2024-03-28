package application.handler;

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

public class UserHandler implements Handler {

    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);
    private final UserDB userDB;

    public UserHandler(UserDB userDB){
        this.userDB = userDB;
    }

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
        } catch (IllegalArgumentException fail) {
            log.info("Fail to create new user : " + fail.getMessage());
        }

        startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        responseHeader = MessageHeader.builder().field(LOCATION , "/").build();

        return new Response(startLine).header(responseHeader);
    }

    @GetMapping(path = "/user/list")
    public Response userList(Request request) {
        if (!verifySession(request)) {
            startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
            responseHeader = MessageHeader.builder().field(LOCATION , "/").build();

            return new Response(startLine).header(responseHeader);
        }

        startLine = new ResponseStartLine(HTTP_VERSION, OK);

        List<User> users = new ArrayList<>();
        users.addAll(userDB.findAll());
        responseBody = new MessageBody(HtmlMaker.getListHtml(users), FileType.HTML);
        responseHeader = writeContentResponseHeader(responseBody);

        return new Response(startLine).header(responseHeader).body(responseBody);
    }
}
