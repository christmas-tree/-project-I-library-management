package dto;

import model.User;
import util.DbConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UserDTO {

    public User authenticate(String username, String password) throws SQLException {
        Connection con = DbConnection.getConnection();

//        String sql = "SELECT * FROM [staff] WHERE [username] = ? AND [password] = HASHBYTES('SHA2_256', ?)";
//
//        PreparedStatement statement = con.prepareStatement(sql);
//        statement.setString(1, username);
//        statement.setString(2, password);
//        ResultSet result = statement.executeQuery();

        String sql2 = "SELECT * FROM [staff] WHERE [username] = \'" + username + "\' AND [password] = HASHBYTES('SHA2_256', \'" + password + "\')";
        System.out.println(sql2);
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery(sql2);

        User user = null;

        if (rs.next()) {
            user = new User();
            user.setUid(rs.getInt("id"));
            user.setAdmin(rs.getBoolean("isAdmin"));
            user.setName(rs.getString("name"));
            user.setUsername(rs.getString("username"));
        }

        con.close();

        return user;
    }
}
