package application.db.H2DB;

import application.db.interfaces.UserDB;
import application.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class H2UserDB extends H2DataBase implements UserDB {
    private final Connection connection;

    public H2UserDB() throws SQLException {
        this.connection = super.connection;
    }

    @Override
    public void addUser(User user) {
        String createUserQuery = "INSERT INTO BE_User (userId, password, userName, email) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement query = connection.prepareStatement(createUserQuery);
            query.setString(1, user.getUserId());
            query.setString(2 , user.getPassWord());
            query.setString(3 , user.getName());
            query.setString(4 , user.getEmail());
            query.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findUserById(String userId) {
        String findByUserIdQuery = "SELECT * FROM BE_User WHERE userId = ?";

        ResultSet resultSet;
        try {
            PreparedStatement query = connection.prepareStatement(findByUserIdQuery);
            query.setString(1 , userId);
            resultSet = query.executeQuery();

            if(!resultSet.next()) return null;
            return convertRowToUser(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<User> findAll() {
        String findAllQuery = "SELECT * FROM BE_User";

        ResultSet resultSet;
        try {
            PreparedStatement query = connection.prepareStatement(findAllQuery);
            resultSet = query.executeQuery();

            Map<String , User> users = new HashMap<>();

            while (resultSet.next()){
                users.put(resultSet.getString("userid") , convertRowToUser(resultSet));
            }

            return users.values();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private User convertRowToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString("userid") ,
                resultSet.getString("password"),
                resultSet.getString("userName"),
                resultSet.getString("email"));
    }

    @Override
    public void clear() {
        String clearQuery = "DELETE FROM BE_User";

        try {
            PreparedStatement query = connection.prepareStatement(clearQuery);
            query.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
