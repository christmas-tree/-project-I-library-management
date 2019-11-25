/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Reader {
    private int rid;
    private Timestamp created;
    private String name;
    private Date dob;
    private boolean gender;
    private long idCardNum;
    private String address;
    private boolean canBorrow;

    public Reader() {
    }

    public Reader(int rid, String name) {
        this.rid = rid;
        this.name = name;
    }

    public Reader(int rid, Timestamp created, String name, Date dob, boolean gender, long idCardNum, String address, boolean canBorrow) {
        this.rid = rid;
        this.created = created;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.idCardNum = idCardNum;
        this.address = address;
        this.canBorrow = canBorrow;
    }

    public Reader(String name, Date dob, boolean gender, long idCardNum, String address) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.idCardNum = idCardNum;
        this.address = address;
    }

    public Reader(int rid, String name, boolean canBorrow) {
        this.rid = rid;
        this.name = name;
        this.canBorrow = canBorrow;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
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

    public boolean isCanBorrow() {
        return canBorrow;
    }

    public void setCanBorrow(boolean canBorrow) {
        this.canBorrow = canBorrow;
    }

    @Override
    public String toString() {
        return name;
    }
}
