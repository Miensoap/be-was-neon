package application.db.H2DB;

import application.db.interfaces.SessionDB;
import application.model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class H2SessionDB extends H2DataBase implements SessionDB {
    private final Connection connection;

    public H2SessionDB() throws SQLException {
        this.connection = super.connection;
        clear();
    }

    /**
     * Session Id 에 해당하는 세션을 찾아 User Id 를 반환
     *
     * @param sessionId
     * @return
     */
    @Override
    public Optional<String> getSession(String sessionId) {
        String findByUserIdQuery = "SELECT * FROM Session WHERE sessionId = ?";

        try (PreparedStatement query = connection.prepareStatement(findByUserIdQuery)) {
            query.setString(1, sessionId);
            try (ResultSet resultSet = query.executeQuery()) {
                if(resultSet.next()) return Optional.of(resultSet.getString("userId"));
            }
        } catch (SQLException e) {
        }
        return Optional.empty();
    }

    @Override
    public void addSession(Session session) {
        String addSessionQuery = "INSERT INTO Session (sessionId, userId) VALUES (? , ?);";
        try {
            PreparedStatement query = connection.prepareStatement(addSessionQuery);
            query.setString(1, session.getSessionId());
            query.setString(2, session.getUserId());
            query.executeUpdate();

            logInfo("Insert Session : " + session.getSessionId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void removeSession(String sessionId) {
        String removeSessionQuery = "DELETE FROM Session WHERE sessionId = ?;";
        try {
            PreparedStatement query = connection.prepareStatement(removeSessionQuery);
            query.setString(1, sessionId);
            query.executeUpdate();

            logInfo("Delete Session : " + sessionId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getSize() {
        String getSizeQuery = "SELECT COUNT(*) FROM Session;";
        try (PreparedStatement query = connection.prepareStatement(getSizeQuery)){
            try(ResultSet resultSet = query.executeQuery()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        String clearQuery = "DELETE FROM Session";
        try {
            PreparedStatement query = connection.prepareStatement(clearQuery);
            query.executeUpdate();

            logInfo("Delete All Session : ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
