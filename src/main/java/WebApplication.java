import application.db.H2DB.*;
import application.db.interfaces.*;
import application.db.memoryDB.*;
import application.handler.*;
import webserver.HttpHandler.Handler;
import webserver.WebServer;

import java.sql.SQLException;
import java.util.List;

public class WebApplication {
    private static UserDB userDB;
    private static SessionDB sessionDB;
    private static ArticleDB articleDB;
    private static CommentDB commentDB;

    public static void main(String[] args)  throws Exception {
        setMemDB();
//        setH2DB();

        List<Handler> codeStargramHandlers = List.of(
                new LoginHandler(userDB, sessionDB),
                new UserHandler(userDB, sessionDB),
                new ArticleHandler(userDB, sessionDB, articleDB, commentDB),
                new CommentHandler(userDB , sessionDB , articleDB , commentDB)
        );

        WebServer webApplicationServer = new WebServer(codeStargramHandlers);

        webApplicationServer.startServer(args);
    }

    private static void setMemDB() {
        userDB = new MemUserDB();
        sessionDB = new MemSessionDB();
        articleDB = new MemArticleDB();
        commentDB = new MemCommentDB();
    }

    private static void setH2DB() throws SQLException {
        userDB = new H2UserDB();
        sessionDB = new H2SessionDB();
        articleDB = new H2ArticleDB();
        commentDB = new H2CommentDB();
    }
}
