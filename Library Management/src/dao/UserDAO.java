/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import model.User;
import util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDAO {

    public UserDAO() {
    }

    public User authenticate(String username, String password) throws SQLException {
        Connection con = DbConnection.getConnection();

        String sql = "SELECT * FROM [staff] WHERE [username] = ? AND [password] = HASHBYTES('SHA2_256', ?)";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setNString(2, password);
        ResultSet rs = stmt.executeQuery();

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

        rs.close();
        stmt.close();
        con.close();

        return user;
    }
}
