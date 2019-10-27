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
        statement.setNString(2, password);
        ResultSet rs = statement.executeQuery();

//        String sql = "SELECT * FROM [staff] WHERE [username] = \'" + username + "\' AND [password] = HASHBYTES('SHA2_256', \'" + password + "\')";
//        System.out.println(sql);
//        Statement stmt = con.createStatement();

//        ResultSet rs = stmt.executeQuery(sql);

        User user = null;

        if (rs.next()) {
            user = new User();
            user.setUid(rs.getInt("sid"));
            user.setAdmin(rs.getBoolean("isAdmin"));
            user.setName(rs.getString("name"));
            user.setUsername(rs.getString("username"));
        }

        con.close();

        return user;
    }
}
