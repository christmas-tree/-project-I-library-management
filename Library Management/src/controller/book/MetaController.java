/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.book;

import controller.basic.IndexController;
import dao.CategoryDAO;
import dao.LanguageDAO;
import dao.PublisherDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import model.Category;
import model.Language;
import model.Publisher;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.ExHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

public class MetaController {
    @FXML
    private TextField pubIdField;

    @FXML
    private TextField pubNameField;

    @FXML
    private TextField catNameField;

    @FXML
    private Button addCatBtn;

    @FXML
    private Button addPubBtn;

    @FXML
    private TextField languageField;

    @FXML
    private TableView<Publisher> pubTable;

    @FXML
    private TextField langIdField;

    @FXML
    private TableView<Category> catTable;

    @FXML
    private TextField catIdField;

    @FXML
    private TableView<Language> langTable;

    @FXML
    private Button addLangBtn;

    @FXML
    private TableColumn<Category, String> catIdCol;

    @FXML
    private TableColumn<Category, String> catNameCol;

    @FXML
    private TableColumn<Publisher, String> pubIdCol;

    @FXML
    private TableColumn<Publisher, String> pubNameCol;

    @FXML
    private TableColumn<Language, String> langIdCol;

    @FXML
    private TableColumn<Language, String> languageCol;

    ObservableList<Category> categories;
    ObservableList<Publisher> publishers;
    ObservableList<Language> languages;

    public void init(IndexController c) {
        try {
            categories = CategoryDAO.getInstance().getCategoryList();
            publishers = PublisherDAO.getInstance().getPublisherList();
            languages = LanguageDAO.getInstance().getLanguageList();
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        // CATEGORIES

        catIdCol.setCellValueFactory(new PropertyValueFactory<>("catId"));
        catNameCol.setCellValueFactory(new PropertyValueFactory<>("catName"));
        catNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        catNameCol.setOnEditCommit((event) -> {
            TablePosition<Category, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            int row = pos.getRow();
            Category category = event.getTableView().getItems().get(row);
            try {
                category.setCatName(newName);
                CategoryDAO.getInstance().updateCategory(category);
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        catTable.setItems(categories);

        addCatBtn.setOnAction(event -> {
            if (catIdField.getText().isBlank() || catNameField.getText().isBlank()) {
                ExHandler.handle(new Exception("Mã thể loại hoặc Tên thể loại đang để trống."));
            } else try {
                Category newCat = new Category(catIdField.getText(), catNameField.getText());
                categories.add(newCat);
                CategoryDAO.getInstance().createCategory(newCat);
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        catIdField.textProperty().addListener((observableValue, s, t1) -> {
            t1 = t1.toUpperCase();
            if (t1.length() > 3) {
                t1 = t1.substring(0, 3);
            }
            catIdField.setText(t1);
        });

        catNameField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > 30) {
                catNameField.setText(t1.substring(0, 30));
            }
        });

        catNameField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER))
                addCatBtn.fire();
        });

        // PUBLISHERS
        pubIdCol.setCellValueFactory(new PropertyValueFactory<>("pubId"));
        pubNameCol.setCellValueFactory(new PropertyValueFactory<>("pubName"));
        pubNameCol.setCellFactory(TextFieldTableCell.forTableColumn());


        pubNameCol.setOnEditCommit((event) -> {
            TablePosition<Publisher, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            int row = pos.getRow();
            Publisher pubegory = event.getTableView().getItems().get(row);
            try {
                pubegory.setPubName(newName);
                PublisherDAO.getInstance().updatePublisher(pubegory);
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        pubTable.setItems(publishers);

        addPubBtn.setOnAction(event -> {
            if (pubIdField.getText().isBlank() || pubNameField.getText().isBlank()) {
                ExHandler.handle(new Exception("Mã NXB hoặc Tên NXB đang để trống."));
            } else try {
                Publisher newPub = new Publisher(pubIdField.getText(), pubNameField.getText());
                publishers.add(newPub);
                PublisherDAO.getInstance().createPublisher(newPub);
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        pubIdField.textProperty().addListener((observableValue, s, t1) -> {
            t1 = t1.toUpperCase();
            if (t1.length() > 3) {
                t1 = t1.substring(0, 3);
            }
            pubIdField.setText(t1);
        });

        pubNameField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > 50) {
                pubNameField.setText(t1.substring(0, 50));
            }
        });

        pubNameField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER))
                addPubBtn.fire();
        });

        // LANGUAGES
        langIdCol.setCellValueFactory(new PropertyValueFactory<>("langId"));
        languageCol.setCellValueFactory(new PropertyValueFactory<>("language"));
        languageCol.setCellFactory(TextFieldTableCell.forTableColumn());

