/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Staff {

    private int sid;
    private Timestamp created;
    private boolean isAdmin;
    private String username;
    private String password;
    private String name;
    private Date dob;
    private boolean gender;
    private long idCardNum;
    private String address;

    public Staff(int uid, Timestamp created, boolean isAdmin, String username, String name, Date dob, boolean gender, long idCardNum, String address) {
        this.sid = uid;
        this.created = created;
        this.isAdmin = isAdmin;
        this.username = username;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.idCardNum = idCardNum;
        this.address = address;
    }

    public Staff(boolean isAdmin, String username, String password, String name, Date dob, boolean gender, long idCardNum, String address) {
        this.isAdmin = isAdmin;
        this.username = username;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.idCardNum = idCardNum;
        this.address = address;
    }

    public Staff() {
    }

    public Staff(int sid, String name) {
        this.sid = sid;
        this.name = name;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int uid) {
        this.sid = uid;
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

    public long getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(long idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name;
    }
}
