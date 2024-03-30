package application.db.interfaces;

import application.model.Article;

public interface ArticleDB {
    /**
     * DB에 게시글을 저장
     * @param article 게시글
     */
    void addArticle(Article article);

    /**
     * DB에 저장된 게시글을 인덱스로 조회해 반환
     * @param index 게시글 인덱스
     * @return 게시글
     */
    Article getArticle(int index);

    /**
     * DB에 저장된 게시글 수를 반환
     * @return
     */
    int getSize();
}
