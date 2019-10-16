package dao;

import model.User;
import util.DbConnection;

import java.sql.*;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        Connection con = DbConnection.getConnection();

        String sql = "SELECT * FROM [staff] WHERE [username] = ? AND [password] = HASHBYTES('SHA2_256', ?)";

        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet result = statement.executeQuery();

//        String sql2 = "SELECT * FROM [staff] WHERE [username] = \'" + username + "\' AND [password] = HASHBYTES('SHA2_256', \'" + password + "\')";
//        System.out.println(sql2);
//        Statement stmt = con.createStatement();
//
//        ResultSet result = stmt.executeQuery(sql2);

        User user = null;

        if (result.next()) {
            user = new User();
            user.setUid(result.getInt("id"));
            user.setAdmin(result.getBoolean("isAdmin"));
            user.setName(result.getString("name"));
            user.setUsername(result.getString("username"));
        }

        con.close();

        return user;
    }
}
