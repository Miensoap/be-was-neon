package application.handler;

import application.db.interfaces.ArticleDB;
import application.db.interfaces.SessionDB;
import application.db.interfaces.UserDB;
import application.handler.utils.HtmlMaker;
import application.model.Article;
import application.model.User;
import webserver.HttpHandler.ErrorHandler;
import webserver.HttpHandler.Handler;
import webserver.HttpHandler.Mapping.GetMapping;
import webserver.HttpHandler.Mapping.PostMapping;
import webserver.HttpHandler.ResourceHandler;
import webserver.HttpMessage.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static webserver.HttpMessage.constants.WebServerConst.*;
import static webserver.HttpMessage.constants.eums.FileType.*;
import static webserver.HttpMessage.constants.eums.ResponseStatus.*;

public class ArticleHandler implements Handler {
    // Response
    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;

    // Handler
    private static final ResourceHandler resourceHandler = new ResourceHandler();
    private static final ErrorHandler errorHandler = new ErrorHandler();

    //DB
    private final UserDB userDB;
    private final SessionDB sessionDB;
    private final ArticleDB articleDB;

    public ArticleHandler(UserDB userDB, SessionDB sessionDB , ArticleDB articleDB){
        this.userDB = userDB;
        this.sessionDB = sessionDB;
        this.articleDB = articleDB;
    }


    @PostMapping(path = "/article")
    public Response postArticle(Request request) {
        int newArticleIndex = createArticle(request);

        startLine = new ResponseStartLine(HTTP_VERSION , FOUND);
        responseHeader = MessageHeader.builder()
                .field(LOCATION , "/main/article?index=" + newArticleIndex).build();

        return new Response(startLine).header(responseHeader).body(responseBody);
    }

    private int createArticle(Request request){
        Base64.Decoder decoder = Base64.getDecoder();
        MessageBody requestBody = request.getBody();

        String writer = userDB.findUserById(sessionDB.getSession(getSid(request))).getName();
        String content = new String(decoder.decode(requestBody.getMultiContent(TXT)));
        String encodedImg = requestBody.getMultiContent(PNG);

        articleDB.addArticle(new Article(content , encodedImg , writer));

        System.out.println("Article added : " + content);

        return articleDB.getSize();
    }

    @GetMapping(path = "/main/article")
    public Response getArticle(Request request) {
        Request mainReq = new Request(GET + " /main " + HTTP_VERSION);
        int index = Integer.parseInt(request.getRequestQuery("index"));

        // 처음 ,마지막 글에서 이동 버튼 클릭 처리
        if(index==0) index+=1;
        else if(index==articleDB.getSize()+1) index-=1;

        Article article = articleDB.getArticle(index);
        // 존재하지 않는 글에 접근 처리
        if (article==null){
            return errorHandler.getErrorResponse(NotFound);
        }

        // 정상 흐름 응답
        startLine = new ResponseStartLine(HTTP_VERSION, OK);
        responseBody = new MessageBody(
                HtmlMaker.getArticlePage(article, new String(resourceHandler.responseGet(mainReq).getBody()), index), HTML);

        responseHeader = writeContentResponseHeader(responseBody);
        return new Response(startLine).header(responseHeader).body(responseBody);
    }
}
