package application.db.H2DB;

import application.db.interfaces.ArticleDB;
import application.model.Article;

import java.sql.SQLException;

public class H2ArticleDB extends H2DataBase implements ArticleDB {
    public H2ArticleDB() throws SQLException {
    }

    @Override
    public void addArticle(Article article) {

    }

    @Override
    public Article getArticle(int index) {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
