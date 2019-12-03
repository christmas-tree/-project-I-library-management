/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.transaction;

import controller.basic.IndexController;
import dao.TransactionDAO;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.Transaction;
import model.User;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import util.ExHandler;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

public class SearchTransactionController {

    @FXML
    private Label dieuKienLabel;

    @FXML
    private TextField searchInputField;

    @FXML
    private DatePicker searchStartDate;

    @FXML
    private Button refreshBtn;

    @FXML
    private TextField searchStartValue;

    @FXML
    private TextField searchEndValue;

    @FXML
    private ChoiceBox<String> searchConditionChoiceBox;

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private DatePicker searchEndDate;

    @FXML
    private Button editBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private ChoiceBox<String> searchChoiceBox;

    @FXML
    private Button addBtn;

    @FXML
    private ImageView refreshIcon;

    private int searchType = -1;
    private ObservableList<Transaction> data;
    private User currentUser;

    public void init(IndexController c) {

        this.currentUser = c.currentUser;

        TableColumn<Transaction, String> idCol = new TableColumn<>("Mã mượn trả");
        TableColumn<Transaction, Timestamp> borowingDateCol = new TableColumn<>("Ngày mượn");

        TableColumn<Transaction, String> ridCol = new TableColumn<>("ID Độc giả");
        TableColumn<Transaction, String> rnameCol = new TableColumn<>("Họ tên độc giả");

        TableColumn<Transaction, String> sidCol = new TableColumn<>("ID Nhân viên mượn");
        TableColumn<Transaction, String> snameCol = new TableColumn<>("Họ tên NV mượn");

        TableColumn<Transaction, Date> dueDateCol = new TableColumn<>("Hạn trả");
        TableColumn<Transaction, Integer> quantityCol = new TableColumn<>("Số lượng mượn");

        idCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%06d", p.getValue().getTransactId())));
        borowingDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowingDate"));
        ridCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%06d", p.getValue().getBorrower().getRid())));
        rnameCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getBorrower().getName()));
        sidCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%06d", p.getValue().getBorrowStaff().getSid())));
        snameCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getBorrowStaff().getName()));

        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        transactionTable.getColumns().addAll(idCol, borowingDateCol, ridCol, rnameCol, sidCol, snameCol, dueDateCol, quantityCol);

        idCol.setSortType(TableColumn.SortType.ASCENDING);

        reloadData();

        String searchChoices[] = {"Mã NV cho mượn", "Tên NV cho mượn", "Mã độc giả", "Tên độc giả", "Thời gian mượn", "Hạn trả", "Số lượng sách mượn"};
        //                              0                       1                   2            3                  4            5              6

        searchChoiceBox.getItems().addAll(searchChoices);

        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, numPre, numPost) -> {
            searchType = numPost.intValue();
            dieuKienLabel.setVisible(true);

            searchInputField.setVisible(false);
            searchBtn.setVisible(false);
            searchConditionChoiceBox.setVisible(false);
            searchStartDate.setVisible(false);
            searchEndDate.setVisible(false);
            searchStartValue.setVisible(false);
            searchEndValue.setVisible(false);

            searchInputField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    switch (searchType) {
                        case 0:
                        case 2:
                            searchInputField.setText(t1.replaceAll("[^\\d]", ""));
                    }
                }
            });

