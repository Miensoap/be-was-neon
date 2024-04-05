package application.db.interfaces;

import application.model.Comment;

import java.util.List;

public interface CommentDB {

    /**
     * DB에 댓글을 저장
     * @param comment 댓글
     */
    void addComment(Comment comment);

    /**
     * DB에 저장된 게시글을 인덱스로 조회해 반환
     * @param articleIndex 게시글 인덱스
     * @return
     */
    List<Comment> getComments(int articleIndex);
}
