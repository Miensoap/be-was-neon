package application.db.H2DB;

import application.db.interfaces.CommentDB;
import application.model.Comment;

import java.sql.SQLException;
import java.util.List;

public class H2CommentDB extends H2DataBase implements CommentDB {
    public H2CommentDB() throws SQLException {
    }

    @Override
    public void addComment(Comment comment) {

    }

    @Override
    public List<Comment> getComments(int articleIndex) {
        return null;
    }
}
