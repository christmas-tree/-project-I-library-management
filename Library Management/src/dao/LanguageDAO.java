/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package dao;

import model.Language;
import util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LanguageDAO {

    public LanguageDAO() {
    }

    public boolean createLanguage(Language language) throws SQLException {
        String sql = "INSERT INTO [language](langId, language) VALUES (?, ?)";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, language.getLangId());
        stmt.setNString(2, language.getLanguage());
        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }
    // READ

    public Language getLanguage(String langId) throws SQLException {
        String sql = "SELECT * FROM [language] WHERE langId=?";
        Language language = null;

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, langId);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            language = new Language(rs.getString("langId"), rs.getNString("langName"));
        }

        rs.close();
        con.close();

        return language;
    }

    public Map<String, Language> getAllLanguages() throws SQLException {
        String sql = "SELECT * FROM [language]";
        Language language = null;
        Map<String, Language> langList = new HashMap<>();

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            language = new Language(rs.getString("langId"), rs.getNString("langName"));
            langList.put(language.getLangId(), language);
        }

        rs.close();
        con.close();

        return langList;
    }

    // UPDATE
    public boolean updateLanguage(Language language) throws SQLException {
        String sql = "UPDATE [language] SET language=? WHERE langId=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setNString(1, language.getLanguage());
        stmt.setString(2, language.getLangId());

        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }

    // DELETE
    public boolean deleteLanguage(Language language) throws SQLException {
        String sql = "DELETE FROM [language] WHERE langId=?";

        Connection con = DbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, language.getLangId());
        boolean result = (stmt.executeUpdate() > 0);
        con.close();

        return result;
    }
}
