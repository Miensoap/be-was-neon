package application.db.H2DB;

import application.db.interfaces.CommentDB;
import application.model.Comment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class H2CommentDB extends H2DataBase implements CommentDB {
    public H2CommentDB() throws SQLException {
    }

    @Override
    public void addComment(Comment comment) {
        String createCommentQuery = "INSERT INTO Comment (content , userName , articleIndex) VALUES (? ,?, ?);";

        try (PreparedStatement query = connection.prepareStatement(createCommentQuery)) {
            query.setString(1, comment.content());
            query.setString(2, comment.writer());
            query.setInt(3, comment.articleIndex());
            query.executeUpdate();

            logInfo("Insert Comment to Article " + comment.articleIndex());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Comment> getComments(int articleIndex) {
        String findAllQuery = "SELECT * FROM  Comment WHERE articleIndex= ? ;";

        List<Comment> comments = new ArrayList<>();
        try (PreparedStatement query = connection.prepareStatement(findAllQuery)) {
            query.setInt(1, articleIndex);
            try(ResultSet resultSet = query.executeQuery()) {
                while (resultSet.next()){
                    comments.add(convertRowToComment(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comments;
    }

    private Comment convertRowToComment(ResultSet resultSet) throws SQLException {
        String content = resultSet.getString("content");
        String writer = resultSet.getString("userName");
        int index =resultSet.getInt("articleIndex");
        return new Comment(writer , index , content);
    }
}
