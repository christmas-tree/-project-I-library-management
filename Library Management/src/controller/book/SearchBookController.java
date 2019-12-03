/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.book;

import controller.basic.IndexController;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.Book;
import model.Category;
import model.Language;
import model.Publisher;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import util.ExHandler;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

public class SearchBookController {

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

    private int searchType = -1;

    public void init(IndexController c) {

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


        String[] searchChoices = {"Mã sách", "Tên sách", "Thời gian thêm", "Giá", "Thể loại", "Tác giả", "Nhà xuất bản", "Năm xuất bản", "Ngôn ngữ"};
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

        searchBtn.setOnAction(event -> search());
        editBtn.setOnAction(event -> edit());
        addBtn.setOnAction(event -> add());
        deleteBtn.setOnAction(event -> delete());
        refreshBtn.setOnAction(event -> refresh());

        c.exportMenu.setDisable(false);
        c.addMenu.setDisable(false);
        c.editMenu.setDisable(false);
        c.deleteMenu.setDisable(false);
//        c.backupMenu.setDisable(false);
        c.importMenu.setDisable(false);

        c.exportMenu.setOnAction(event -> export());
        c.deleteMenu.setOnAction(event -> deleteBtn.fire());
        c.editMenu.setOnAction(event -> editBtn.fire());
        c.addMenu.setOnAction(event -> addBtn.fire());
        // // c.backupMenu.setOnAction(event -> backup());
        c.importMenu.setOnAction(event -> importData());
    }

    public void reloadData() {
        Runnable reload = () -> {
            try {
                categories = CategoryDAO.getInstance().getAllCategories();
                publishers = PublisherDAO.getInstance().getAllPublishers();
                languages = LanguageDAO.getInstance().getAllLanguages();
                data = FXCollections.observableArrayList(BookDAO.getInstance().getAllBooks(categories, publishers, languages));
                Platform.runLater(() -> {
                    bookTable.setItems(data);
                    bookTable.refresh();
                });
            } catch (SQLException e) {
                Platform.runLater(() -> ExHandler.handle(e));
            }
        };
        new Thread(reload).start();
    }

    public void refresh() {
        reloadData();

        searchChoiceBox.getSelectionModel().clearSelection();
        searchType = -1;

        RotateTransition rt = new RotateTransition(Duration.millis(750), refreshIcon);
        rt.setByAngle(360 * 3);
        rt.setCycleCount(1);
        rt.setInterpolator(Interpolator.EASE_BOTH);
        rt.play();
    }

    //String searchChoices[] = {"Mã sách", "Tên sách", "Thời gian thêm", "Giá", "Thể loại", "Tác giả", "Nhà xuất bản", "Năm xuất bản", "Ngôn ngữ"};
    //                             0            1       2               3           4           5           6               7              8

