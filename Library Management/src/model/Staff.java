package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Staff {

    private int uid;
    private Timestamp created;
    private boolean isAdmin;
    private String username;
    private String password;
    private String name;
    private Date dob;
    private boolean gender;
    private int idCardNum;
    private String address;

    public Staff(int uid, Timestamp created, boolean isAdmin, String username, String name, Date dob, boolean gender, int idCardNum, String address) {
        this.uid = uid;
        this.created = created;
        this.isAdmin = isAdmin;
        this.username = username;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.idCardNum = idCardNum;
        this.address = address;
    }

    public Staff(boolean isAdmin, String username, String password, String name, Date dob, boolean gender, int idCardNum, String address) {
        this.isAdmin = isAdmin;
        this.username = username;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.idCardNum = idCardNum;
        this.address = address;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public int getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(int idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
