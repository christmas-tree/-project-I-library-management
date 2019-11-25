/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Book;
import model.Category;
import model.Language;
import model.Publisher;
import util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookDAO {

    private static BookDAO instance = new BookDAO();

    private BookDAO() {
    }

    public static BookDAO getInstance() {
        return instance;
    }

    //CREATE

    public boolean createBook(Book book) throws SQLException {
        String sql = "INSERT INTO [book](bid, created, bookName, price, catId, author, pubId, pubYear, langId, location, quantity, availQuantity)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, book.getBid());
        stmt.setTimestamp(2, book.getCreated());
        stmt.setNString(3, book.getBookName());
        stmt.setLong(4, book.getPrice());
        stmt.setString(5, book.getCategory().getCatId());
        stmt.setString(6, book.getAuthor());
        stmt.setString(7, book.getPublisher().getPubId());
        stmt.setInt(8, book.getPubYear());
        stmt.setString(9, book.getLanguage().getLangId());
        stmt.setNString(10, book.getLocation());
        stmt.setInt(11, book.getQuantity());
        stmt.setInt(12, book.getAvailQuantity());

        boolean result = (stmt.executeUpdate() > 0);

        stmt.close();
        con.close();

        return result;
    }

    // READ

    public int getCountByCategory(String catId) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM book WHERE catId=?");
        stmt.setString(1, catId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int result = rs.getInt(1);

        stmt.close();
        rs.close();
        con.close();

        return result;
    }

    public Book getBook(String bid) throws SQLException {
        Book book = null;

        String sql =
                "SELECT " +
                        "b.bid," +
                        "b.created," +
                        "b.bookName," +
                        "b.price," +
                        "b.catId," +
                        "c.category," +
                        "b.author," +
                        "b.pubId," +
                        "p.publisher," +
                        "b.pubYear," +
                        "b.langId," +
                        "l.language," +
                        "b.location," +
                        "b.quantity," +
                        "b.availQuantity " +
                        "FROM book b " +
                        "LEFT JOIN category c on b.catId = c.catId " +
                        "LEFT JOIN publisher p on b.pubId = p.pubId " +
                        "LEFT JOIN language l on b.langId = l.langId " +
                        "WHERE b.bid=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, bid);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            book = new Book(
                    rs.getString("bid"),
                    rs.getTimestamp("created"),
                    rs.getNString("bookName"),
                    rs.getLong("price"),
                    new Category(rs.getString("catId"), rs.getNString("catName")),
                    rs.getNString("author"),
                    new Publisher(rs.getString("pubId"), rs.getNString("pubName")),
                    rs.getInt("pubYear"),
                    new Language(rs.getString("langId"), rs.getNString("language")),
                    rs.getNString("location"),
                    rs.getInt("quantity"),
                    rs.getInt("availQuantity"));
        }
        rs.close();
        con.close();

        return book;
    }

    public ObservableList<Book> getAllBooks() throws SQLException {
        Book book = null;
        ObservableList<Book> bookList = FXCollections.observableArrayList();

        String sql =
                "SELECT " +
                        "b.bid," +
                        "b.bookName," +
                        "b.price," +
                        "b.availQuantity " +
                        "FROM book b";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            book = new Book(
                    rs.getString("bid"),
                    rs.getNString("bookName"),
                    rs.getLong("price"),
                    rs.getInt("availQuantity"));
            bookList.add(book);
        }
        rs.close();
        stmt.close();
        con.close();

        return bookList;
    }


    public List<Book> getAllBooks(Map<String, Category> catList, Map<String, Publisher> pubList, Map<String, Language> langList) throws SQLException {
        Book book = null;
        List<Book> bookList = new ArrayList<>();

        String sql =
                "SELECT " +
                        "b.bid," +
                        "b.created," +
                        "b.bookName," +
                        "b.price," +
                        "b.catId," +
                        "b.author," +
                        "b.pubId," +
                        "b.pubYear," +
                        "b.langId," +
                        "b.location," +
                        "b.quantity," +
                        "b.availQuantity " +
                        "FROM book b";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            book = new Book(
                    rs.getString("bid"),
                    rs.getTimestamp("created"),
                    rs.getNString("bookName"),
                    rs.getLong("price"),
                    catList.get(rs.getString("catId")),
                    rs.getNString("author"),
                    pubList.get(rs.getString("pubId")),
                    rs.getInt("pubYear"),
                    langList.get(rs.getString("langId")),
                    rs.getNString("location"),
                    rs.getInt("quantity"),
                    rs.getInt("availQuantity"));
            bookList.add(book);
        }
        rs.close();
        stmt.close();
        con.close();

        return bookList;
    }

    public List<Book> searchBook(int searchMethod, String value, Map<String, Category> catList, Map<String, Publisher> pubList, Map<String, Language> langList)
            throws SQLException, NumberFormatException {

        List<Book> bookSearchResults = new ArrayList<>();
        Book book;

        String sql = "SELECT * FROM book";
        Connection con;
        PreparedStatement stmt;
        ResultSet rs;

        //String searchChoices[] = {"Mã sách", "Tên sách", "Thời gian thêm", "Giá", "Thể loại", "Tác giả", "Nhà xuất bản", "Năm xuất bản", "Ngôn ngữ"};
        //                             0            1                                     4           5           6                              8

        switch (searchMethod) {

            case 0: // Ma sach
                sql += " WHERE bid LIKE ?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            case 1: // Ten sach
                sql += " WHERE [bookName] LIKE ?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setNString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            case 4: // The loai
                sql += " WHERE [catId]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setString(1, value);
                rs = stmt.executeQuery();
                break;

            case 5: // Tac gia
                sql += " WHERE [author] LIKE ?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setNString(1, "%" + value + "%");
                rs = stmt.executeQuery();
                break;

            case 6: // Nha xuat ban
                sql += " WHERE [pubId]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setString(1, value);
                rs = stmt.executeQuery();
                break;

            case 8: // Ngon ngu
                sql += " WHERE [langId]=?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setString(1, value);
                rs = stmt.executeQuery();
                break;

            default:
                throw new IllegalArgumentException("Illegal Searching Method.");
        }

        while (rs.next()) {
            book = new Book(
                    rs.getString("bid"),
                    rs.getTimestamp("created"),
                    rs.getNString("bookName"),
                    rs.getLong("price"),
                    catList.get(rs.getString("catId")),
                    rs.getNString("author"),
                    pubList.get(rs.getString("pubId")),
                    rs.getInt("pubYear"),
                    langList.get(rs.getString("langId")),
                    rs.getNString("location"),
                    rs.getInt("quantity"),
                    rs.getInt("availQuantity"));
            bookSearchResults.add(book);
        }
        rs.close();
        stmt.close();
        con.close();

        return bookSearchResults;
    }

    public List<Book> searchBook(int searchMethod, int value1, int value2, Map<String, Category> catList, Map<String, Publisher> pubList, Map<String, Language> langList)
            throws SQLException {

        List<Book> bookSearchResults = new ArrayList<>();
        Book book;

        String sql = "SELECT * FROM book";
        Connection con;
        PreparedStatement stmt;
        ResultSet rs;

        //String searchChoices[] = {"Mã sách", "Tên sách", "Thời gian thêm", "Giá", "Thể loại", "Tác giả", "Nhà xuất bản", "Năm xuất bản", "Ngôn ngữ"};
        //                                                                     3                                                7

        switch (searchMethod) {

            case 3: // Gia
                sql += " WHERE [price] BETWEEN ? AND ?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, value1);
                stmt.setInt(2, value2);
                rs = stmt.executeQuery();
                break;

            case 7: // Nam xuat ban
                sql += " WHERE [pubYear] BETWEEN ? AND ?";
                con = DbConnection.getConnection();
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, value1);
                stmt.setInt(2, value2);
                rs = stmt.executeQuery();
                break;

            default:
                throw new IllegalArgumentException("Illegal Searching Method.");
        }

        while (rs.next()) {
            book = new Book(
                    rs.getString("bid"),
                    rs.getTimestamp("created"),
                    rs.getNString("bookName"),
                    rs.getLong("price"),
                    catList.get(rs.getString("catId")),
                    rs.getNString("author"),
                    pubList.get(rs.getString("pubId")),
                    rs.getInt("pubYear"),
                    langList.get(rs.getString("langId")),
                    rs.getNString("location"),
                    rs.getInt("quantity"),
                    rs.getInt("availQuantity"));
            bookSearchResults.add(book);
        }
        rs.close();
        stmt.close();
        con.close();

        return bookSearchResults;
    }

    public List<Book> searchBookByCreatedTime(Timestamp startDate, Timestamp endDate, Map<String, Category> catList, Map<String, Publisher> pubList, Map<String, Language> langList)
            throws SQLException {

        List<Book> bookSearchResults = new ArrayList<>();
        Book book;

        String sql = "SELECT * FROM [book] WHERE [created] BETWEEN ? AND ?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setTimestamp(1, startDate);
        stmt.setTimestamp(2, endDate);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            book = new Book(
                    rs.getString("bid"),
                    rs.getTimestamp("created"),
                    rs.getNString("bookName"),
                    rs.getLong("price"),
                    catList.get(rs.getString("catId")),
                    rs.getNString("author"),
                    pubList.get(rs.getString("pubId")),
                    rs.getInt("pubYear"),
                    langList.get(rs.getString("langId")),
                    rs.getNString("location"),
                    rs.getInt("quantity"),
                    rs.getInt("availQuantity"));
            bookSearchResults.add(book);
        }
        rs.close();
        stmt.close();
        con.close();

        return bookSearchResults;
    }

    // UPDATE
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE    [book]" +
                "SET " +
                "bookName=?," +
                "price=?," +
                "catId=?," +
                "author=?," +
                "pubId=?," +
                "pubYear=?," +
                "langId=?," +
                "location=?," +
                "quantity=?," +
                "availQuantity=? " +
                "WHERE bid=?";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setNString(1, book.getBookName());
        stmt.setLong(2, book.getPrice());
        stmt.setString(3, book.getCategory().getCatId());
        stmt.setString(4, book.getAuthor());
        stmt.setString(5, book.getPublisher().getPubId());
        stmt.setInt(6, book.getPubYear());
        stmt.setString(7, book.getLanguage().getLangId());
        stmt.setNString(8, book.getLocation());
        stmt.setInt(9, book.getQuantity());
        stmt.setInt(10, book.getAvailQuantity());
        stmt.setString(11, book.getBid());

        boolean result = (stmt.executeUpdate() > 0);

        stmt.close();
        con.close();

        return result;
    }

    // DELETE

    public boolean deleteBook(Book book) throws SQLException {
        String sql = "DELETE FROM [book] WHERE bid = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, book.getBid());

        boolean result = (stmt.executeUpdate() > 0);

        stmt.close();
        con.close();

        return result;
    }

    public boolean updateBookAvailQuantity(Book book) throws SQLException {
        String sql = "UPDATE [book] SET availQuantity=? WHERE bid=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setInt(1, book.getAvailQuantity());
        stmt.setString(2, book.getBid());

        boolean result = (stmt.executeUpdate() > 0);

        stmt.close();
        con.close();

        return result;
    }
}
