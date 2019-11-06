/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import model.Category;
import util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CategoryDAO {

    public CategoryDAO() {
    }

    // CREATE
    public boolean createCategory(Category category) throws SQLException {
        String sql = "INSERT INTO [category](catId, catName) VALUES (?, ?)";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, category.getCatId());
        stmt.setNString(2, category.getCatName());
        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }
    // READ

    public Category getCategory(String catId) throws SQLException {
        String sql = "SELECT * FROM [category] WHERE catId=?";
        Category category = null;

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, catId);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            category = new Category(rs.getString("catId"), rs.getNString("catName"));
        }

        rs.close();
        con.close();

        return category;
    }

    public Map<String, Category> getAllCategories() throws SQLException {
        String sql = "SELECT * FROM [category]";
        Category category = null;
        Map<String, Category> catList = new HashMap();

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            category = new Category(rs.getString("catId"), rs.getNString("catName"));
            catList.put(category.getCatId(), category);
        }

        rs.close();
        con.close();

        return catList;
    }

    // UPDATE
    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE [category] SET catName=? WHERE catId=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setNString(1, category.getCatName());
        stmt.setString(2, category.getCatId());

        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }

    // DELETE
    public boolean deleteCategory(Category category) throws SQLException {
        String sql = "DELETE FROM [category] WHERE catId=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, category.getCatId());
        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }

}
