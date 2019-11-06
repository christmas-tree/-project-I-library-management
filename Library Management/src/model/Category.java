/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package model;

public class Category {
    private String catId;
    private String catName;

    public Category(String catId, String catName) {
        this.catId = catId;
        this.catName = catName;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }


}
