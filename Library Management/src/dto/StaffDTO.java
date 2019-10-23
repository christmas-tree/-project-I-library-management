package dto;

import model.Staff;
import util.AlertPanel;
import util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDTO {
    public StaffDTO() {
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        Staff staff;

            String sql = "SELECT * FROM [staff]";
            Connection con = DbConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                staff = new Staff();
                staff.setUid(rs.getInt("uid"));
                staff.setCreated(rs.getTimestamp("created"));
                staff.setAdmin(rs.getBoolean("isAdmin"));
                staff.setUsername(rs.getNString("username"));
                staff.setName(rs.getNString("name"));
                staff.setDob(rs.getDate("dob"));
                staff.setGender(rs.getBoolean("gender"));
                staff.setIdCardNum(rs.getInt("idCardNum"));
                staff.setAddress(rs.getNString("address"));
                staffList.add(staff);
            }
            rs.close();
            con.close();

        return staffList;
    }
}

