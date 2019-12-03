/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Category;
import util.DbConnection;
import util.ExHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDAO {

    private static CategoryDAO instance = new CategoryDAO();

    private CategoryDAO() {
    }

    public static CategoryDAO getInstance() {
        return instance;
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

    public void importCategory(ArrayList<Category> categories) {
        String sql = "INSERT INTO [category](catId, catName) VALUES (?, ?)";

        try {
            Connection con = DbConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            String err = "";
            Category category = null;
            for (int i = 0; i < categories.size(); i++) {
                category = categories.get(i);
                try {
                    stmt.setString(1, category.getCatId());
                    stmt.setNString(2, category.getCatName());

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    err += "Có vấn đề nhập mục thứ " + (i+1) + " - Mã: " + category.getCatId() + ".\n";
                }
            }
            stmt.close();
            con.close();

            if (!err.isBlank())
                ExHandler.handleLong(err);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }
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

    public ObservableList<Category> getCategoryList() throws SQLException {
        String sql = "SELECT * FROM [category]";
        Category category = null;
        ObservableList<Category> catList = FXCollections.observableArrayList();

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            category = new Category(rs.getString("catId"), rs.getNString("catName"));
            catList.add(category);
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
