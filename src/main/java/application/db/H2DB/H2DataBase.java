package application.db.H2DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class H2DataBase {
    private String url = "jdbc:h2:~/codestagram";
    private String id ="soap";
    private String password = "1234";
    protected final Connection connection;
    public H2DataBase() throws SQLException {
        connection = DriverManager.getConnection(url , id , password);
    }
}
