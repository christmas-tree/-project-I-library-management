/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Transaction {
    private int transactId;
    private Timestamp borrowingDate;
    private Reader borrower;
    private Staff borrowStaff;
    private Timestamp dueDate;
    private int quantity = 0;
    private ObservableList<TransactionDetail> details = FXCollections.observableArrayList();

    public Transaction() {
    }

    public Transaction(int transactId, Timestamp borrowingDate, Reader borrower, Staff borrowStaff, Timestamp dueDate) {
        this.transactId = transactId;
        this.borrowingDate = borrowingDate;
        this.borrower = borrower;
        this.borrowStaff = borrowStaff;
        this.dueDate = dueDate;
    }

    public Transaction(int transactId, Timestamp borrowingDate, Reader borrower, Staff borrowStaff, Timestamp dueDate, int quantity) {
        this.transactId = transactId;
        this.borrowingDate = borrowingDate;
        this.borrower = borrower;
        this.borrowStaff = borrowStaff;
        this.dueDate = dueDate;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public int getTransactId() {
        return transactId;
    }

    public void setTransactId(int transactId) {
        this.transactId = transactId;
    }

    public Timestamp getBorrowingDate() {
        return borrowingDate;
    }

    public void setBorrowingDate(Timestamp borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public Reader getBorrower() {
        return borrower;
    }

    public void setBorrower(Reader borrower) {
        this.borrower = borrower;
    }

    public Staff getBorrowStaff() {
        return borrowStaff;
    }

    public void setBorrowStaff(Staff borrowStaff) {
        this.borrowStaff = borrowStaff;
    }

    public ObservableList<TransactionDetail> getAllDetails() {
        return details;
    }

    public TransactionDetail getDetail(int index) {
        return details.get(index);
    }

    public TransactionDetail addDetail(Book book, long deposit) {
        TransactionDetail newDetail = new TransactionDetail(book, deposit);
        this.details.add(newDetail);
        return newDetail;
    }

    public void addDetail(Book book, Staff returnStaff, Timestamp returnDate, long deposit, boolean isExtended, long fine) {
        this.details.add(new TransactionDetail(book, returnStaff, returnDate, deposit, isExtended, fine));
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public void setDetails(ObservableList<TransactionDetail> details) {
        this.details = details;
    }

    //public void removeDetail(b)

    public class TransactionDetail {
        private Book book;
        private Staff returnStaff;
        private Timestamp returnDate;
        private long deposit;
        private boolean isExtended;
        private long fine;
        private boolean isNew;

        public TransactionDetail(Book book, long deposit) {
            this.book = book;
            this.deposit = deposit;
            this.isExtended = false;
            this.isNew = true;
        }

        private TransactionDetail(Book book, Staff returnStaff, Timestamp returnDate, long deposit, boolean isExtended, long fine) {
            this.book = book;
            this.returnStaff = returnStaff;
            this.returnDate = returnDate;
            this.deposit = deposit;
            this.isExtended = isExtended;
            this.fine = fine;
            this.isNew = false;
        }

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }

        public Staff getReturnStaff() {
            return returnStaff;
        }

        public void setReturnStaff(Staff returnStaff) {
            this.returnStaff = returnStaff;
        }

        public Timestamp getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(Timestamp returnDate) {
            this.returnDate = returnDate;
        }

        public long getDeposit() {
            return deposit;
        }

        public void setDeposit(long deposit) {
            this.deposit = deposit;
        }

        public boolean isExtended() {
            return isExtended;
        }

        public void setExtended(boolean extended) {
            isExtended = extended;
        }

        public long getFine() {
            return fine;
        }

        public void setFine(long fine) {
            this.fine = fine;
        }

        public boolean isNew() {
            return isNew;
        }
    }
}
