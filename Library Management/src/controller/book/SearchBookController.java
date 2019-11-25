/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.book;

import dao.BookDAO;
import dao.CategoryDAO;
import dao.LanguageDAO;
import dao.PublisherDAO;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.Book;
import model.Category;
import model.Language;
import model.Publisher;
import util.ExHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

public class SearchBookController {
    @FXML
    private AnchorPane searchBookPane;

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private ChoiceBox searchChoiceBox;

    @FXML
    private Label dieuKienLabel;

    @FXML
    private ChoiceBox searchConditionChoiceBox;

    @FXML
    private DatePicker searchStartDate;

    @FXML
    private DatePicker searchEndDate;

    @FXML
    private TextField searchInputField;

    @FXML
    private TextField searchStartValue;

    @FXML
    private TextField searchEndValue;

    @FXML
    private Button searchBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private ImageView refreshIcon;

    private ObservableList<Book> data;
    private Map<String, Category> categories;
    private Map<String, Publisher> publishers;
    private Map<String, Language> languages;

    private int searchType = 0;

    public void init() {

        TableColumn<Book, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(75.0);
        TableColumn<Book, Timestamp> createdCol = new TableColumn<>("Ngày tạo");
        createdCol.setMinWidth(150.0);
        TableColumn<Book, String> nameCol = new TableColumn<>("Tên sách");
        nameCol.setMinWidth(250.0);
        TableColumn<Book, String> priceCol = new TableColumn<>("Giá");
        priceCol.setMinWidth(100.0);
        priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        TableColumn<Book, Category> catCol = new TableColumn<>("Thể loại");
        catCol.setMinWidth(150.0);
        TableColumn<Book, String> authorCol = new TableColumn<>("Tác giả");
        authorCol.setMinWidth(200.0);
        TableColumn<Book, Publisher> pubCol = new TableColumn<>("NXB");
        pubCol.setMinWidth(150.0);
        TableColumn<Book, Integer> pubYearCol = new TableColumn<>("Năm XB");
        pubYearCol.setMinWidth(100.0);
        TableColumn<Book, Language> langCol = new TableColumn<>("Ngôn ngữ");
        langCol.setMinWidth(150.0);
        TableColumn<Book, String> locationCol = new TableColumn<>("Vị trí");
        locationCol.setMinWidth(100.0);
        TableColumn<Book, Integer> quantityCol = new TableColumn<>("Số lượng");
        quantityCol.setMinWidth(50.0);
        TableColumn<Book, Integer> availQuantityCol = new TableColumn<>("Còn lại");
        availQuantityCol.setMinWidth(50.0);

        idCol.setCellValueFactory(new PropertyValueFactory<>("bid"));
        createdCol.setCellValueFactory(new PropertyValueFactory<>("created"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        priceCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%,d", p.getValue().getPrice())));
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        pubCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        pubYearCol.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        langCol.setCellValueFactory(new PropertyValueFactory<>("language"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        availQuantityCol.setCellValueFactory(new PropertyValueFactory<>("availQuantity"));


        bookTable.getColumns().addAll(idCol, createdCol, nameCol, priceCol, catCol, authorCol, pubCol, pubYearCol, langCol, locationCol, quantityCol, availQuantityCol);

        reloadData();
        nameCol.setSortType(TableColumn.SortType.ASCENDING);


        String searchChoices[] = {"Mã sách", "Tên sách", "Thời gian thêm", "Giá", "Thể loại", "Tác giả", "Nhà xuất bản", "Năm xuất bản", "Ngôn ngữ"};
        //                             0            1       2               3           4           5           6               7              8

        searchChoiceBox.getItems().addAll(searchChoices);

        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, numPre, numPost) -> {
            dieuKienLabel.setVisible(true);

            searchInputField.setVisible(false);
            searchBtn.setVisible(false);
            searchConditionChoiceBox.setVisible(false);
            searchStartDate.setVisible(false);
            searchEndDate.setVisible(false);
            searchStartValue.setVisible(false);
            searchEndValue.setVisible(false);

            searchType = numPost.intValue();

            switch (searchType) {
                case 0:
                case 1:
                case 5:
                    searchInputField.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 620.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 2:
                    searchStartDate.setVisible(true);
                    searchEndDate.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 670.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 3: //Gia
                case 7: // Nam XB
                    searchStartValue.setVisible(true);
                    searchEndValue.setVisible(true);
                    searchStartValue.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (!newValue.matches("\\d*")) {
                                searchStartValue.setText(newValue.replaceAll("[^\\d]", ""));
                            }
                        }
                    });
                    searchEndValue.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (!newValue.matches("\\d*")) {
                                searchEndValue.setText(newValue.replaceAll("[^\\d]", ""));
                            }
                        }
                    });
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 670.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 4: // Thể loại
                    searchConditionChoiceBox.getItems().clear();
                    searchConditionChoiceBox.getItems().addAll(categories.values());
                    searchConditionChoiceBox.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 520.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 6: // NXB
                    searchConditionChoiceBox.getItems().clear();
                    searchConditionChoiceBox.getItems().addAll(publishers.values());
                    searchConditionChoiceBox.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 520.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 8: // Ngôn ngữ
                    searchConditionChoiceBox.getItems().clear();
                    searchConditionChoiceBox.getItems().addAll(languages.values());
                    searchConditionChoiceBox.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 520.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
            }
        });

        bookTable.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    edit();
                }
            });
            return row;
        });

        searchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                search();
            }
        });

        editBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                edit();
            }
        });

        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                add();
            }
        });

        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                delete();
            }
        });

        refreshBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bookTable.refresh();
            }
        });
    }

    public void reloadData() {
        Runnable reload = new Runnable() {
            @Override
            public void run() {
                try {
                    categories = CategoryDAO.getInstance().getAllCategories();
                    publishers = PublisherDAO.getInstance().getAllPublishers();
                    languages = LanguageDAO.getInstance().getAllLanguages();
                    data = FXCollections.observableArrayList(BookDAO.getInstance().getAllBooks(categories, publishers, languages));
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            bookTable.setItems(data);
                            bookTable.refresh();
                        }
                    });
                } catch (SQLException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ExHandler.handle(e);
                        }
                    });
                }
            }
        };
        new Thread(reload).start();
    }

    public void refresh() {
        reloadData();

        RotateTransition rt = new RotateTransition(Duration.millis(750), refreshIcon);
        rt.setByAngle(360 * 3);
        rt.setCycleCount(1);
        rt.setInterpolator(Interpolator.EASE_BOTH);
        rt.play();
    }

    // ISSUES WITH THE FIRST 2 SEARCHES

    //String searchChoices[] = {"Mã sách", "Tên sách", "Thời gian thêm", "Giá", "Thể loại", "Tác giả", "Nhà xuất bản", "Năm xuất bản", "Ngôn ngữ"};
    //                             0            1       2               3           4           5           6               7              8

    public void search() {

        Runnable searchTask = new Runnable() {
            @Override
            public void run() {
                System.out.println("Searching case " + searchType);
                try {
                    switch (searchType) {
                        case 0:
                        case 1:
                        case 5:
                            data = FXCollections.observableArrayList(BookDAO.getInstance().searchBook(searchType, searchInputField.getText(), categories, publishers, languages));
                            break;
                        case 2:
                            Timestamp startDate = Timestamp.valueOf(searchStartDate.getValue().atStartOfDay());
                            Timestamp endDate = Timestamp.valueOf(searchEndDate.getValue().plusDays(1).atStartOfDay());
                            data = FXCollections.observableArrayList(BookDAO.getInstance().searchBookByCreatedTime(startDate, endDate, categories, publishers, languages));
                            break;
                        case 3:
                        case 7:
                            int startValue = Integer.parseInt(searchStartValue.getText());
                            int endValue = Integer.parseInt(searchEndValue.getText());
                            data = FXCollections.observableArrayList(BookDAO.getInstance().searchBook(searchType, startValue, endValue, categories, publishers, languages));
                            break;
                        case 4:
                            String value = ((Category) searchConditionChoiceBox.getSelectionModel().getSelectedItem()).getCatId();
                            data = FXCollections.observableArrayList(BookDAO.getInstance().searchBook(searchType, value, categories, publishers, languages));
                            break;
                        case 6:
                            String value2 = ((Publisher) searchConditionChoiceBox.getSelectionModel().getSelectedItem()).getPubId();
                            data = FXCollections.observableArrayList(BookDAO.getInstance().searchBook(searchType, value2, categories, publishers, languages));
                            break;
                        case 8:
                            String value3 = ((Language) searchConditionChoiceBox.getSelectionModel().getSelectedItem()).getLangId();
                            data = FXCollections.observableArrayList(BookDAO.getInstance().searchBook(searchType, value3, categories, publishers, languages));
                            break;
                    }
                    Platform.runLater(() -> {
                        bookTable.setItems(data);
                    });
                } catch (SQLException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ExHandler.handle(e);
                        }
                    });
                }
            }
        };

        new Thread(searchTask).start();
    }

    public void add() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/book/editbook.fxml"));
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot);

            stage.setTitle("Thêm đầu sách");
            stage.setScene(scene);
            stage.setResizable(false);

            EditBookController editBookController = loader.getController();
            editBookController.init(categories, publishers, languages);

            stage.showAndWait();
            reloadData();

        } catch (IOException e) {
            ExHandler.handle(e);
        }

    }

    public void delete() {
        Book focusedBook = bookTable.getSelectionModel().getSelectedItem();

        if (focusedBook == null) {
            ExHandler.handle(new RuntimeException("Bạn chưa chọn đầu sách nào."));
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xoá");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xoá?");
        confirmAlert.setContentText("Bạn đang thực hiện xoá Đầu sách ID " + focusedBook.getBid() + " - " + focusedBook.getBookName() + ".");
        confirmAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> choice = confirmAlert.showAndWait();

        if (choice.get() == ButtonType.OK) {
            try {
                if (BookDAO.getInstance().deleteBook(focusedBook)) {
                    Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                    resultAlert.setTitle("Kết quả xoá");
                    resultAlert.setHeaderText("Xoá thành công!");
                    resultAlert.setContentText("Đã xoá đầu sách ID " + focusedBook.getBid() + " - " + focusedBook.getBookName() + " thành công.");
                    resultAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    resultAlert.showAndWait();
                    reloadData();
                } else {
                    Alert resultAlert = new Alert(Alert.AlertType.ERROR);
                    resultAlert.setTitle("Kết quả xoá");
                    resultAlert.setHeaderText("Xoá thất bại!");
                    resultAlert.setContentText("Đầu sách ID " + focusedBook.getBid() + " - " + focusedBook.getBookName() + " chưa xoá khỏi CSDL.");
                    resultAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    resultAlert.showAndWait();
                }
                ;
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        }
    }

    public void edit() {
        Book focusedBook = bookTable.getSelectionModel().getSelectedItem();

        if (focusedBook == null) {
            ExHandler.handle(new RuntimeException("Bạn chưa chọn đầu sách nào."));
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("view/book/editbook.fxml"));
        try {
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot);
            stage.setTitle("Sửa đầu sách");
            stage.setScene(scene);
            stage.setResizable(false);
            EditBookController editBookController = loader.getController();
            editBookController.init(focusedBook, categories, publishers, languages);

            stage.showAndWait();
        } catch (IOException e) {
            ExHandler.handle(e);
        }

        reloadData();
    }

}
