/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import model.Reader;
import util.DbConnection;
import util.ExHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderDAO {
    private static ReaderDAO instance = new ReaderDAO();

    private ReaderDAO() {
    }
    
    public static ReaderDAO getInstance() {
        return instance;
    }
    
    // CREATE
    public boolean createReader(Reader reader) throws SQLException {
        String sql = "INSERT INTO [reader](created, name, dob, gender, idCardNum, address, canBorrow) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        stmt.setNString(2, reader.getName());
        stmt.setDate(3, reader.getDob());
        stmt.setBoolean(4, reader.getGender());
        stmt.setLong(5, reader.getIdCardNum());
        stmt.setNString(6, reader.getAddress());
        stmt.setBoolean(7, reader.isCanBorrow());

        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }

    public void importReader(ArrayList<Reader> readers) {
        String sql = "INSERT INTO [reader](created, name, dob, gender, idCardNum, address, canBorrow) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection con = DbConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            String err = "";
            Reader reader = null;
            for (int i = 0; i < readers.size(); i++) {
                reader = readers.get(i);
                try {
                    stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    stmt.setNString(2, reader.getName());
                    stmt.setDate(3, reader.getDob());
                    stmt.setBoolean(4, reader.getGender());
                    stmt.setLong(5, reader.getIdCardNum());
                    stmt.setNString(6, reader.getAddress());
                    stmt.setBoolean(7, reader.isCanBorrow());

                    stmt.executeUpdate();
                } catch (SQLException e) {
                    err += "Có vấn đề nhập độc giả số " + (i+1) + " - "+ reader.getName() + ".\n";
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

    public Reader getReader(String rid) throws SQLException {
        String sql = "SELECT * FROM [reader] WHERE rid=?";
        Reader reader = null;

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, rid);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            reader = new Reader(
                    rs.getInt("rid"),
                    rs.getTimestamp("created"),
                    rs.getNString("name"),
                    rs.getDate("dob"),
                    rs.getBoolean("gender"),
                    rs.getLong("idCardNum"),
                    rs.getNString("address"),
                    rs.getBoolean("canBorrow")
            );
        }

        rs.close();
        con.close();

        return reader;
    }

    public List<Reader> getAllReaders() throws SQLException {
        String sql = "SELECT * FROM [reader]";
        Reader reader = null;
        List<Reader> readerList = new ArrayList<>();

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            reader = new Reader(
                    rs.getInt("rid"),
                    rs.getTimestamp("created"),
                    rs.getNString("name"),
                    rs.getDate("dob"),
                    rs.getBoolean("gender"),
                    rs.getLong("idCardNum"),
                    rs.getNString("address"),
                    rs.getBoolean("canBorrow")
            );
            readerList.add(reader);
        }

        rs.close();
        con.close();

        return readerList;
    }

    public List<Reader> searchReader(int searchMethod, String value)
            throws SQLException, NumberFormatException {

        List<Reader> readerSearchResults = new ArrayList<>();
        Reader reader;

        String sql = "SELECT * FROM reader";
        Connection con;
        PreparedStatement stmt;
        ResultSet rs;

//        String searchChoices[] = {"Mã độc giả", "Tên", "Thời gian tạo", "Giới tính", "Trạng thái"};

        switch (searchMethod) {

            case 0: // Ma doc gia
                System.out.println("Running search DAO 1");
                sql += " WHERE rid=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                try {
                    stmt.setInt(1, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    ExHandler.handle(e);
                }
                rs = stmt.executeQuery();
                break;

            case 1: // Ten
                System.out.println("Running search DAO 1");
                sql += " WHERE [name] LIKE ?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setNString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            case 3: // Gioi Tinh
                sql += " WHERE [gender]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setBoolean(1, (value=="Nam")?true:false);
                rs = stmt.executeQuery();
                break;

            case 4: // Trang thai
                sql += " WHERE [canBorrow]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setBoolean(1, (value=="Được mượn")?true:false);
                rs = stmt.executeQuery();
                break;

            default:
                throw new IllegalArgumentException("Illegal Searching Method.");
        }

        while (rs.next()) {
            reader = new Reader(
                    rs.getInt("rid"),
                    rs.getTimestamp("created"),
                    rs.getNString("name"),
                    rs.getDate("dob"),
                    rs.getBoolean("gender"),
                    rs.getInt("idCardNum"),
                    rs.getNString("address"),
                    rs.getBoolean("canBorrow")
            );
            readerSearchResults.add(reader);
        }
        rs.close();
        stmt.close();
        con.close();

        return readerSearchResults;
    }

    public List<Reader> searchReaderByCreatedTime(Timestamp startDate, Timestamp endDate)
            throws SQLException {

        List<Reader> readerSearchResults = new ArrayList<>();
        Reader reader;

        String sql = "SELECT * FROM [reader] WHERE [created] BETWEEN ? AND ?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setTimestamp(1, startDate);
        stmt.setTimestamp(2, endDate);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            reader = new Reader(
                    rs.getInt("rid"),
                    rs.getTimestamp("created"),
                    rs.getNString("name"),
                    rs.getDate("dob"),
                    rs.getBoolean("gender"),
                    rs.getLong("idCardNum"),
                    rs.getNString("address"),
                    rs.getBoolean("canBorrow")
            );
            readerSearchResults.add(reader);
        }
        rs.close();
        stmt.close();
        con.close();

        return readerSearchResults;
    }

    // UPDATE
    public boolean updateReader(Reader reader) throws SQLException {
        String sql = "UPDATE [reader]" +
                "SET name=?, dob=?, gender=?, idCardNum=?, address=?, canBorrow=? " +
                "WHERE rid=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setNString(1, reader.getName());
        stmt.setDate(2, reader.getDob());
        stmt.setBoolean(3, reader.getGender());
        stmt.setLong(4, reader.getIdCardNum());
        stmt.setNString(5, reader.getAddress());
        stmt.setBoolean(6, reader.isCanBorrow());
        stmt.setInt(7, reader.getRid());

        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }


    // DELETE
    public boolean deleteReader(Reader reader) throws SQLException {
        String sql = "DELETE FROM [reader] WHERE rid=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, reader.getRid());
        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }
}