        langTable.setItems(languages);

        languageCol.setOnEditCommit((event) -> {
            TablePosition<Language, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            int row = pos.getRow();
            Language language = event.getTableView().getItems().get(row);
            try {
                language.setLanguage(newName);
                LanguageDAO.getInstance().updateLanguage(language);
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        addLangBtn.setOnAction(event -> {
            if (langIdField.getText().isBlank() || languageField.getText().isBlank()) {
                ExHandler.handle(new Exception("Mã NN hoặc Ngôn ngữ đang để trống."));
            } else try {
                Language newLang = new Language(langIdField.getText(), languageField.getText());
                languages.add(newLang);
                LanguageDAO.getInstance().createLanguage(newLang);
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        langIdField.textProperty().addListener((observableValue, s, t1) -> {
            t1 = t1.toUpperCase();
            if (t1.length() > 3) {
                t1 = t1.substring(0, 3);
            }
            langIdField.setText(t1);
        });

        languageField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length() > 30) {
                languageField.setText(t1.substring(0, 30));
            }
        });

        languageField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER))
                addLangBtn.fire();
        });

        c.exportMenu.setDisable(true);
        c.addMenu.setDisable(true);
        c.editMenu.setDisable(true);
        c.deleteMenu.setDisable(true);
        c.importMenu.setDisable(false);

        c.importMenu.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Nhập thông tin phụ");
            alert.setContentText("Chọn loại thông tin nhập:");

            ButtonType catImportBtn = new ButtonType("Thể loại");
            ButtonType pubImportBtn = new ButtonType("Nhà xuất bản");
            ButtonType langImportBtn = new ButtonType("Ngôn ngữ");
            ButtonType cancelBtn = new ButtonType("Huỷ", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(catImportBtn, pubImportBtn, langImportBtn, cancelBtn);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == catImportBtn){
                XSSFSheet sheet = getSheet();

                ArrayList<Category> newCategories = new ArrayList<>();
                // DATA
                Iterator rows = sheet.rowIterator();
                XSSFRow row = (XSSFRow) rows.next();
                if (row.getLastCellNum() >= 2) {
                    while (rows.hasNext()) {
                        row = (XSSFRow) rows.next();
                        Category newItem = new Category(
                                row.getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue(),
                                row.getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue()
                        );
                        newCategories.add(newItem);
                        categories.add(newItem);
                    }
                } else
                    ExHandler.handle(new Exception("File không đúng định dạng." + row.getLastCellNum()));
                CategoryDAO.getInstance().importCategory(newCategories);

            } else if (result.get() == pubImportBtn) {
                XSSFSheet sheet = getSheet();

                ArrayList<Publisher> newPublishers = new ArrayList<>();
                // DATA
                Iterator rows = sheet.rowIterator();
                XSSFRow row = (XSSFRow) rows.next();
                if (row.getLastCellNum() >= 2) {
                    while (rows.hasNext()) {
                        row = (XSSFRow) rows.next();
                        Publisher newItem = new Publisher(
                                row.getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue(),
                                row.getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue()
                        );
                        newPublishers.add(newItem);
                        publishers.add(newItem);
                    }
                } else
                    ExHandler.handle(new Exception("File không đúng định dạng." + row.getLastCellNum()));
                PublisherDAO.getInstance().importPublisher(newPublishers);

            } else if (result.get() == langImportBtn) {
                XSSFSheet sheet = getSheet();

                ArrayList<Language> newLanguages = new ArrayList<>();
                // DATA
                Iterator rows = sheet.rowIterator();
                XSSFRow row = (XSSFRow) rows.next();
                if (row.getLastCellNum() >= 2) {
                    while (rows.hasNext()) {
                        row = (XSSFRow) rows.next();
                        Language newItem = new Language(
                                row.getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue(),
                                row.getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue()
                        );
                        newLanguages.add(newItem);
                        languages.add(newItem);
                    }
                } else
                    ExHandler.handle(new Exception("File không đúng định dạng." + row.getLastCellNum()));
                LanguageDAO.getInstance().importLanguage(newLanguages);
            }
        });
    }

    public XSSFSheet getSheet() {
        XSSFWorkbook excelWorkBook;

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file.");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedFile = fileChooser.showOpenDialog(catTable.getScene().getWindow());

            FileInputStream inputStream = new FileInputStream(selectedFile);
            excelWorkBook = new XSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            ExHandler.handle(e);
            return null;
        }

        return excelWorkBook.getSheetAt(0);
    }
}
