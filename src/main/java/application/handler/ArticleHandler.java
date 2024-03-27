package application.handler;

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
    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;


    private static final ResourceHandler resourceHandler = new ResourceHandler();
    private static final ErrorHandler errorHandler = new ErrorHandler();
    private final List<Article> articleList = new ArrayList<>();

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

        String writer = getCookie(request).getName();
        String content = new String(decoder.decode(requestBody.getMultiContent(TXT)));
        String encodedImg = requestBody.getMultiContent(PNG);

        articleList.add(new Article(content , encodedImg , writer));

        System.out.println("Article added : " + content);

        return articleList.size();
    }

    @GetMapping(path = "/main/article")
    public Response getArticle(Request request) {
        Request mainReq = new Request(GET + " /main " + HTTP_VERSION);
        int index = Integer.parseInt(request.getRequestQuery("index")) - 1;

        if (index>=articleList.size()){
            return errorHandler.getErrorResponse(NotFound);
        }

        startLine = new ResponseStartLine(HTTP_VERSION, OK);
        responseBody = new MessageBody(
                HtmlMaker.getArticlePage(articleList.get(index), new String(resourceHandler.responseGet(mainReq).getBody()), index
                )
                , HTML);

        responseHeader = writeContentResponseHeader(responseBody);
        return new Response(startLine).header(responseHeader).body(responseBody);
    }
}