//            String searchChoices[] = {"Mã nhân viên mượn", "Tên nhân viên mượn", "Mã độc giả", "Tên độc giả", "Thời gian mượn", "Hạn trả", "Số lượng sách mượn"};
            //                              0                       1                   2            3                  4            5              6

            switch (searchType) {
                case 0:
                case 1:
                case 2:
                case 3:
                    searchInputField.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 620.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 4:
                case 5:
                    searchStartDate.setVisible(true);
                    searchEndDate.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 670.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 6:
                    searchStartValue.setVisible(true);
                    searchEndValue.setVisible(true);
                    searchStartValue.textProperty().addListener((observable, oldValue, newValue) -> searchStartValue.setText(newValue.replaceAll("[^\\d]", "")));
                    searchEndValue.textProperty().addListener((observable, oldValue, newValue) -> searchEndValue.setText(newValue.replaceAll("[^\\d]", "")));
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 670.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
            }
        });

        transactionTable.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()))
                    edit();
            });
            return row;
        });

        searchBtn.setOnAction(event -> search());

        editBtn.setOnAction(event -> edit());

        addBtn.setOnAction(event -> add());

        refreshBtn.setOnAction(event -> refresh());

        c.editMenu.setDisable(false);
        c.addMenu.setDisable(false);
        c.exportMenu.setDisable(false);
        c.deleteMenu.setVisible(true);
        c.importMenu.setDisable(true);

        c.editMenu.setOnAction(event -> editBtn.fire());
        c.addMenu.setOnAction(event -> addBtn.fire());
        c.exportMenu.setOnAction(event -> export());
    }

    public void reloadData() {
        Runnable reload = () -> {
            try {
                data = FXCollections.observableArrayList(TransactionDAO.getInstance().getAllTransactions());
                Platform.runLater(() -> {
                    transactionTable.setItems(data);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> ExHandler.handle(e));
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

    public void search() {

        Runnable searchTask = () -> {
            try {
                switch (searchType) {
//            String searchChoices[] = {"Mã nhân viên mượn", "Tên nhân viên mượn", "Mã độc giả", "Tên độc giả", "Thời gian mượn", "Hạn trả", "Số lượng sách mượn"};
              //                         0                       1                   2            3                  4            5              6
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        data = FXCollections.observableArrayList(TransactionDAO.getInstance().searchTransaction(searchType, searchInputField.getText()));
                        break;
                    case 4:
                    case 5:
                        Timestamp startDate = Timestamp.valueOf(searchStartDate.getValue().atStartOfDay());
                        Timestamp endDate = Timestamp.valueOf(searchEndDate.getValue().plusDays(1).atStartOfDay());
                        data = FXCollections.observableArrayList(TransactionDAO.getInstance().searchTransaction(searchType, startDate, endDate));
                        break;
                    case 6:
                        int startValue = Integer.parseInt(searchStartValue.getText());
                        int endValue = Integer.parseInt(searchEndValue.getText());
                        data = FXCollections.observableArrayList(TransactionDAO.getInstance().searchTransactionByBookCount(startValue, endValue));
                        break;
                }
                Platform.runLater(() -> {
                    transactionTable.setItems(data);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> ExHandler.handle(e));
            }
        };
        new Thread(searchTask).start();
    }

    public void add() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/transaction/edittransaction.fxml"));
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot);

            stage.setScene(scene);
            stage.setResizable(false);

            EditTransactionController editTransactionController = loader.getController();
            editTransactionController.init(currentUser);

            stage.showAndWait();
            reloadData();

        } catch (IOException e) {
            ExHandler.handle(e);
        }

    }

    public void edit() {
        Transaction focusedTransaction = transactionTable.getSelectionModel().getSelectedItem();

        if (focusedTransaction == null) {
            ExHandler.handle(new RuntimeException("Bạn chưa chọn phiếu mượn trả nào."));
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("view/transaction/edittransaction.fxml"));
        try {
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot);
            stage.setScene(scene);
            stage.setResizable(false);
            EditTransactionController editTransactionController = loader.getController();
            editTransactionController.init(currentUser, focusedTransaction);

            stage.setTitle("Phiếu mượn trả");
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
            File file = new File("src/resources/form/DsMuonTra.xlsx");

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

            Transaction transaction;

            // DATE
            cell = sheet.getRow(1).getCell(5);
            cell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy")));
            cell.setCellStyle(dateStyle);

            // SEARCH TYPE
            if (searchType != -1) {
                String searchChoices[] = {"Mã NV cho mượn", "Tên NV cho mượn", "Mã độc giả", "Tên độc giả", "Thời gian mượn", "Hạn trả", "Số lượng sách mượn"};
                //                     0                  1                   2            3                  4            5              6
                String searchInfoString = searchChoices[searchType];

                switch (searchType) {
                    case 0:
                    case 2:
                        searchInfoString += ": " + searchInputField.getText();
                        break;
                    case 1:
                    case 3:
                        searchInfoString += " có chữ: " + searchInputField.getText();
                        break;
                    case 4:
                    case 5:
                        searchInfoString += " từ " +
                                DateTimeFormatter.ofPattern("dd/MM/YYYY").format(searchStartDate.getValue()) + " đến " +
                                DateTimeFormatter.ofPattern("dd/MM/YYYY").format(searchEndDate.getValue());
                        break;
                    case 6:
                        searchInfoString += " từ " +
                                searchStartValue.getText() + " đến " +
                                searchEndValue.getText();
                        break;
                }

                cell = sheet.getRow(5).getCell(0);
                cell.setCellStyle(searchInfoStyle);
                cell.setCellValue(searchInfoString);
            }

            // DATA
            for (int i = 0; i < data.size(); i++) {
                transaction = data.get(i);
                int row = 8 + i;
                sheet.createRow(row);

                cell = sheet.getRow(row).createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(1);
                cell.setCellValue(String.format("%06d", transaction.getTransactId()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(2);
                cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(transaction.getBorrowingDate()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(3);
                cell.setCellValue(String.format("%06d", transaction.getBorrower().getRid()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(4);
                cell.setCellValue(transaction.getBorrower().getName());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(5);
                cell.setCellValue(String.format("%06d", transaction.getBorrowStaff().getSid()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(6);
                cell.setCellValue(transaction.getBorrowStaff().getName());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(7);
                cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(transaction.getDueDate()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(8);
                cell.setCellValue(transaction.getQuantity());
                cell.setCellStyle(tableElementStyle);
            }

            // Ghi file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn vị trí lưu.");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialFileName("Tong Hop Muon Tra " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedFile = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());

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
}