    public void search() {

        Runnable searchTask = () -> {
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

    public void export() {
        if (data.size() == 0) {
            ExHandler.handle(new Exception("Không tìm thấy dữ liệu phù hợp với lựa chọn tìm kiếm."));
        } else {
            File file = new File("src/resources/form/DsSach.xlsx");

            XSSFWorkbook workbook;

            try {
                FileInputStream inputStream = new FileInputStream(file);
                workbook = new XSSFWorkbook(inputStream);
                inputStream.close();
            } catch (IOException e) {
                ExHandler.handle(e);
                return;
            }

            XSSFSheet sheet = workbook.getSheetAt(0);

            // CELL STYLES
            XSSFCellStyle tableElementStyle = workbook.createCellStyle();
            tableElementStyle.setBorderLeft(BorderStyle.THIN);
            tableElementStyle.setBorderRight(BorderStyle.THIN);
            tableElementStyle.setFont(workbook.createFont());
            tableElementStyle.getFont().setFontHeightInPoints((short) 9);
            tableElementStyle.getFont().setFontName("Arial");
            tableElementStyle.setWrapText(true);

            XSSFCellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setFont(workbook.createFont());
            dateStyle.getFont().setFontHeightInPoints((short) 9);
            dateStyle.getFont().setFontName("Arial");
            dateStyle.setAlignment(HorizontalAlignment.CENTER);
            dateStyle.getFont().setItalic(true);

            XSSFCellStyle searchInfoStyle = workbook.createCellStyle();
            searchInfoStyle.setFont(workbook.createFont());
            searchInfoStyle.getFont().setFontHeightInPoints((short) 9);
            searchInfoStyle.setAlignment(HorizontalAlignment.CENTER);
            searchInfoStyle.getFont().setFontName("Arial");

            XSSFCell cell = null;

            Book book;

            // DATE
            cell = sheet.getRow(1).getCell(6);
            cell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy")));
            cell.setCellStyle(dateStyle);

            // SEARCH TYPE
            if (searchType != -1) {
                String[] searchChoices = {"Mã sách", "Tên sách", "Thời gian thêm", "Giá", "Thể loại", "Tác giả", "Nhà xuất bản", "Năm xuất bản", "Ngôn ngữ"};
                //                             0            1       2               3           4           5           6               7              8
                String searchInfoString = searchChoices[searchType];

                switch (searchType) {
                    case 0:
                    case 1:
                    case 5:
                        searchInfoString += " có: " + searchInputField.getText();
                        break;
                    case 2:
                        searchInfoString += " từ " +
                                DateTimeFormatter.ofPattern("dd/MM/YYYY").format(searchStartDate.getValue()) + " đến " +
                                DateTimeFormatter.ofPattern("dd/MM/YYYY").format(searchEndDate.getValue());
                        break;
                    case 3:
                    case 7:
                        searchInfoString += " từ " +
                                searchStartValue.getText() + " đến " +
                                searchEndValue.getText();
                        break;
                    case 4:
                    case 6:
                    case 8:
                        searchInfoString += ": " + searchConditionChoiceBox.getSelectionModel().getSelectedItem().toString();
                        break;
                }

                cell = sheet.getRow(5).getCell(0);
                cell.setCellStyle(searchInfoStyle);
                cell.setCellValue(searchInfoString);
            }

            // DATA
            for (int i = 0; i < data.size(); i++) {
                book = data.get(i);
                int row = 8 + i;
                sheet.createRow(row);

                cell = sheet.getRow(row).createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(1);
                cell.setCellValue(book.getBid());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(2);
                cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(book.getCreated()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(3);
                cell.setCellValue(book.getBookName());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(4);
                cell.setCellValue(book.getPrice());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(5);
                cell.setCellValue(book.getCategory().getCatName());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(6);
                cell.setCellValue(book.getAuthor());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(7);
                cell.setCellValue(book.getPublisher().getPubName());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(8);
                cell.setCellValue(book.getPubYear());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(9);
                cell.setCellValue(book.getLanguage().getLanguage());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(10);
                cell.setCellValue(book.getQuantity());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(11);
                cell.setCellValue(book.getAvailQuantity());
                cell.setCellStyle(tableElementStyle);
            }

            // Ghi file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn vị trí lưu.");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialFileName("Thong Tin Sach " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedFile = fileChooser.showSaveDialog(bookTable.getScene().getWindow());

            try {
                FileOutputStream output = new FileOutputStream(selectedFile);
                workbook.write(output);
                output.close();
                Desktop.getDesktop().open(selectedFile);
            } catch (IOException e) {
                ExHandler.handle(e);
            }
        }
    }

//    public void backup() {
//        List<Book> books;
//        Book book;
//
//        try {
//            books = BookDAO.getInstance().getAllBooks(categories, publishers, languages);
//        } catch (SQLException e) {
//            ExHandler.handle(e);
//            return;
//        }
//
//        XSSFWorkbook excelWorkBook = new XSSFWorkbook();
//        XSSFSheet sheet = excelWorkBook.createSheet();
//
//        XSSFCell cell = null;
//
//        String[] props = {"bid", "created", "bookName", "price", "catId", "author", "pubId", "pubYear", "langId", "location", "quantity", "availQuantity"};
//
//        XSSFRow row = sheet.createRow(0);
//        row.createCell(0).setCellValue(props[0]);
//        row.createCell(1).setCellValue(props[1]);
//        row.createCell(2).setCellValue(props[2]);
//        row.createCell(3).setCellValue(props[3]);
//        row.createCell(4).setCellValue(props[4]);
//        row.createCell(5).setCellValue(props[5]);
//        row.createCell(6).setCellValue(props[6]);
//        row.createCell(7).setCellValue(props[7]);
//        row.createCell(8).setCellValue(props[8]);
//        row.createCell(9).setCellValue(props[9]);
//        row.createCell(10).setCellValue(props[10]);
//        row.createCell(11).setCellValue(props[11]);
//
//
//        for (int i = 0; i < data.size(); i++) {
//            int j = i + 1;
//
//            book = books.get(i);
//            row = sheet.createRow(j);
//
//            row.createCell(0).setCellValue(book.getBid());
//            row.createCell(1).setCellValue(book.getCreated());
//            row.createCell(2).setCellValue(book.getBookName());
//            row.createCell(3).setCellValue(book.getPrice());
//            row.createCell(4).setCellValue(book.getCategory().getCatId());
//            row.createCell(5).setCellValue(book.getAuthor());
//            row.createCell(6).setCellValue(book.getPublisher().getPubId());
//            row.createCell(7).setCellValue(book.getPubYear());
//            row.createCell(8).setCellValue(book.getLanguage().getLangId());
//            row.createCell(9).setCellValue(book.getLocation());
//            row.createCell(10).setCellValue(book.getQuantity());
//            row.createCell(11).setCellValue(book.getAvailQuantity());
//        }
//
//        // Ghi file
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Chọn vị trí lưu.");
//        fileChooser.getExtensionFilters().add(
//                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
//        );
//        fileChooser.setInitialFileName("BookBackup " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
//
//        File selectedFile = fileChooser.showSaveDialog(bookTable.getScene().getWindow());
//
//        try {
//            FileOutputStream output = new FileOutputStream(selectedFile);
//            excelWorkBook.write(output);
//            output.close();
//            Desktop.getDesktop().open(selectedFile);
//        } catch (IOException e) {
//            ExHandler.handle(e);
//        }
//    }

    public void importData() {

        ArrayList<Book> newBooks = new ArrayList<>();
        Book newBook;

        XSSFWorkbook excelWorkBook;

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file.");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedFile = fileChooser.showOpenDialog(bookTable.getScene().getWindow());


            FileInputStream inputStream = new FileInputStream(selectedFile);
            excelWorkBook = new XSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            ExHandler.handle(e);
            return;
        }

        XSSFSheet sheet = excelWorkBook.getSheetAt(0);

        // DATA
        Iterator rows = sheet.rowIterator();
        XSSFRow row = (XSSFRow) rows.next();
        if (row.getLastCellNum() >= 10) {
            while (rows.hasNext()) {
                row = (XSSFRow) rows.next();
                newBook = new Book();

                newBook.setBid(row.getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue());
                newBook.setBookName(row.getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue());
                newBook.setPrice((long) row.getCell(2, CREATE_NULL_AS_BLANK).getNumericCellValue());
                newBook.setCategory(categories.get(row.getCell(3, CREATE_NULL_AS_BLANK).getStringCellValue()));
                newBook.setAuthor(row.getCell(4, CREATE_NULL_AS_BLANK).getStringCellValue());
                newBook.setPublisher(publishers.get(row.getCell(5, CREATE_NULL_AS_BLANK).getStringCellValue()));
                newBook.setPubYear((int) row.getCell(6, CREATE_NULL_AS_BLANK).getNumericCellValue());
                newBook.setLanguage(languages.get(row.getCell(7, CREATE_NULL_AS_BLANK).getStringCellValue()));
                newBook.setLocation(row.getCell(8, CREATE_NULL_AS_BLANK).getStringCellValue());
                newBook.setQuantity((int) row.getCell(9, CREATE_NULL_AS_BLANK).getNumericCellValue());
                newBook.setAvailQuantity(newBook.getQuantity());

                newBooks.add(newBook);
            }
        } else
            ExHandler.handle(new Exception("File không đúng định dạng." + row.getLastCellNum()));

        BookDAO.getInstance().importBook(newBooks);
        refresh();
    }
}

