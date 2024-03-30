package application.db.interfaces;

import application.model.Comment;

import java.util.List;

public interface CommentDB {
    void addComment(Comment comment);
    List<Comment> getComments(int articleIndex);
}
