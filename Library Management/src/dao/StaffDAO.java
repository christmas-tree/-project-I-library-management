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
    public StaffDAO() {
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
        stmt.setInt(7, staff.getIdCardNum());
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
                    rs.getInt("uid"),
                    rs.getTimestamp("created"),
                    rs.getBoolean("isAdmin"),
                    rs.getNString("username"),
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

    public List<Staff> getAllStaff() throws SQLException {

        List<Staff> staffSearchResults = new ArrayList<>();
        Staff staff;

        String sql = "SELECT * FROM [staff]";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            staff = new Staff(
                    rs.getInt("uid"),
                    rs.getTimestamp("created"),
                    rs.getBoolean("isAdmin"),
                    rs.getNString("username"),
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

    public List<Staff> searchStaff(String searchMethod, String value)
            throws SQLException, NumberFormatException {

        List<Staff> staffSearchResults = new ArrayList<>();
        Staff staff;

        String sql = "SELECT * FROM [staff]";
        Connection con;
        PreparedStatement stmt;
        ResultSet rs;

        switch (searchMethod) {

            case "uid":
                sql += " WHERE [uid]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(value));
                rs = stmt.executeQuery();
                break;

            case "isAdmin":
                sql += " WHERE [isAdmin]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setBoolean(1, Boolean.parseBoolean(value));
                rs = stmt.executeQuery();
                break;

            case "name":
                sql += " WHERE [name] LIKE ?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setNString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            case "gender":
                sql += " WHERE [gender]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setBoolean(1, Boolean.parseBoolean(value));
                rs = stmt.executeQuery();
                break;

            case "username":
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
                    rs.getInt("uid"),
                    rs.getTimestamp("created"),
                    rs.getBoolean("isAdmin"),
                    rs.getNString("username"),
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
                    rs.getInt("uid"),
                    rs.getTimestamp("created"),
                    rs.getBoolean("isAdmin"),
                    rs.getNString("username"),
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
        String sql =    "UPDATE [staff] " +
                        "SET     isAdmin=?, " +
                                "username=?, " +
                                "password=HASHBYTES('SHA2_256', ?), " +
                                "name=?, " +
                                "dob=?, " +
                                "gender=?, " +
                                "idCardNum=?, " +
                                "address=? " +
                        "WHERE   [uid]=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setBoolean(1, staff.isAdmin());
        stmt.setString(2, staff.getUsername());
        stmt.setString(3, staff.getPassword());
        stmt.setNString(4, staff.getName());
        stmt.setDate(5, staff.getDob());
        stmt.setBoolean(6, staff.getGender());
        stmt.setInt(7, staff.getIdCardNum());
        stmt.setNString(8, staff.getAddress());
        stmt.setInt(9, staff.getSid());

        boolean result = (stmt.executeUpdate() > 0);
        stmt.close();
        con.close();

        return result;
    }

    // DELETE

    public boolean deleteStaff(Staff staff) throws SQLException {
        String sql = "DELETE FROM [staff] WHERE uid = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, staff.getSid());

        boolean result = (stmt.executeUpdate() > 0);
        stmt.close();
        con.close();

        return result;
    }
}

