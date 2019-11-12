/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.basic;

import controller.book.SearchBookController;
import controller.reader.EditReaderController;
import controller.reader.SearchReaderController;
import controller.staff.SearchStaffController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import model.Reader;
import model.User;
import util.ExHandler;

import java.util.Optional;

public class IndexController {

    @FXML
    private BorderPane window;

    @FXML
    private Label footerNote;

    @FXML
    private Menu menuEdit;

    @FXML
    private Menu menuFile;

    @FXML
    private Menu menuHelp;

    @FXML
    private TreeView sideMenu;

    //Data
    public static ObservableList<Reader> readerList = FXCollections.observableArrayList();

//    public static void main(String[] args) {

        // add a listener to the ObservableList
//        observableList.addListener(new ListChangeListener<Double>() {
//            @Override
//            public void onChanged(Change<? extends Double> c) {
//                // c represents the changed element
//                System.out.println("Added " + c + " to the Observablelist");
//                // we add the last element added to the observable list to the arraylist
//                arrayList.add(observableList.get(observableList.size()-1));
//                System.out.println("Added " + arrayList.get(arrayList.size()-1) + " to the Arraylist");
//            }
//        });

    public void init(User user) {

        TreeItem rootItem = new TreeItem("Menu");

        TreeItem transactMenu = new TreeItem("Quản lý mượn trả");
        transactMenu.getChildren().add(new TreeItem("Tìm kiếm giao dịch"));
        transactMenu.getChildren().add(new TreeItem("Thêm giao dịch"));
        rootItem.getChildren().add(transactMenu);

        TreeItem bookMenu = new TreeItem("Quản lý sách");
        bookMenu.getChildren().add(new TreeItem("Tìm kiếm sách"));
        bookMenu.getChildren().add(new TreeItem("Thêm sách"));
        bookMenu.getChildren().add(new TreeItem("Thể loại"));
        bookMenu.getChildren().add(new TreeItem("Nhà xuất bản"));
        bookMenu.getChildren().add(new TreeItem("Ngôn ngữ"));
        rootItem.getChildren().add(bookMenu);

        TreeItem readerMenu = new TreeItem("Quản lý độc giả");
        readerMenu.getChildren().add(new TreeItem("Tìm kiếm độc giả"));
        readerMenu.getChildren().add(new TreeItem("Thêm độc giả"));
        rootItem.getChildren().add(readerMenu);

        if (user.isAdmin()) {
            TreeItem staffMenu = new TreeItem("Quản lý nhân viên");
            staffMenu.getChildren().add(new TreeItem("Tìm kiếm nhân viên"));
            staffMenu.getChildren().add(new TreeItem("Thêm nhân viên"));
            rootItem.getChildren().add(staffMenu);
        }

        sideMenu.setRoot(rootItem);

        sideMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            Node node = event.getPickResult().getIntersectedNode();

            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                String option = (String) ((TreeItem)sideMenu.getSelectionModel().getSelectedItem()).getValue();
                System.out.println("Đã chọn: " + option);
                renderMainScene(option);
            }
        });

    }

    public void exit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Thoát chương trình");
        alert.setHeaderText("Bạn chắc chắn muốn thoát?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK)
            System.exit(0);
    }

    public void renderMainScene(String option) {
        FXMLLoader loader = new FXMLLoader();
        switch (option) {
            // Quan ly giao dich
            case "Tìm kiếm giao dịch":
                break;

            // Quan ly sach
            case "Tìm kiếm sách":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/book/searchbook.fxml"));
                    window.setCenter(loader.load());
                    SearchBookController searchBookController = loader.getController();
                    searchBookController.init();
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
                break;
            case "Thêm sách":
                break;
            case "Thể loại":
                break;
            case "Nhà xuất bản":
                break;
            case "Ngôn ngữ":
                break;

            // Quan ly doc gia
            case "Tìm kiếm độc giả":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/reader/searchreader.fxml"));
                    window.setCenter(loader.load());
                    SearchReaderController searchReaderController = loader.getController();
                    searchReaderController.init();
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
                break;
            case "Thêm độc giả":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/reader/editreader.fxml"));
                    window.setCenter(loader.load());
                    EditReaderController editReaderController = loader.getController();
                    editReaderController.init();
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
                break;

            // Quan ly nhan vien
            case "Tìm kiếm nhân viên":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/staff/searchstaff.fxml"));
                    window.setCenter(loader.load());
                    SearchStaffController searchStaffController = loader.getController();
                    searchStaffController.init();
                } catch (Exception e) {
                    ExHandler.handle(e);
                }
            case "Thêm nhân viên":
                break;
        }
    }

}


