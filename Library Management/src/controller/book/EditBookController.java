/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.book;

import dao.BookDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import model.Book;
import model.Category;
import model.Language;
import model.Publisher;
import util.ExHandler;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class EditBookController {

    @FXML
    private ComboBox<Language> langComboBox;

    @FXML
    private Button cancelBtn;

    @FXML
    private Label headerLabel;

    @FXML
    private TextField createdTextField;

    @FXML
    private TextField pubYearTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button confirmBtn;

    @FXML
    private TextField locationTextField;

    @FXML
    private TextField bidTextField;

    @FXML
    private ComboBox<Category> catComboBox;

    @FXML
    private TextField availQuanTextField;

    @FXML
    private ComboBox<Publisher> pubComboBox;

    @FXML
    private TextField priceTextField;

    @FXML
    private TextField authorTextField;

    @FXML
    private TextField quantityTextField;

    private Map<String, Category> categories;
    private Map<String, Publisher> publishers;
    private Map<String, Language> languages;

    public void init(Map<String, Category> catList, Map<String, Publisher> pubList, Map<String, Language> langList) {
        categories = catList;
        publishers = pubList;
        languages = langList;

        availQuanTextField.setText("0");

        uiInit();

        confirmBtn.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                if (validate()) {
                    add();
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                }
            }
        });
    }

    public void init(Book book, Map<String, Category> catList, Map<String, Publisher> pubList, Map<String, Language> langList) {
        categories = catList;
        publishers = pubList;
        languages = langList;

        uiInit();

        bidTextField.setDisable(true);

        headerLabel.setText("Sửa đầu sách");

        bidTextField.setText(String.valueOf(book.getBid()));
        createdTextField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(book.getCreated()));
        nameTextField.setText(book.getBookName());
        priceTextField.setText(Long.toString(book.getPrice()));
        catComboBox.getSelectionModel().select(book.getCategory());
        authorTextField.setText(book.getAuthor());
        pubComboBox.getSelectionModel().select(book.getPublisher());
        pubYearTextField.setText(Integer.toString(book.getPubYear()));
        langComboBox.getSelectionModel().select(book.getLanguage());
        locationTextField.setText(book.getLocation());
        quantityTextField.setText(Integer.toString(book.getQuantity()));
        availQuanTextField.setText(Integer.toString(book.getAvailQuantity()));

        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (validate()) {
                    update(book);
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                }
            }
        });
    }

    public void uiInit() {

        catComboBox.getItems().addAll(categories.values());
        pubComboBox.getItems().addAll(publishers.values());
        langComboBox.getItems().addAll(languages.values());

        bidTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 6)
                bidTextField.setText(t1.substring(0, 6));
            }
        });

        priceTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    priceTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        pubYearTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    pubYearTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        quantityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                    quantityTextField.setText(newValue.replaceAll("[^\\d]", ""));
                    int oldQ = oldValue.isBlank()?0:Integer.parseInt(oldValue);
                    int newQ = newValue.isBlank()?0:Integer.parseInt(newValue);
                    int availQ = Integer.parseInt(availQuanTextField.getText());
                    availQuanTextField.setText(String.valueOf(availQ+(newQ - oldQ)));
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }
        });
    }

    public void update(Book book) {
        book.setBid(bidTextField.getText());
        book.setBookName(nameTextField.getText());
        book.setPrice(Long.parseLong(priceTextField.getText()));
        book.setCategory(catComboBox.getSelectionModel().getSelectedItem());
        book.setAuthor(authorTextField.getText());
        book.setPublisher(pubComboBox.getSelectionModel().getSelectedItem());
        book.setPubYear(Integer.parseInt(pubYearTextField.getText()));
        book.setLanguage(langComboBox.getSelectionModel().getSelectedItem());
        book.setLocation(locationTextField.getText());
        book.setQuantity(Integer.parseInt(quantityTextField.getText()));
        book.setAvailQuantity(Integer.parseInt(availQuanTextField.getText()));

        boolean success = false;

        try {
            success = BookDAO.getInstance().updateBook(book);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        if (success) {
            System.out.println("Sucessfully updated ID " + book.getBid());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Cập nhật đầu sách thành công");
            alert.setContentText("Đầu sách ID " + book.getBid() + ": " + book.getBookName() + " đã được cập nhật thành công vào cơ sở dữ liệu.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    public void add() {
        Book book = new Book();

        book.setBid(bidTextField.getText());
        book.setCreated(new Timestamp(System.currentTimeMillis()));
        book.setBookName(nameTextField.getText());
        book.setPrice(Long.parseLong(priceTextField.getText()));
        book.setCategory(catComboBox.getSelectionModel().getSelectedItem());
        book.setAuthor(authorTextField.getText());
        book.setPublisher(pubComboBox.getSelectionModel().getSelectedItem());
        book.setPubYear(Integer.parseInt(pubYearTextField.getText()));
        book.setLanguage(langComboBox.getSelectionModel().getSelectedItem());
        book.setLocation(locationTextField.getText());
        book.setQuantity(Integer.parseInt(quantityTextField.getText()));
        book.setAvailQuantity(Integer.parseInt(availQuanTextField.getText()));

        boolean success = false;

        try {
            success = BookDAO.getInstance().createBook(book);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        if (success) {
            System.out.println("Sucessfully added new book: " + book.getBookName());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Thêm đầu sách thành công");
            alert.setContentText("Đầu sách " + book.getBookName() + " đã được thêm thành công vào cơ sở dữ liệu.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    public boolean validate() {
        String err = "";
        if (bidTextField.getText().isBlank()) {
            err += "Không được bỏ trống mã sách.\n";
        }
        if (nameTextField.getText().isBlank()) {
            err += "Không được bỏ trống tên sách.\n";
        }
        if (priceTextField.getText().isBlank()) {
            err += "Không được bỏ trống giá tiền.\n";
        } else if (Integer.parseInt(priceTextField.getText()) < 1000) {
            err += "Giá tiền không hợp lệ.\n";
        }
        if (catComboBox.getSelectionModel().getSelectedItem() == null) {
            err += "Không được bỏ trống thể loại.\n";
        }
        if (authorTextField.getText().isBlank()) {
            err += "Không được bỏ trống tác giả.\n";
        }
        if (pubComboBox.getSelectionModel().getSelectedItem() == null) {
            err += "Không được bỏ trống nhà xuất bản.\n";
        }
        if (pubYearTextField.getText().isBlank()) {
            err += "Không được bỏ trống năm xuất bản.\n";
        } else {
            int year = Integer.parseInt(pubYearTextField.getText());
            if (year < 1800 || year > Calendar.getInstance().get(Calendar.YEAR))
                err += "Năm xuất bản không hợp lệ.\n";
        }
        if (langComboBox.getSelectionModel().getSelectedItem() == null) {
            err += "Không được bỏ trống ngôn ngữ.\n";
        }
        if (quantityTextField.getText().isBlank()) {
            err += "Không được bỏ trống số lượng.\n";
        }
        if (Integer.parseInt(availQuanTextField.getText()) < 0) {
            err += "Số lượng sách không hợp lệ.\n";
        }
        if (err.isBlank()) {
            return true;
        } else {
            ExHandler.handle(new Exception(err));
            return false;
        }
    }
}
