/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.staff;

import controller.basic.IndexController;
import dao.StaffDAO;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import model.Staff;
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
import java.util.Optional;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

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

        searchBtn.setOnAction(event -> search());

        editBtn.setOnAction(event -> edit());

        addBtn.setOnAction(event -> add());

        deleteBtn.setOnAction(event -> delete());

        refreshBtn.setOnAction(event -> refresh());

        c.addMenu.setDisable(false);
        c.deleteMenu.setDisable(false);
        c.editMenu.setDisable(false);
        c.exportMenu.setDisable(false);
//        c.backupMenu.setDisable(false);
        c.importMenu.setDisable(false);

        c.addMenu.setOnAction(event -> addBtn.fire());
        c.deleteMenu.setOnAction(event -> deleteBtn.fire());
        c.editMenu.setOnAction(event -> editBtn.fire());
        c.exportMenu.setOnAction(event -> export());
        // c.backupMenu.setOnAction(event -> backup());
        c.importMenu.setOnAction(event -> restore());
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

        searchChoiceBox.getSelectionModel().clearSelection();
        searchType = -1;

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
        if (data.size() == 0) {
            ExHandler.handle(new Exception("Không tìm thấy dữ liệu phù hợp với lựa chọn tìm kiếm."));
        } else {
            File file = new File("src/resources/form/DsNhanVien.xlsx");

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

            Staff staff;

            // DATE
            cell = sheet.getRow(1).getCell(6);
            cell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy")));
            cell.setCellStyle(dateStyle);

            // SEARCH TYPE
            if (searchType != -1) {
                String searchChoices[] = {"Mã nhân viên", "Tên", "Thời gian tạo", "Giới tính", "Vai trò", "Tên đăng nhập"};
                //                              0             1         2               3         4              5
                String searchInfoString = searchChoices[searchType];

                switch (searchType) {
                    case 0:
                        searchInfoString += ": " + searchInputField.getText();
                        break;
                    case 1:
                    case 5:
                        searchInfoString += " có chữ: " + searchInputField.getText();
                        break;
                    case 2:
                        searchInfoString += " từ " +
                                DateTimeFormatter.ofPattern("dd/MM/YYYY").format(searchStartDate.getValue()) + " đến " +
                                DateTimeFormatter.ofPattern("dd/MM/YYYY").format(searchEndDate.getValue());
                        break;
                    case 3:
                    case 4:
                        searchInfoString += ": " + searchConditionChoiceBox.getSelectionModel().getSelectedItem();
                        break;
                }

                cell = sheet.getRow(5).getCell(0);
                cell.setCellStyle(searchInfoStyle);
                cell.setCellValue(searchInfoString);
            }

            // DATA
            for (int i = 0; i < data.size(); i++) {
                staff = data.get(i);
                int row = 8 + i;
                sheet.createRow(row);

                cell = sheet.getRow(row).createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(1);
                cell.setCellValue(String.format("%06d", staff.getSid()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(2);
                cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(staff.getCreated()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(3);
                cell.setCellValue(staff.isAdmin() ? "Quản lý" : "Nhân viên");
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(4);
                cell.setCellValue(staff.getName());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(5);
                cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(staff.getDob()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(6);
                cell.setCellValue(staff.getGender() ? "Nam" : "Nữ");
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(7);
                cell.setCellValue(String.valueOf(staff.getIdCardNum()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(8);
                cell.setCellValue(staff.getAddress());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(9);
                cell.setCellValue(staff.getUsername());
                cell.setCellStyle(tableElementStyle);
            }

            // Ghi file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn vị trí lưu.");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialFileName("Thong Tin Nhan Vien " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedFile = fileChooser.showSaveDialog(staffTable.getScene().getWindow());

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
//        List<Staff> staffs;
//        Staff staff;
//
//        try {
//            staffs = StaffDAO.getInstance().getAllStaffs();
//        } catch (SQLException e) {
//            ExHandler.handle(e);
//            return;
//        }
//
//        XSSFWorkbook excelWorkBook = new XSSFWorkbook();
//        XSSFSheet sheet = excelWorkBook.createSheet();
//
//        String[] props = {"sid", "created", "isAdmin", "username", "name", "dob", "gender", "idCardNum", "address"};
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
//
//        for (int i = 0; i < data.size(); i++) {
//            int j = i + 1;
//
//            staff = staffs.get(i);
//            row = sheet.createRow(j);
//
//            row.createCell(0).setCellValue(staff.getSid());
//            row.createCell(1).setCellValue(staff.getCreated());
//            row.createCell(2).setCellValue(staff.isAdmin());
//            row.createCell(3).setCellValue(staff.getUsername());
//            row.createCell(4).setCellValue(staff.getName());
//            row.createCell(5).setCellValue(staff.getDob());
//            row.createCell(6).setCellValue(staff.getGender());
//            row.createCell(7).setCellValue(staff.getIdCardNum());
//            row.createCell(8).setCellValue(staff.getAddress());
//        }
//
//        // Ghi file
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Chọn vị trí lưu.");
//        fileChooser.getExtensionFilters().add(
//                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
//        );
//        fileChooser.setInitialFileName("StaffBackup " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.home/Desktop")));
//
//        File selectedFile = fileChooser.showSaveDialog(staffTable.getScene().getWindow());
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

    public void restore() {

        ArrayList<Staff> newStaffs = new ArrayList<>();
        Staff newStaff;

        XSSFWorkbook excelWorkBook;

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file.");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedFile = fileChooser.showOpenDialog(staffTable.getScene().getWindow());


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
        if (row.getLastCellNum() >= 8) {
            while (rows.hasNext()) {
                row = (XSSFRow) rows.next();
                newStaff = new Staff();

                newStaff.setAdmin(row.getCell(0, CREATE_NULL_AS_BLANK).getBooleanCellValue());
                newStaff.setUsername(row.getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue());
                newStaff.setPassword(row.getCell(2, CREATE_NULL_AS_BLANK).getStringCellValue());
                newStaff.setName(row.getCell(3, CREATE_NULL_AS_BLANK).getStringCellValue());
                newStaff.setDob(new Date(row.getCell(4, CREATE_NULL_AS_BLANK).getDateCellValue().getTime()));
                newStaff.setGender(row.getCell(5, CREATE_NULL_AS_BLANK).getBooleanCellValue());
                newStaff.setIdCardNum((long) row.getCell(6, CREATE_NULL_AS_BLANK).getNumericCellValue());
                newStaff.setAddress(row.getCell(7, CREATE_NULL_AS_BLANK).getStringCellValue());

                newStaffs.add(newStaff);
            }
        } else
            ExHandler.handle(new Exception("File không đúng định dạng." + row.getLastCellNum()));

        StaffDAO.getInstance().importStaff(newStaffs);
        refresh();
    }
}

