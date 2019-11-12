/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

public class TransactionDetailDAO {
    private static TransactionDetailDAO instance = new TransactionDetailDAO();

    private TransactionDetailDAO() {
    }

    public static TransactionDetailDAO getInstance() {
        return instance;
    }
}
