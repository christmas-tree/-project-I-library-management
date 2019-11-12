/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import model.Book;
import model.Reader;
import model.Staff;
import model.Transaction;
import util.DbConnection;
import util.ExHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class TransactionDAO {

    private static TransactionDAO instance = new TransactionDAO();

    private TransactionDAO() {
    }

    public static TransactionDAO getInstance() {
        return instance;
    }

    // CREATE

    public void createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO [transaction](borrowingDate, rid, borrowStaffId, dueDate) OUTPUT transactId VALUES (?, ?, ?, ?)";
        String sql2 = "INSERT INTO [transactionDetail](transactId, bid, deposit) " +
                "VALUES (?, ?, ?)";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setTimestamp(1, transaction.getBorrowingDate());
        stmt.setInt(2, transaction.getBorrower().getRid());
        stmt.setInt(3, transaction.getBorrowStaff().getSid());
        stmt.setTimestamp(4, transaction.getDueDate());

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            transaction.setTransactId(rs.getInt("transactId"));
        }
        rs.close();
        stmt.close();

        PreparedStatement stmt2 = con.prepareStatement(sql2);

        boolean success = true;

        transaction.getAllDetails().forEach((transactionDetail) -> {
            try {
                stmt2.setInt(1, transaction.getTransactId());
                stmt2.setString(2, transactionDetail.getBook().getBid());
                stmt2.setLong(3, transactionDetail.getDeposit());

                stmt2.executeUpdate();
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        stmt2.close();
        con.close();
    }

    // READ

    public ArrayList<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT t.transactId transactIdA, borrowingDate, t.rid ridA, r.name rname, borrowStaffId, s.name sname, dueDate, COUNT(dt.bid) quantity " +
                "FROM [transaction] t, [transactionDetail] dt, [reader] r, [staff] s " +
                "WHERE t.transactId = dt.transactId AND " +
                "t.rid = r.rid AND s.sid = t.borrowStaffId " +
                "GROUP BY dt.transactId";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        ArrayList<Transaction> transactions = new ArrayList<>();
        Transaction transaction;

        while (rs.next()) {
            transaction = new Transaction(
                    rs.getInt("transactIdA"),
                    rs.getTimestamp("borrowingDate"),
                    new Reader(rs.getInt("ridA"), rs.getNString("rname")),
                    new Staff(rs.getInt("sidA"), rs.getNString("sname")),
                    rs.getTimestamp("dueDate"),
                    rs.getInt("quantity")
            );
            transactions.add(transaction);
        }

        rs.close();
        stmt.close();
        con.close();

        return transactions;
    }

    public Transaction getTransaction(int transactionId) throws SQLException {

        String sql =
                "SELECT      t.transactId transactIdA, borrowingDate, t.rid ridA, r.name rname, borrowStaffId, s1.sid sId1, s1.name sname, dueDate, " +
                        "b.bid bidA, bookName, returnStaffId, s2.sid sId2, s2.name s2name, returnDate, deposit, fine, isExtended " +
                        "FROM       [transaction] t, [transactionDetail] dt, [reader] r, [staff] s1, [staff] s2, [book] b " +
                        "WHERE       t.transactId = ? AND t.transactId = dt.transactId AND t.rid = r.rid AND s1.sid = t.borrowStaffId AND " +
                        "s2.sid = dt.returnStaffId AND b.bid = dt.bid";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        Transaction transaction;

        rs.next();
        transaction = new Transaction(
                rs.getInt("transactIdA"),
                rs.getTimestamp("borrowingDate"),
                new Reader(rs.getInt("ridA"), rs.getNString("rname")),
                new Staff(rs.getInt("sId1"), rs.getNString("sname1")),
                rs.getTimestamp("dueDate"),
                rs.getInt("quantity")
        );
        do {
            //Book book, Staff returnStaff, Timestamp returnDate, int deposit, boolean isExtended, int fine)
            transaction.addDetail(
                    new Book(rs.getString("bidA"), rs.getNString("bookName")),
                    new Staff(rs.getInt("sId2"), rs.getNString("sname2")),
                    rs.getTimestamp("returnDate"),
                    rs.getLong("deposit"),
                    rs.getBoolean("isExtended"),
                    rs.getLong("fine"));

        } while (rs.next());

        rs.close();
        stmt.close();
        con.close();

        return transaction;
    }

    // UPDATE
    public boolean updateDetail(int transactId, Transaction.TransactionDetail detail) throws SQLException {
        String sql = "UPDATE [transactionDetail]" +
                "SET     returnStaffId=?," +
                "returnDate=?," +
                "deposit=?," +
                "fine=?," +
                "isExtended=? " +
                "WHERE   bid=? AND transactId=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setInt(1, detail.getReturnStaff().getSid());
        stmt.setTimestamp(2, detail.getReturnDate());
        stmt.setLong(3, detail.getDeposit());
        stmt.setLong(4, detail.getFine());
        stmt.setBoolean(5, detail.isExtended());
        stmt.setString(6, detail.getBook().getBid());
        stmt.setInt(7, transactId);

        boolean result = (stmt.executeUpdate() > 0);

        stmt.close();
        con.close();

        return result;
    }

    public boolean update(Transaction transaction) throws SQLException {
        String sql = "UPDATE [transaction] SET " +
                "rid = ?," +
                "dueDate = ? " +
                "WHERE transactId = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setInt(1, transaction.getBorrower().getRid());
        stmt.setTimestamp(2, transaction.getDueDate());
        stmt.setInt(3, transaction.getTransactId());

        boolean result = (stmt.executeUpdate() > 0);

        stmt.close();
        con.close();

        return result;
    }

    // DELETE

    public boolean deleteDetail(int transactId, Transaction.TransactionDetail transactionDetail) throws SQLException {
        String sql = "DELETE FROM transactionDetail WHERE transactId = ? AND bid = ?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setInt(1, transactId);
        stmt.setString(2, transactionDetail.getBook().getBid());

        boolean result = (stmt.executeUpdate() > 0);

        stmt.close();
        con.close();

        return result;
    }

}
