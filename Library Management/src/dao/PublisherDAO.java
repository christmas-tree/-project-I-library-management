/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Publisher;
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

public class PublisherDAO {

    private static PublisherDAO instance = new PublisherDAO();

    private PublisherDAO() {
    }

    public static PublisherDAO getInstance() {
        return instance;
    }
    // CREATE
    public boolean createPublisher(Publisher publisher) throws SQLException {
        String sql = "INSERT INTO [publisher](pubId, pubName) VALUES (?, ?)";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, publisher.getPubId());
        stmt.setNString(2, publisher.getPubName());
        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }

    public void importPublisher(ArrayList<Publisher> publishers) {
        String sql = "INSERT INTO [publisher](pubId, pubName) VALUES (?, ?)";

        try {
            Connection con = DbConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            String err = "";
            Publisher publisher = null;
            for (int i = 0; i < publishers.size(); i++) {
                publisher = publishers.get(i);
                try {
                    stmt.setString(1, publisher.getPubId());
                    stmt.setNString(2, publisher.getPubName());

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    err += "Có vấn đề nhập mục thứ " + (i+1) + " - Mã: " + publisher.getPubId() + ".\n";
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

    public Publisher getPublisher(String pubId) throws SQLException {
        String sql = "SELECT * FROM [publisher] WHERE pubId=?";
        Publisher publisher = null;

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, pubId);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            publisher = new Publisher(rs.getString("pubId"), rs.getNString("pubName"));
        }

        rs.close();
        con.close();

        return publisher;
    }

    public Map<String, Publisher> getAllPublishers() throws SQLException {
        String sql = "SELECT * FROM [publisher] ORDER BY pubId";
        Publisher publisher = null;
        Map<String, Publisher> pubList = new HashMap<>();

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            publisher = new Publisher(rs.getString("pubId"), rs.getNString("pubName"));
            pubList.put(publisher.getPubId(), publisher);
        }

        rs.close();
        con.close();

        return pubList;
    }

    public ObservableList<Publisher> getPublisherList() throws SQLException {
        String sql = "SELECT * FROM [publisher]";
        Publisher publisher = null;
        ObservableList<Publisher> pubList = FXCollections.observableArrayList();

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            publisher = new Publisher(rs.getString("pubId"), rs.getNString("pubName"));
            pubList.add(publisher);
        }

        rs.close();
        con.close();

        return pubList;
    }

    // UPDATE
    public boolean updatePublisher(Publisher publisher) throws SQLException {
        String sql = "UPDATE [publisher] SET pubName=? WHERE pubId=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setNString(1, publisher.getPubName());
        stmt.setString(2, publisher.getPubId());

        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }

    // DELETE
    public boolean deletePublisher(Publisher publisher) throws SQLException {
        String sql = "DELETE FROM [publisher] WHERE pubId=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, publisher.getPubId());
        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }

}
