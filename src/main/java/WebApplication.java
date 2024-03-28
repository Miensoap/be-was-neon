import application.db.H2DB.*;
import application.db.interfaces.*;
import application.db.memoryDB.*;
import application.handler.*;
import webserver.HttpHandler.Handler;
import webserver.WebServer;

import java.sql.SQLException;
import java.util.List;

public class WebApplication {
    static UserDB userDB;
    static SessionDB sessionDB;
    static ArticleDB articleDB;

    public static void main(String[] args) throws Exception {
        setMemDB();

        List<Handler> codeStargramHandlers = List.of(
                new LoginHandler(userDB, sessionDB),
                new UserHandler(userDB),
                new ArticleHandler(userDB, sessionDB, articleDB)
        );

        WebServer webApplicationServer = new WebServer(codeStargramHandlers);

        webApplicationServer.startServer(args);
    }

    private static void setMemDB() {
        userDB = new MemUserDB();
        sessionDB = new MemSessionDB();
        articleDB = new MemArticleDB();
    }

    private static void setH2DB() throws SQLException {
        userDB = new H2UserDB();
        sessionDB = new H2SessionDB();
        articleDB = new H2ArticleDB();
    }
}
