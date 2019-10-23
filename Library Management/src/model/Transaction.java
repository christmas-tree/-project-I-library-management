package model;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Transaction {
    private int transactId;
    private Timestamp borrowingDate;
    private Reader borrower;
    private Staff borrowStaff;
    private ArrayList<TransactionDetail> details;

    public Transaction() {
    }

    public Transaction(int transactId, Timestamp borrowingDate, Reader borrower, Staff borrowStaff) {
        this.transactId = transactId;
        this.borrowingDate = borrowingDate;
        this.borrower = borrower;
        this.borrowStaff = borrowStaff;
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

    public ArrayList<TransactionDetail> getAllDetails() {
        return details;
    }

    public TransactionDetail getDetail(int index) {
        return details.get(index);
    }

    public void addDetail(Book book, Timestamp dueDate, int deposit) {
        this.details.add(new TransactionDetail(book, dueDate, deposit));
    }

    //public void removeDetail(b)

    public class TransactionDetail {
        private Book book;
        private Staff returnStaff;
        private Timestamp dueDate;
        private Timestamp returnDate;
        private int deposit;
        private boolean isExtended;
        private int fine;

        private TransactionDetail(Book book, Timestamp dueDate, int deposit) {
            this.book = book;
            this.dueDate = dueDate;
            this.deposit = deposit;
            this.isExtended = false;
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

        public Timestamp getDueDate() {
            return dueDate;
        }

        public void setDueDate(Timestamp dueDate) {
            this.dueDate = dueDate;
        }

        public Timestamp getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(Timestamp returnDate) {
            this.returnDate = returnDate;
        }

        public int getDeposit() {
            return deposit;
        }

        public void setDeposit(int deposit) {
            this.deposit = deposit;
        }

        public boolean isExtended() {
            return isExtended;
        }

        public void setExtended(boolean extended) {
            isExtended = extended;
        }

        public int getFine() {
            return fine;
        }

        public void setFine(int fine) {
            this.fine = fine;
        }
    }
}
