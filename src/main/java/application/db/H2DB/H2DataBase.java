package application.db.H2DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class H2DataBase {
    private String url = "jdbc:h2:~/codestagram" + ";DB_CLOSE_ON_EXIT=FALSE";
    private String id ="soap";
    private String password = "1234";
    protected final Connection connection;
    public H2DataBase() throws SQLException {
        connection = DriverManager.getConnection(url , id , password);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, id , password);
    }
}
