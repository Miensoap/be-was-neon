package application.db.memoryDB;

import application.db.interfaces.CommentDB;
import application.model.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemCommentDB implements CommentDB {
    private final List<Comment> comments = new ArrayList<>();
    @Override
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    @Override
    public List<Comment> getComments(int articleIndex) {
        return comments.stream().filter(c -> c.articleIndex() == articleIndex).collect(Collectors.toList());
    }
}
