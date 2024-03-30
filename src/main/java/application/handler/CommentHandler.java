package application.handler;

import application.db.interfaces.*;
import application.handler.utils.HtmlMaker;
import application.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpHandler.ErrorHandler;
import webserver.HttpHandler.Handler;
import webserver.HttpHandler.Mapping.GetMapping;
import webserver.HttpHandler.Mapping.PostMapping;
import webserver.HttpHandler.ResourceHandler;
import webserver.HttpMessage.*;
import webserver.HttpMessage.constants.eums.FileType;
import webserver.HttpMessage.constants.eums.ResponseStatus;

import static application.handler.utils.HtmlConst.ARTICLE_URL;
import static webserver.HttpMessage.constants.WebServerConst.HTTP_VERSION;

public class CommentHandler implements Handler , Authorizer{

    @GetMapping(path = "/comment")
    public Response writePage(Request request){
       if (sessionDB.getSession(getSid(request)).isEmpty()) return redirectToLogin();

       startLine = new ResponseStartLine(HTTP_VERSION, ResponseStatus.OK);
       int articleIndex;

       articleIndex = Integer.parseInt(request.getRequestQuery("index"));

       Request commentReq = new Request("GET /comment HTTP/1.1");
       String template = new String(resourceHandler.responseGet(commentReq).getBody());
       responseBody = new MessageBody(HtmlMaker.getCommentPage(template, articleIndex), FileType.HTML);
       responseHeader = writeContentResponseHeader(responseBody);

       return new Response(startLine).header(responseHeader).body(responseBody);
    }

    @PostMapping(path = "/comment")
    public Response writeComment(Request request){
        int articleIndex = Integer.parseInt(request.getRequestQuery("index"));
        String writer = userDB.findUserById(sessionDB.getSession(getSid(request)).get()).get().getName();

        createComment(request.getBody() , writer, articleIndex);

        return redirectTo(ARTICLE_URL + articleIndex);
    }

    private void createComment(MessageBody messageBody , String writer, int articleIndex){
        String content = messageBody.getContentByKey("content");

        Comment comment = new Comment(writer , articleIndex , content);

        log.info(writer + "'s Comment added to Article-" + articleIndex + " : " + content);
        commentDB.addComment(comment);
    }

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
    private final CommentDB commentDB;

    public CommentHandler(UserDB userDB, SessionDB sessionDB, ArticleDB articleDB, CommentDB commentDB) {
        this.userDB = userDB;
        this.sessionDB = sessionDB;
        this.articleDB = articleDB;
        this.commentDB = commentDB;
    }
}
