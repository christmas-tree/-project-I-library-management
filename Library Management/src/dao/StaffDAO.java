/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import model.Staff;
import util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private static StaffDAO instance = new StaffDAO();

    private StaffDAO() {
    }

    public static StaffDAO getInstance() {
        return instance;
    }

    // CREATE

    public boolean createStaff(Staff staff) throws SQLException {

        String sql = "INSERT INTO [staff](isAdmin, username, password, name, dob, gender, idCardNum, address)" +
                "VALUES (?, ?, HASHBYTES('SHA2_256', ?), ?, ?, ?, ?, ?)";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setBoolean(1, staff.isAdmin());
        stmt.setString(2, staff.getUsername());
        stmt.setNString(3, staff.getPassword());
        stmt.setNString(4, staff.getName());
        stmt.setDate(5, staff.getDob());
        stmt.setBoolean(6, staff.getGender());
        stmt.setLong(7, staff.getIdCardNum());
        stmt.setNString(8, staff.getAddress());

        boolean result = (stmt.executeUpdate() > 0);

        stmt.close();
        con.close();

        return result;
    }

    // READ

    public Staff getStaff(int sid) throws SQLException {
        Staff staff = null;

        String sql = "SELECT * FROM [staff] WHERE [sid]=?";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, sid);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            staff = new Staff(
                    rs.getInt("sid"),
                    rs.getTimestamp("created"),
                    rs.getBoolean("isAdmin"),
                    rs.getString("username"),
                    rs.getNString("name"),
                    rs.getDate("dob"),
                    rs.getBoolean("gender"),
                    rs.getInt("idCardNum"),
                    rs.getNString("address")
            );
        }
        rs.close();
        stmt.close();
        con.close();

        return staff;
    }

    public List<Staff> getAllStaffs() throws SQLException {

        List<Staff> staffSearchResults = new ArrayList<>();
        Staff staff;

        String sql = "SELECT * FROM [staff]";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            staff = new Staff(
                    rs.getInt("sid"),
                    rs.getTimestamp("created"),
                    rs.getBoolean("isAdmin"),
                    rs.getString("username"),
                    rs.getNString("name"),
                    rs.getDate("dob"),
                    rs.getBoolean("gender"),
                    rs.getInt("idCardNum"),
                    rs.getNString("address")
            );
            staffSearchResults.add(staff);
        }
        rs.close();
        stmt.close();
        con.close();

        return staffSearchResults;
    }

    public List<Staff> searchStaff(int searchMethod, String value)
            throws SQLException, NumberFormatException {

        List<Staff> staffSearchResults = new ArrayList<>();
        Staff staff;

        String sql = "SELECT * FROM [staff]";
        Connection con;
        PreparedStatement stmt;
        ResultSet rs;

//        String searchChoices[] = {"Mã nhân viên", "Tên", "Thời gian tạo", "Giới tính", "Vai trò", "Tên đăng nhập"};
        //                              0             1         2               3         4              5
        switch (searchMethod) {

            case 0:
                sql += " WHERE [sid]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(value));
                rs = stmt.executeQuery();
                break;

            case 1:
                sql += " WHERE [name] LIKE ?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setNString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            case 3:
                sql += " WHERE [gender]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setBoolean(1, value=="Nam"?true:false);
                rs = stmt.executeQuery();
                break;

            case 4:
                sql += " WHERE [isAdmin]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setBoolean(1, value=="Quản lý"?true:false);
                rs = stmt.executeQuery();
                break;

            case 5:
                sql += " WHERE [username]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setNString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            default:
                throw new IllegalArgumentException("Illegal Searching Method.");
        }

        while (rs.next()) {
            staff = new Staff(
                    rs.getInt("sid"),
                    rs.getTimestamp("created"),
                    rs.getBoolean("isAdmin"),
                    rs.getString("username"),
                    rs.getNString("name"),
                    rs.getDate("dob"),
                    rs.getBoolean("gender"),
                    rs.getInt("idCardNum"),
                    rs.getNString("address")
            );
            staffSearchResults.add(staff);
        }
        rs.close();
        stmt.close();
        con.close();

        return staffSearchResults;
    }

    public List<Staff> searchStaffByCreatedTime(Timestamp startDate, Timestamp endDate)
            throws SQLException {

        List<Staff> staffSearchResults = new ArrayList<>();
        Staff staff;

        String sql = "SELECT * FROM [staff] WHERE [created] BETWEEN ? AND ?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setTimestamp(1, startDate);
        stmt.setTimestamp(2, endDate);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            staff = new Staff(
                    rs.getInt("sid"),
                    rs.getTimestamp("created"),
                    rs.getBoolean("isAdmin"),
                    rs.getString("username"),
                    rs.getNString("name"),
                    rs.getDate("dob"),
                    rs.getBoolean("gender"),
                    rs.getInt("idCardNum"),
                    rs.getNString("address")
            );
            staffSearchResults.add(staff);
        }
        rs.close();
        stmt.close();
        con.close();

        return staffSearchResults;
    }

    // UPDATE

    public boolean updateStaff(Staff staff) throws SQLException {
       String sql;

        if (staff.getPassword() == null) {
            sql =    "UPDATE [staff] " +
                    "SET     isAdmin=?, " +
                    "username=?, " +
                    "name=?, " +
                    "dob=?, " +
                    "gender=?, " +
                    "idCardNum=?, " +
                    "address=? " +
                    "WHERE   [sid]=?";
        } else {
            sql =    "UPDATE [staff] " +
                    "SET     isAdmin=?, " +
                    "username=?, " +
                    "name=?, " +
                    "dob=?, " +
                    "gender=?, " +
                    "idCardNum=?, " +
                    "address=?, " +
                    "password=HASHBYTES('SHA2_256', ?) " +
                    "WHERE   [sid]=?";
        }

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setBoolean(1, staff.isAdmin());
        stmt.setString(2, staff.getUsername());
        stmt.setNString(3, staff.getName());
        stmt.setDate(4, staff.getDob());
        stmt.setBoolean(5, staff.getGender());
        stmt.setLong(6, staff.getIdCardNum());
        stmt.setNString(7, staff.getAddress());
        if (staff.getPassword() == null) {
            stmt.setInt(8, staff.getSid());
        } else {
            stmt.setNString(8, staff.getPassword());
            stmt.setInt(9, staff.getSid());
        }

        boolean result = (stmt.executeUpdate() > 0);
        stmt.close();
        con.close();

        return result;
    }

    // DELETE

    public boolean deleteStaff(Staff staff) throws SQLException {
        String sql = "DELETE FROM [staff] WHERE sid = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, staff.getSid());

        boolean result = (stmt.executeUpdate() > 0);
        stmt.close();
        con.close();

        return result;
    }
}

