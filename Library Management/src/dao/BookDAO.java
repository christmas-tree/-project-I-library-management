package dao;

import model.Book;
import model.Category;
import model.Language;
import model.Publisher;
import util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class BookDAO {
    //CREATE

    public boolean createBook(Book book) throws SQLException {
        String sql = "INSERT INTO [book](bid, bookName, price, catId, author, pubId, pubYear, langId, location, quantity, availQuantity)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, book.getBid());
        stmt.setNString(2, book.getBookName());
        stmt.setInt(3, book.getPrice());
        stmt.setString(4, book.getCategory().getCatId());
        stmt.setString(5, book.getAuthor());
        stmt.setString(6, book.getPublisher().getPubId());
        stmt.setInt(7, book.getPubYear());
        stmt.setString(8, book.getLanguage().getLangId());
        stmt.setNString(9, book.getLocation());
        stmt.setInt(10, book.getQuantity());
        stmt.setInt(11, book.getAvailQuantity());

        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }

    // READ

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
                    rs.getNString("bookName"),
                    rs.getInt("price"),
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

    public List<Book> getAllBooks() throws SQLException {
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

        CategoryDAO categoryDAO = new CategoryDAO();
        Map<String, Category> catList = categoryDAO.getAllCategories();

        PublisherDAO publisherDAO = new PublisherDAO();
        Map<String, Publisher> pubList = publisherDAO.getAllPublishers();

        LanguageDAO languageDAO = new LanguageDAO();
        Map<String, Language> langList = languageDAO.getAllLanguages();

        while (rs.next()) {
            book = new Book(
                    rs.getString("bid"),
                    rs.getNString("bookName"),
                    rs.getInt("price"),
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
        con.close();

        return bookList;
    }

    // UPDATE
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE    [book]" +
                "SET       bid=?," +
                "bookName=?," +
                "price=?," +
                "catId=?," +
                "author=?," +
                "pubId=?," +
                "pubYear=?," +
                "langId=?," +
                "location=?," +
                "quantity=?," +
                "availQuantity)=? " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, book.getBid());
        stmt.setNString(2, book.getBookName());
        stmt.setInt(3, book.getPrice());
        stmt.setString(4, book.getCategory().getCatId());
        stmt.setString(5, book.getAuthor());
        stmt.setString(6, book.getPublisher().getPubId());
        stmt.setInt(7, book.getPubYear());
        stmt.setString(8, book.getLanguage().getLangId());
        stmt.setNString(9, book.getLocation());
        stmt.setInt(10, book.getQuantity());
        stmt.setInt(11, book.getAvailQuantity());

        boolean result = (stmt.executeUpdate() > 0);
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
        con.close();

        return result;
    }
}
