package application.db.memoryDB;

import application.db.interfaces.ArticleDB;
import application.model.Article;

import java.util.ArrayList;
import java.util.List;

public class MemArticleDB implements ArticleDB {
    private final List<Article> articles = new ArrayList<>();
    @Override
    public void addArticle(Article article) {
        articles.add(article);
    }

    @Override
    public Article getArticle(int index) {
        try {
            return articles.get(index - 1);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }
    @Override
    public int getSize() {
        return articles.size();
    }
}
