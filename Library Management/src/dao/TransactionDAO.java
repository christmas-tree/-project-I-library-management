/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import model.Transaction;
import util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class TransactionDAO {

    public TransactionDAO() {
    }

    // CREATE

    public void createTransaction (Transaction transaction) throws SQLException {
        String sql = "INSERT INTO [transaction](borrowingDate, rid, borrowStaffId) OUTPUT transactId VALUES (?, ?, ?)";
        String sql2 = "INSERT INTO [transactionDetail](transactId, bid, dueDate, deposit) " +
                "VALUES (?, ?, ?, ?)";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setTimestamp(1, transaction.getBorrowingDate());
        stmt.setInt(2, transaction.getBorrower().getRid());
        stmt.setInt(3, transaction.getBorrowStaff().getSid());

        ResultSet rs = stmt.executeQuery();

        rs.next();
        transaction.setTransactId(rs.getInt(1));
        rs.close();
        stmt.close();

        PreparedStatement stmt2 = con.prepareStatement(sql2);

        Iterator<Transaction.TransactionDetail> details = transaction.getAllDetails().iterator();

        while (details.hasNext()) {
            Transaction.TransactionDetail transactionDetail = details.next();
            stmt2.setInt(1, transaction.getTransactId());
            stmt2.setString(2, transactionDetail.getBook().getBid());
            stmt2.setTimestamp(3, transactionDetail.getDueDate());
            stmt2.setInt(4, transactionDetail.getDeposit());

            stmt2.executeUpdate();
        }

        stmt2.close();
        con.close();
    }

    // READ

    // UPDATE
    public void updateDetail(int transactId, Transaction.TransactionDetail detail) throws SQLException {
        String sql = "UPDATE [transactionDetail]" +
                        "SET     returnStaffId=?," +
                                "dueDate=?," +
                                "returnDate=?," +
                                "deposit=?," +
                                "fine=?," +
                                "isExtended=? " +
                        "WHERE   bid=? AND transactId=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setInt(1, detail.getReturnStaff().getSid());
        stmt.setTimestamp(2, detail.getDueDate());
        stmt.setTimestamp(3, detail.getReturnDate());
        stmt.setInt(4, detail.getDeposit());
        stmt.setInt(5, detail.getFine());
        stmt.setBoolean(6,detail.isExtended());
        stmt.setString(7, detail.getBook().getBid());
        stmt.setInt(8, transactId);

        stmt.executeUpdate();

        stmt.close();
        con.close();
    }
}
