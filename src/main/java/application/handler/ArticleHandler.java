package application.handler;

import application.db.interfaces.ArticleDB;
import application.db.interfaces.SessionDB;
import application.db.interfaces.UserDB;
import application.handler.utils.HtmlMaker;
import application.model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpHandler.ErrorHandler;
import webserver.HttpHandler.Handler;
import webserver.HttpHandler.Mapping.GetMapping;
import webserver.HttpHandler.Mapping.PostMapping;
import webserver.HttpHandler.ResourceHandler;
import webserver.HttpMessage.*;
import webserver.HttpMessage.constants.eums.FileType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Optional;

import static webserver.HttpMessage.constants.WebServerConst.*;
import static webserver.HttpMessage.constants.eums.FileType.HTML;
import static webserver.HttpMessage.constants.eums.FileType.TXT;
import static webserver.HttpMessage.constants.eums.ResponseStatus.*;
import static webserver.WebServer.staticSourcePath;

public class ArticleHandler implements Handler, Authorizer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

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

    public ArticleHandler(UserDB userDB, SessionDB sessionDB, ArticleDB articleDB) {
        this.userDB = userDB;
        this.sessionDB = sessionDB;
        this.articleDB = articleDB;
    }

    @PostMapping(path = "/article")
    public Response postArticle(Request request) throws IOException {
        String writer = userDB.findUserById(sessionDB.getSession(getSid(request)).get()).get().getName(); // optional 사용해서 이상해짐
        int newArticleIndex = createArticle(request.getBody(), writer);

        return redirectTo("/main/article?index=" + newArticleIndex);
    }

    @GetMapping(path = "/article")
    public Response getWritePage(Request request) {
        if (sessionDB.getSession(getSid(request)).isEmpty()) return redirectToLogin();

        return resourceHandler.responseGet(request);
    }

    @GetMapping(path = "/main/article")
    public Response getArticle(Request request) {
        int index = Integer.parseInt(request.getRequestQuery("index"));

        // 처음 ,마지막 글에서 이동 버튼 클릭 처리
        int maxIndex = articleDB.getSize();
        if (index == 0) return redirectToArticle(1);
        else if (index ==  maxIndex+ 1) return redirectToArticle(maxIndex);

        Article article = articleDB.getArticle(index);
        // 존재하지 않는 글에 접근 처리
        if (article == null) {
            return errorHandler.getErrorResponse(NotFound);
        }

        // 정상 흐름 응답
        startLine = new ResponseStartLine(HTTP_VERSION, OK);
        Request mainReq = new Request(GET + " /main " + HTTP_VERSION); // Todo . 파일에 직접 접근하는 기능 인터페이스 추가
        responseBody = new MessageBody(
                HtmlMaker.getArticlePage(article, new String(resourceHandler.responseGet(mainReq).getBody()), index), HTML);

        responseHeader = writeContentResponseHeader(responseBody);
        return new Response(startLine).header(responseHeader).body(responseBody);
    }

    private int createArticle(MessageBody messageBody, String writer) throws IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        int currentIndex = articleDB.getSize() + 1;
        String filePath;
        String content;

        Optional<FileType> imageType = FileType.imageTypes().stream().filter(ft -> messageBody.getMultiContent(ft) != null).findAny();
        if (imageType.isPresent()) {
            byte[] decodedImg = decoder.decode(messageBody.getMultiContent(imageType.get()));
            filePath = writeToFile(decodedImg, "post" + currentIndex, "." + imageType.get().toString().toLowerCase());
        } else filePath = "/img/foo.png";

        if (messageBody.getMultiContent(TXT) != null) {
            content = new String(decoder.decode(messageBody.getMultiContent(TXT)));
        }else content = "No content";


        articleDB.addArticle(new Article(content, filePath, writer, currentIndex));
        log.info(writer + "'s Article added : " + content);
        return currentIndex;
    }

    private String writeToFile(byte[] content, String fileName, String extend) throws IOException {
        String filePath = "/img/post/" + fileName + extend;
        OutputStream outputStream = new FileOutputStream(staticSourcePath + filePath);
        outputStream.write(content);
        outputStream.close();

        log.info("file saved : " + filePath);
        return filePath;
    }

    private Response redirectToArticle(int index){
        return  redirectTo("/main/article?index=" + index);
    }
}
