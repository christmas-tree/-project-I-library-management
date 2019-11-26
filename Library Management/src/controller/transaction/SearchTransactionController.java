/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.transaction;

import controller.basic.IndexController;
import controller.transaction.EditTransactionController;
import dao.TransactionDAO;
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
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.Transaction;
import model.User;
import util.ExHandler;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

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

    @FXML
    private AnchorPane searchTransactionPane;

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

        String searchChoices[] = {"Mã nhân viên mượn", "Tên nhân viên mượn", "Mã độc giả", "Tên độc giả", "Thời gian mượn", "Hạn trả", "Số lượng sách mượn"};
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
        c.deleteMenu.setDisable(true);

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

    }
}
