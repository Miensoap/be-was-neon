package application.db.H2DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class H2DataBase {
    private String url = "jdbc:h2:~/codestagram" + ";DB_CLOSE_ON_EXIT=FALSE";
    private String id ="soap";
    private String password = "1234";
    protected final Connection connection;
    private final Logger logger = LoggerFactory.getLogger("H2DB");

    public H2DataBase() throws SQLException {
        connection = DriverManager.getConnection(url , id , password);
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, id , password);
    }

    protected void logInfo(String message){
        logger.info(message);
    }
}
