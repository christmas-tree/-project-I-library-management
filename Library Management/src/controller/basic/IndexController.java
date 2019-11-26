/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.basic;

import controller.book.MetaController;
import controller.book.ReportBookController;
import controller.book.SearchBookController;
import controller.reader.EditReaderController;
import controller.reader.SearchReaderController;
import controller.staff.SearchStaffController;
import controller.transaction.SearchTransactionController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.Reader;
import model.User;
import util.ExHandler;

import java.io.IOException;
import java.util.Optional;

public class IndexController {

    @FXML
    private BorderPane window;

    @FXML
    private TreeView sideMenu;

    @FXML
    private MenuItem exitMenu;

    @FXML
    private MenuItem logOutMenu;

    @FXML
    public MenuItem editMenu;

    @FXML
    public MenuItem addMenu;

    @FXML
    public MenuItem deleteMenu;

    @FXML
    public MenuItem exportMenu;

    @FXML
    private MenuItem aboutMenu;

    public User currentUser;

    public void init(User user) {

        this.currentUser = user;

        TreeItem rootItem = new TreeItem("Menu");

        TreeItem transactMenu = new TreeItem("Quản lý mượn trả");
        transactMenu.getChildren().add(new TreeItem("Tìm kiếm giao dịch"));
        transactMenu.getChildren().add(new TreeItem("Thêm giao dịch"));
        rootItem.getChildren().add(transactMenu);
        transactMenu.setExpanded(true);


        TreeItem bookMenu = new TreeItem("Quản lý sách");
        bookMenu.getChildren().add(new TreeItem("Tìm kiếm sách"));
        bookMenu.getChildren().add(new TreeItem("Thông tin meta"));
        bookMenu.getChildren().add(new TreeItem("Thống kê"));
        rootItem.getChildren().add(bookMenu);
        bookMenu.setExpanded(true);

        TreeItem readerMenu = new TreeItem("Quản lý độc giả");
        readerMenu.getChildren().add(new TreeItem("Tìm kiếm độc giả"));
        readerMenu.getChildren().add(new TreeItem("Thêm độc giả"));
        rootItem.getChildren().add(readerMenu);
        readerMenu.setExpanded(true);

        if (currentUser.isAdmin()) {
            TreeItem staffMenu = new TreeItem("Quản lý nhân viên");
            staffMenu.getChildren().add(new TreeItem("Tìm kiếm nhân viên"));
            staffMenu.getChildren().add(new TreeItem("Thêm nhân viên"));
            rootItem.getChildren().add(staffMenu);
            staffMenu.setExpanded(true);
        }

        sideMenu.setRoot(rootItem);

        sideMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            Node node = event.getPickResult().getIntersectedNode();

            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                String option = (String) ((TreeItem)sideMenu.getSelectionModel().getSelectedItem()).getValue();
                renderMainScene(option);
            }
        });

        exitMenu.setOnAction(event-> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Thoát chương trình");
            alert.setHeaderText("Bạn chắc chắn muốn thoát?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK)
                System.exit(0);
        });

        aboutMenu.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Về ứng dụng");
            alert.setHeaderText("PHẦN MỀM QUẢN LÝ THƯ VIỆN");
            alert.setContentText("Trần Trung Nghĩa - 20173281");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        });

        editMenu.setDisable(true);
        exportMenu.setDisable(true);
        deleteMenu.setDisable(true);
        addMenu.setDisable(true);

        logOutMenu.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/basic/login.fxml"));
                Scene firstScene = new Scene(root, 1000, 600);
                firstScene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
                new JMetro(root, Style.DARK);
                Stage stage = new Stage();
                stage.setTitle("Đăng nhập - QLTV");
                stage.setScene(firstScene);
                stage.setResizable(false);
                stage.setScene(firstScene);
                window.getScene().getWindow().hide();
                stage.show();
            } catch (IOException e) {
                ExHandler.handle(e);
                System.exit(0);
            }
        });
    }

    public void renderMainScene(String option) {
        FXMLLoader loader = new FXMLLoader();
        switch (option) {
            // Quan ly giao dich
            case "Tìm kiếm giao dịch":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/transaction/searchtransaction.fxml"));
                    window.setCenter(loader.load());
                    SearchTransactionController searchTransactionController = loader.getController();
                    searchTransactionController.init(this);
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
                break;

            // Quan ly sach
            case "Tìm kiếm sách":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/book/searchbook.fxml"));
                    window.setCenter(loader.load());
                    SearchBookController searchBookController = loader.getController();
                    searchBookController.init(this);
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
                break;
            case "Thông tin meta":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/book/metadata.fxml"));
                    window.setCenter(loader.load());
                    MetaController metaController = loader.getController();
                    metaController.init(this);
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
                break;
            case "Thống kê":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/book/reportbook.fxml"));
                    window.setCenter(loader.load());
                    ReportBookController reportBookController = loader.getController();
                    reportBookController.init(this);
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
                break;

            // Quan ly doc gia
            case "Tìm kiếm độc giả":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/reader/searchreader.fxml"));
                    window.setCenter(loader.load());
                    SearchReaderController searchReaderController = loader.getController();
                    searchReaderController.init(this);
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
                break;
            case "Thêm độc giả":
                break;

            // Quan ly nhan vien
            case "Tìm kiếm nhân viên":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/staff/searchstaff.fxml"));
                    window.setCenter(loader.load());
                    SearchStaffController searchStaffController = loader.getController();
                    searchStaffController.init(this);
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
            case "Thêm nhân viên":
                break;
        }
    }

}


