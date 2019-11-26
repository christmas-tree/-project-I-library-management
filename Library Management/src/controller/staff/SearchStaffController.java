/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.staff;

import controller.basic.IndexController;
import controller.staff.EditStaffController;
import dao.StaffDAO;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import model.Staff;
import util.ExHandler;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class SearchStaffController {

    @FXML
    private AnchorPane searchStaffPane;

    @FXML
    private TableView<Staff> staffTable;

    @FXML
    private ChoiceBox searchChoiceBox;

    @FXML
    private Label dieuKienLabel;

    @FXML
    private ChoiceBox<String> searchConditionChoiceBox;

    @FXML
    private DatePicker searchStartDate;

    @FXML
    private DatePicker searchEndDate;

    @FXML
    private TextField searchInputField;

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

    private ObservableList<Staff> data;

    private int searchType = -1;

    public void init(IndexController c) {

        TableColumn<Staff, String> idCol = new TableColumn<>("ID");
        TableColumn<Staff, Timestamp> createdCol = new TableColumn<>("Ngày tạo");
        TableColumn<Staff, String> nameCol = new TableColumn<>("Họ tên");
        TableColumn<Staff, Date> dobCol = new TableColumn<>("Ngày sinh");
        TableColumn<Staff, String> genderCol = new TableColumn<>("Giới tính");
        TableColumn<Staff, String> isAdminCol = new TableColumn<>("Vai trò");
        TableColumn<Staff, String> usernameCol = new TableColumn<>("Tên đăng nhập");

        idCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%06d", p.getValue().getSid())));
        createdCol.setCellValueFactory(new PropertyValueFactory<>("created"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        genderCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<String>(p.getValue().getGender() ? "Nam" : "Nữ"));
        isAdminCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().isAdmin() ? "Quản lý" : "Nhân viên"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        staffTable.getColumns().addAll(idCol, createdCol, nameCol, dobCol, genderCol, isAdminCol, usernameCol);

        idCol.setSortType(TableColumn.SortType.ASCENDING);

        reloadData();

        String searchChoices[] = {"Mã nhân viên", "Tên", "Thời gian tạo", "Giới tính", "Vai trò", "Tên đăng nhập"};
        //                              0             1         2               3         4              5

        searchChoiceBox.getItems().addAll(searchChoices);

        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, numPre, numPost) -> {
            searchType = numPost.intValue();

            dieuKienLabel.setVisible(true);
            searchInputField.setVisible(false);
            searchBtn.setVisible(false);
            searchConditionChoiceBox.setVisible(false);
            searchStartDate.setVisible(false);
            searchEndDate.setVisible(false);

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
                case 3:
                    searchType = 3;
                    searchConditionChoiceBox.getItems().clear();
                    searchConditionChoiceBox.getItems().addAll("Nam", "Nữ");
                    searchConditionChoiceBox.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 520.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 4:
                    searchType = 4;
                    searchConditionChoiceBox.getItems().clear();
                    searchConditionChoiceBox.getItems().addAll("Nhân viên", "Quản lý");
                    searchConditionChoiceBox.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 520.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
            }
        });

        staffTable.setRowFactory(tv -> {
            TableRow<Staff> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()))
                    edit();
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
                refresh();
            }
        });

        c.addMenu.setDisable(false);
        c.deleteMenu.setDisable(false);
        c.editMenu.setDisable(false);
        c.exportMenu.setDisable(false);

        c.addMenu.setOnAction(event -> addBtn.fire());
        c.deleteMenu.setOnAction(event -> deleteBtn.fire());
        c.editMenu.setOnAction(event -> editBtn.fire());
        c.exportMenu.setOnAction(event -> export());
    }

    public void reloadData() {
        Runnable reload = new Runnable() {
            @Override
            public void run() {
                try {
                    data = FXCollections.observableArrayList(StaffDAO.getInstance().getAllStaffs());
                    Platform.runLater(() -> {
                        staffTable.setItems(data);
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

    public void search() {

        Runnable searchTask = new Runnable() {
            @Override
            public void run() {
                try {
                    switch (searchType) {
//                        String searchChoices[] = {"Mã nhân viên", "Tên", "Thời gian tạo", "Giới tính", "Vai trò", "Tên đăng nhập"};
                        //                              0             1         2               3         4              5
                        case 0:
                        case 1:
                        case 5:
                            data = FXCollections.observableArrayList(StaffDAO.getInstance().searchStaff(searchType, searchInputField.getText()));
                            break;
                        case 2:
                            Timestamp startDate = Timestamp.valueOf(searchStartDate.getValue().atStartOfDay());
                            Timestamp endDate = Timestamp.valueOf(searchEndDate.getValue().plusDays(1).atStartOfDay());
                            data = FXCollections.observableArrayList(StaffDAO.getInstance().searchStaffByCreatedTime(startDate, endDate));
                            break;
                        case 3:
                        case 4:
                            String value = searchConditionChoiceBox.getSelectionModel().getSelectedItem();
                            data = FXCollections.observableArrayList(StaffDAO.getInstance().searchStaff(searchType, value));
                            break;
                    }
                    Platform.runLater(() -> {
                        staffTable.setItems(data);
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
            loader.setLocation(getClass().getClassLoader().getResource("view/staff/editstaff.fxml"));
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot);

            stage.setTitle("Thêm nhân viên");
            stage.setScene(scene);
            stage.setResizable(false);

            EditStaffController editStaffController = loader.getController();
            editStaffController.init();

            stage.showAndWait();
            reloadData();

        } catch (IOException e) {
            ExHandler.handle(e);
        }

    }

    public void delete() {
        Staff focusedStaff = staffTable.getSelectionModel().getSelectedItem();

        if (focusedStaff == null) {
            ExHandler.handle(new RuntimeException("Bạn chưa chọn nhân viên nào."));
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xoá");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xoá?");
        confirmAlert.setContentText("Bạn đang thực hiện xoá Nhân viên ID " + focusedStaff.getSid() + " - " + focusedStaff.getName() + ".");
        confirmAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> choice = confirmAlert.showAndWait();

        if (choice.get() == ButtonType.OK) {
            try {
                if (StaffDAO.getInstance().deleteStaff(focusedStaff)) {
                    Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                    resultAlert.setTitle("Kết quả xoá");
                    resultAlert.setHeaderText("Xoá thành công!");
                    resultAlert.setContentText("Đã xoá nhân viên ID " + focusedStaff.getSid() + " - " + focusedStaff.getName() + " thành công.");
                    resultAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    resultAlert.showAndWait();
                    reloadData();
                } else {
                    Alert resultAlert = new Alert(Alert.AlertType.ERROR);
                    resultAlert.setTitle("Kết quả xoá");
                    resultAlert.setHeaderText("Xoá thất bại!");
                    resultAlert.setContentText("Nhân viên ID " + focusedStaff.getSid() + " - " + focusedStaff.getName() + " chưa xoá khỏi CSDL.");
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
        Staff focusedStaff = staffTable.getSelectionModel().getSelectedItem();

        if (focusedStaff == null) {
            ExHandler.handle(new RuntimeException("Bạn chưa chọn nhân viên nào."));
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("view/staff/editstaff.fxml"));
        try {
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot);
            stage.setTitle("Sửa nhân viên");
            stage.setScene(scene);
            stage.setResizable(false);
            EditStaffController editStaffController = loader.getController();
            editStaffController.init(focusedStaff);

            stage.showAndWait();
        } catch (IOException e) {
            ExHandler.handle(e);
        }

        reloadData();
    }

    public void export() {

    }
}

