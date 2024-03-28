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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import static webserver.HttpMessage.constants.WebServerConst.*;
import static webserver.HttpMessage.constants.eums.FileType.*;
import static webserver.HttpMessage.constants.eums.ResponseStatus.*;
import static webserver.WebServer.staticSourcePath;

public class ArticleHandler implements Handler , Authorizer{
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
    public Response postArticle(Request request) throws IOException {
        String writer = userDB.findUserById(sessionDB.getSession(getSid(request)).get()).get().getName();

        int newArticleIndex = createArticle(request.getBody() , writer);

        startLine = new ResponseStartLine(HTTP_VERSION , FOUND);
        responseHeader = MessageHeader.builder()
                .field(LOCATION , "/main/article?index=" + newArticleIndex).build();

        return new Response(startLine).header(responseHeader).body(responseBody);
    }

    @GetMapping(path = "/article")
    public Response getWritePage(Request request){
        if(sessionDB.getSession(getSid(request)).isEmpty()) return redirectToLogin();

        return resourceHandler.responseGet(request);
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

    private int createArticle(MessageBody messageBody , String writer) throws IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedImg = decoder.decode(messageBody.getMultiContent(PNG));
        int currentIndex = articleDB.getSize()+1;

        String content = new String(decoder.decode(messageBody.getMultiContent(TXT)));
        String filePath = writeToFilePNG(decodedImg , currentIndex+".png");

        articleDB.addArticle(new Article(content , filePath , writer, currentIndex));

        System.out.println("Article added : " + content);

        return currentIndex;
    }

    private String writeToFilePNG(byte[] content , String fileName) throws IOException {
        String filePath = "/img/post/"+fileName;
        OutputStream outputStream = new FileOutputStream(staticSourcePath + filePath);
        outputStream.write(content);
        outputStream.close();

        return filePath;
    }
}
