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
import java.util.Optional;

public class H2UserDB extends H2DataBase implements UserDB {
    private final Connection connection;

    public H2UserDB() throws SQLException {
        this.connection = super.connection;
    }

    @Override
    public void addUser(User user) {
        if (findUserById(user.getUserId()).isPresent()) throw new IllegalArgumentException("이미 존재하는 회원 ID");

        String createUserQuery = "INSERT INTO BE_User (userId, password, userName, email) VALUES (?, ?, ?, ?)";

        try (PreparedStatement query = getConnection().prepareStatement(createUserQuery)) {
            query.setString(1, user.getUserId());
            query.setString(2, user.getPassWord());
            query.setString(3, user.getName());
            query.setString(4, user.getEmail());
            query.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findUserById(String userId) {
        String findByUserIdQuery = "SELECT * FROM BE_User WHERE userId = ?";

        try (PreparedStatement query = getConnection().prepareStatement(findByUserIdQuery)) {
            query.setString(1, userId);
            try(ResultSet resultSet = query.executeQuery()){
                if(resultSet.next()) return convertRowToUser(resultSet);
            }
        } catch (SQLException e) {
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> findAll() {
        String findAllQuery = "SELECT * FROM BE_User";
        Map<String, User> users = new HashMap<>();

        try (PreparedStatement query = getConnection().prepareStatement(findAllQuery)) {
            try(ResultSet resultSet = query.executeQuery()) {

                while (resultSet.next()) {
                    users.put(resultSet.getString("userid"), convertRowToUser(resultSet).get());
                }
                return users.values();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<User> convertRowToUser(ResultSet resultSet) {
        String id, password, name, email;
        try {
            id = resultSet.getString("USERID");
            password = resultSet.getString("PASSWORD");
            name = resultSet.getString("USERNAME");
            email = resultSet.getString("EMAIL");

            return Optional.of(new User(id, password, name, email));
        } catch (SQLException fail) {
            return Optional.empty();
        }
    }

    @Override
    public void clear() {
        String clearQuery = "DELETE FROM BE_User";

        try {
            try (PreparedStatement query = getConnection().prepareStatement(clearQuery)) {
                query.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
