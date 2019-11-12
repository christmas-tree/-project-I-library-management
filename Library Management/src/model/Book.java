/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package model;

import java.sql.Timestamp;

public class Book {
    private String bid;
    private Timestamp created;
    private String bookName;
    private long price;
    private Category category;
    private String author;
    private Publisher publisher;
    private int pubYear;
    private Language language;
    private String location;
    private int quantity;
    private int availQuantity;

    public Book() {
    }

    public Book(String bid, Timestamp created, String bookName, long price, Category category, String author, Publisher publisher, int pubYear, Language language, String location, int quantity, int availQuantity) {
        this.bid = bid;
        this.created = created;
        this.bookName = bookName;
        this.price = price;
        this.category = category;
        this.author = author;
        this.publisher = publisher;
        this.pubYear = pubYear;
        this.language = language;
        this.location = location;
        this.quantity = quantity;
        this.availQuantity = availQuantity;
    }

    public Book(String bid, String bookName) {
        this.bid = bid;
        this.bookName = bookName;
    }

    public Book(String bid, String bookName, int availQuantity) {
        this.bid = bid;
        this.bookName = bookName;
        this.availQuantity = availQuantity;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public int getPubYear() {
        return pubYear;
    }

    public void setPubYear(int pubYear) {
        this.pubYear = pubYear;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailQuantity() {
        return availQuantity;
    }

    public void setAvailQuantity(int availQuantity) {
        this.availQuantity = availQuantity;
    }
}
