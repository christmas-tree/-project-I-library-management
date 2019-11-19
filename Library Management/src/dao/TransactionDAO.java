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

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class TransactionDAO {

    private static TransactionDAO instance = new TransactionDAO();

    private TransactionDAO() {
    }

    public static TransactionDAO getInstance() {
        return instance;
    }

    // CREATE
    public boolean createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO [transaction](borrowingDate, rid, borrowStaffId, dueDate) OUTPUT inserted.transactId VALUES (?, ?, ?, ?)";
        String sql2 = "INSERT INTO [transactionDetail](transactId, bid, deposit, isExtended) " +
                "VALUES (?, ?, ?, ?)";

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

        transaction.getAllDetails().forEach((transactionDetail) -> {
            try {
                stmt2.setInt(1, transaction.getTransactId());
                stmt2.setString(2, transactionDetail.getBook().getBid());
                stmt2.setLong(3, transactionDetail.getDeposit());
                stmt2.setBoolean(4, transactionDetail.isExtended());

                stmt2.executeUpdate();
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        stmt2.close();
        con.close();
        return true;
    }

    // READ

    public ArrayList<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT dt.transactId transactIdA, borrowingDate, t.rid ridA, r.name rname, borrowStaffId sidA, s.name sname, dueDate, COUNT(dt.bid) quantity " +
                "FROM [transaction] t, [transactionDetail] dt, [reader] r, [staff] s " +
                "WHERE t.transactId = dt.transactId AND " +
                "t.rid = r.rid AND s.sid = t.borrowStaffId " +
                "GROUP BY dt.transactId, borrowingDate, t.rid, r.name, borrowStaffId, s.name, dueDate";

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

        String sql = "SELECT t.transactId transactIdA, " +
                "       borrowingDate, " +
                "       t.rid        ridA, " +
                "       r.name       rname, " +
                "       s1.sid       sId1, " +
                "       s1.name      s1name, " +
                "       dueDate, " +
                "       b.bid        bidA, " +
                "       bookName, " +
                "       s2.sid       sId2, " +
                "       s2.name      s2name, " +
                "       returnDate, " +
                "       deposit, " +
                "       fine, " +
                "       isExtended " +
                "FROM [transaction] t " +
                "         JOIN [transactionDetail] dt ON t.transactId = dt.transactId " +
                "         JOIN [book] b ON b.bid = dt.bid " +
                "         JOIN [reader] r ON t.rid = r.rid " +
                "         JOIN [staff] s1 ON s1.sid = t.borrowStaffId " +
                "         LEFT JOIN [staff] s2 ON s2.sid = dt.returnStaffId " +
                "WHERE t.transactId = ?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, transactionId);
        ResultSet rs = stmt.executeQuery();

        rs.next();
        Transaction transaction = new Transaction(
                rs.getInt("transactIdA"),
                rs.getTimestamp("borrowingDate"),
                new Reader(rs.getInt("ridA"), rs.getNString("rname")),
                new Staff(rs.getInt("sId1"), rs.getNString("s1name")),
                rs.getTimestamp("dueDate")
        );
        do {
            transaction.addDetail(
                    new Book(rs.getString("bidA"), rs.getNString("bookName")),
                    new Staff(rs.getInt("sId2"), rs.getNString("s2name")),
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

    public List<Transaction> searchTransaction(int searchMethod, String value)
            throws SQLException, NumberFormatException {

        String sql =    "SELECT " +
                                "dt.transactId transactIdA, borrowingDate, t.rid ridA, r.name rname, borrowStaffId sidA, s.name sname, dueDate, COUNT(dt.bid) quantity " +
                        "FROM  " +
                                "[transaction] t, [transactionDetail] dt, [reader] r, [staff] s " +
                        "WHERE " +
                                "t.transactId = dt.transactId AND t.rid = r.rid AND s.sid = t.borrowStaffId " +
                                "%s " +
                        "GROUP BY " +
                                "dt.transactId, borrowingDate, t.rid, r.name, borrowStaffId, s.name, dueDate";

        Connection con;
        PreparedStatement stmt;
        ResultSet rs;

//            String searchChoices[] = {"Mã nhân viên mượn", "Tên nhân viên mượn", "Mã độc giả", "Tên độc giả", "Thời gian mượn", "Hạn trả", "Số lượng sách mượn"};
        //                                      0                       1                   2            3                  4            5              6

        switch (searchMethod) {

            case 0: // Ma nhan vien muon
                sql = String.format(sql, "AND s.sid = ?");
                con = DbConnection.getConnection();
                System.out.println(sql);
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(value));
                rs = stmt.executeQuery();
                break;

            case 1: // Ten NV muon
                sql = String.format(sql, "AND s.name LIKE ?");
                con = DbConnection.getConnection();
                System.out.println(sql);
                stmt = con.prepareStatement(sql);
                stmt.setNString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            case 2:
                sql = String.format(sql, "AND r.rid = ?");
                con = DbConnection.getConnection();
                System.out.println(sql);
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(value));
                rs = stmt.executeQuery();
                break;
            case 3:
                sql = String.format(sql, "AND r.name LIKE ?");
                con = DbConnection.getConnection();
                System.out.println(sql);
                stmt = con.prepareStatement(sql);
                stmt.setNString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            default:
                throw new IllegalArgumentException("Illegal Searching Method.");
        }

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

    public List<Transaction> searchTransactionByBookCount(int value1, int value2)
            throws SQLException {

        String sql =    "SELECT " +
                                "dt.transactId transactIdA, borrowingDate, t.rid ridA, r.name rname, borrowStaffId sidA, s.name sname, dueDate, COUNT(dt.bid) quantity " +
                        "FROM  " +
                                "[transaction] t, [transactionDetail] dt, [reader] r, [staff] s " +
                        "WHERE " +
                                "t.transactId = dt.transactId AND t.rid = r.rid AND s.sid = t.borrowStaffId " +
                        "GROUP BY " +
                                "dt.transactId, borrowingDate, t.rid, r.name, borrowStaffId, s.name, dueDate " +
                        "HAVING COUNT(dt.bid) BETWEEN ? AND ?";

        Connection con;
        PreparedStatement stmt;
        ResultSet rs;

//            String searchChoices[] = {"Mã nhân viên mượn", "Tên nhân viên mượn", "Mã độc giả", "Tên độc giả", "Thời gian mượn", "Hạn trả", "Số lượng sách mượn"};
        //                                      0                       1                   2            3                  4            5              6

        con = DbConnection.getConnection();
        stmt = con.prepareStatement(sql);
        stmt.setInt(1, value1);
        stmt.setInt(2, value2);
        rs = stmt.executeQuery();

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

    public List<Transaction> searchTransaction(int searchMethod, Timestamp startDate, Timestamp endDate)
            throws SQLException {

        final String sql1 =    "SELECT " +
                                "dt.transactId transactIdA, borrowingDate, t.rid ridA, r.name rname, borrowStaffId sidA, s.name sname, dueDate, COUNT(dt.bid) quantity " +
                        "FROM  " +
                                "[transaction] t, [transactionDetail] dt, [reader] r, [staff] s " +
                        "WHERE " +
                                "t.transactId = dt.transactId AND t.rid = r.rid AND s.sid = t.borrowStaffId AND " +
                                "borrowingDate BETWEEN ? AND ? " +
                        "GROUP BY " +
                                "dt.transactId, borrowingDate, t.rid, r.name, borrowStaffId, s.name, dueDate";

        final String sql2 =    "SELECT " +
                "dt.transactId transactIdA, borrowingDate, t.rid ridA, r.name rname, borrowStaffId sidA, s.name sname, dueDate, COUNT(dt.bid) quantity " +
                "FROM  " +
                "[transaction] t, [transactionDetail] dt, [reader] r, [staff] s " +
                "WHERE " +
                "t.transactId = dt.transactId AND t.rid = r.rid AND s.sid = t.borrowStaffId AND " +
                "dueDate BETWEEN ? AND ? " +
                "GROUP BY " +
                "dt.transactId, borrowingDate, t.rid, r.name, borrowStaffId, s.name, dueDate";

        Connection con;
        PreparedStatement stmt;
        ResultSet rs;

        switch (searchMethod) {
            case 4:
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql1);
                stmt.setTimestamp(1, startDate);
                stmt.setTimestamp(2, endDate);
                break;
            case 5:
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql2);
                stmt.setTimestamp(1, startDate);
                stmt.setTimestamp(2, endDate);
                break;
            default:
                throw new IllegalArgumentException("Illegal Searching Method.");
        }


        ArrayList<Transaction> transactions = new ArrayList<>();
        Transaction transaction;

        rs = stmt.executeQuery();

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

    // UPDATE

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

        String sql2 = "IF NOT EXISTS (SELECT * FROM [transactionDetail] WHERE bid=? AND transactId=?) " +
                "    INSERT INTO [transactionDetail](transactId, bid, deposit, isExtended) " +
                "                VALUES (?, ?, ?, ?) " +
                "ELSE " +
                "UPDATE [transactionDetail]" +
                "SET     returnStaffId=?," +
                "returnDate=?," +
                "deposit=?," +
                "fine=?," +
                "isExtended=? " +
                "WHERE   bid=? AND transactId=? ";

        System.out.println(sql2);

        Connection con2 = DbConnection.getConnection();
        PreparedStatement stmt2 = con2.prepareStatement(sql2);

        transaction.getAllDetails().forEach(detail -> {
            try {
                stmt2.setString(1, detail.getBook().getBid());
                stmt2.setInt(2, transaction.getTransactId());
                stmt2.setInt(3, transaction.getTransactId());
                stmt2.setString(4, detail.getBook().getBid());
                stmt2.setLong(5, detail.getDeposit());
                stmt2.setBoolean(6, detail.isExtended());

                if (detail.getReturnStaff().getName() == null) {
                    stmt2.setNull(7, java.sql.Types.INTEGER);
                } else {
                    System.out.println("Not null");
                    stmt2.setInt(7, detail.getReturnStaff().getSid());
                }

                stmt2.setTimestamp(8, detail.getReturnDate());
                stmt2.setLong(9, detail.getDeposit());
                stmt2.setLong(10, detail.getFine());
                stmt2.setBoolean(11, detail.isExtended());
                stmt2.setString(12, detail.getBook().getBid());
                stmt2.setInt(13, transaction.getTransactId());

                stmt2.executeUpdate();
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        stmt2.close();
        con2.close();

        return result;
    }

    // DELETE

    public boolean deleteDetails(int transactId, ArrayList<Transaction.TransactionDetail> details) throws SQLException {
        String sql = "DELETE FROM transactionDetail WHERE transactId = ? AND bid = ?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        details.forEach(detail -> {
            try {
                stmt.setInt(1, transactId);
                stmt.setString(2, detail.getBook().getBid());
                stmt.executeUpdate();
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        stmt.close();
        con.close();

        return true;
    }

}
