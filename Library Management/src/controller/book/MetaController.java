/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.book;

import dao.CategoryDAO;
import dao.LanguageDAO;
import dao.PublisherDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Category;
import model.Language;
import model.Publisher;
import util.ExHandler;

import java.sql.SQLException;

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
    private TableView<String> pubTable;

    @FXML
    private TextField langIdField;

    @FXML
    private TableView<String> catTable;

    @FXML
    private TextField catIdField;

    @FXML
    private TableView<String> langTable;

    @FXML
    private Button addLangBtn;

    @FXML
    private TableColumn<String, String> catIdCol;

    @FXML
    private TableColumn<String, Category> catCol;

    @FXML
    private TableColumn<String, String> pubIdCol;

    @FXML
    private TableColumn<String, Publisher> pubCol;

    @FXML
    private TableColumn<String, String> langIdCol;

    @FXML
    private TableColumn<String, Language> langCol;

    ObservableMap<String, Category> categories;
    ObservableMap<String, Publisher> publishers;
    ObservableMap<String, Language> languages;

    ObservableList<String> catKeys = FXCollections.observableArrayList();
    ObservableList<String> pubKeys = FXCollections.observableArrayList();
    ObservableList<String> langKeys = FXCollections.observableArrayList();

    public void init() {
        try {
            categories = FXCollections.observableMap(CategoryDAO.getInstance().getAllCategories());
            publishers = FXCollections.observableMap(PublisherDAO.getInstance().getAllPublishers());
            languages = FXCollections.observableMap(LanguageDAO.getInstance().getAllLanguages());
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        // CATEGORIES

        categories.addListener((MapChangeListener<String, Category>) change -> {
            boolean removed = change.wasRemoved();
            if (removed != change.wasAdded()) {
                // no put for existing key
                if (removed) {
                    catKeys.remove(change.getKey());
                } else {
                    catKeys.add(change.getKey());
                }
            }
        });

        catKeys.addAll(categories.keySet());

        catIdCol.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue()));
        catCol.setCellValueFactory(cd -> Bindings.valueAt(categories, cd.getValue()));

        catTable.setItems(catKeys);

        addCatBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (catIdField.getText().isBlank() || catNameField.getText().isBlank()) {
                    ExHandler.handle(new Exception("Mã thể loại hoặc Tên thể loại đang để trống."));
                } else try {
                    Category newCat = new Category(catIdField.getText(), catNameField.getText());
                    categories.put(newCat.getCatId(), newCat);
                    CategoryDAO.getInstance().createCategory(newCat);
                } catch (SQLException e) {
                    ExHandler.handle(e);
                }
            }
        });

        catIdField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 2) {
                    catIdField.setText(t1.substring(0, 2));
                }
            }
        });

        catNameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 30) {
                    catNameField.setText(t1.substring(0, 30));
                }
            }
        });


        // PUBLISHERS
        publishers.addListener((MapChangeListener<String, Publisher>) change -> {
            boolean removed = change.wasRemoved();
            if (removed != change.wasAdded()) {
                // no put for existing key
                if (removed) {
                    pubKeys.remove(change.getKey());
                } else {
                    pubKeys.add(change.getKey());
                }
            }
        });

        pubKeys.addAll(publishers.keySet());

        pubIdCol.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue()));
        pubCol.setCellValueFactory(cd -> Bindings.valueAt(publishers, cd.getValue()));

        pubTable.setItems(pubKeys);

        addPubBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (pubIdField.getText().isBlank() || pubNameField.getText().isBlank()) {
                    ExHandler.handle(new Exception("Mã NXB hoặc Tên NXB đang để trống."));
                } else try {
                    Publisher newPub = new Publisher(pubIdField.getText(), pubNameField.getText());
                    publishers.put(newPub.getPubId(), newPub);
                    PublisherDAO.getInstance().createPublisher(newPub);
                } catch (SQLException e) {
                    ExHandler.handle(e);
                }
            }
        });

        pubIdField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 3) {
                    pubIdField.setText(t1.substring(0, 3));
                }
            }
        });

        pubNameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 50) {
                    pubNameField.setText(t1.substring(0, 50));
                }
            }
        });

        // LANGUAGES
        languages.addListener((MapChangeListener<String, Language>) change -> {
            boolean removed = change.wasRemoved();
            if (removed != change.wasAdded()) {
                // no put for existing key
                if (removed) {
                    langKeys.remove(change.getKey());
                } else {
                    langKeys.add(change.getKey());
                }
            }
        });

        langKeys.addAll(languages.keySet());

        langIdCol.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue()));
        langCol.setCellValueFactory(cd -> Bindings.valueAt(languages, cd.getValue()));

        langTable.setItems(langKeys);

        addLangBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (langIdField.getText().isBlank() || languageField.getText().isBlank()) {
                    ExHandler.handle(new Exception("Mã NN hoặc Ngôn ngữ đang để trống."));
                } else try {
                    Language newLang = new Language(langIdField.getText(), languageField.getText());
                    languages.put(newLang.getLangId(), newLang);
                    LanguageDAO.getInstance().createLanguage(newLang);
                } catch (SQLException e) {
                    ExHandler.handle(e);
                }
            }
        });

        langIdField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 2) {
                    langIdField.setText(t1.substring(0, 2));
                }
            }
        });

        languageField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 30) {
                    languageField.setText(t1.substring(0, 30));
                }
            }
        });

    }
}
