package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";
    private static final String DATABASE = "ThuVienDB";
    private static final String CONN = "jdbc:sqlserver://localhost:1433;";

    public static Connection getConnection() {
        String connectionUrl = CONN
                + "database=" + DATABASE + ";"
                + "user=" + USERNAME + ";"
                + "password=" + PASSWORD + ";";

        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}