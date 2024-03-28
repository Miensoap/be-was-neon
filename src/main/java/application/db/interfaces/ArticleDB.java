package application.db.interfaces;

import application.model.Article;

public interface ArticleDB {
    void addArticle(Article article);
    Article getArticle(int index);
    int getSize();
}
