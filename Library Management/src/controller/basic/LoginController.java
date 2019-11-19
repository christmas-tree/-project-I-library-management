/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.basic;

import dao.UserDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.User;
import util.DbConnection;
import util.ExHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBtn;
    @FXML
    private Label statusLabel;

    public void init() {
        Runnable checkDbCon = new Runnable() {
            @Override
            public void run() {
                Connection con = DbConnection.getConnection();
                if (con == null) {
                    Platform.runLater(() -> {
                        ExHandler.handle(new Exception("Lỗi kết nối tới cơ sở dữ liệu! Chương trình sẽ thoát."));
                        System.exit(0);
                    });
                } else {
                    Platform.runLater(() -> statusLabel.setText("Trình trạng CSDL: Đã kết nối"));
                    try {
                        con.close();
                    } catch (SQLException e) {
                        ExHandler.handle(e);
                    }
                }
            }
        };
        new Thread(checkDbCon).start();
    }

    public void login(Event event) throws ClassNotFoundException, SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng nhập");
            alert.setHeaderText("Không được bỏ trống tên đăng nhập hoặc mật khẩu!");
            alert.setContentText("Vui lòng kiểm tra lại thông tin đăng nhập.");
            alert.showAndWait();
            return;
        }

        UserDAO userDAO = UserDAO.getInstance();

        try {
            User user = userDAO.authenticate(username, password);

            if (user != null) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("view/basic/frame.fxml"));
                    Parent root = loader.load();

                    new JMetro(root, Style.LIGHT);
                    Stage stage = new Stage();
                    stage.setTitle("QLTV");
                    stage.setScene(new Scene(root, 450, 450));
                    stage.setMaximized(true);

                    IndexController indexController = loader.getController();
                    indexController.init(user);

                    stage.show();
                    ((Node)(event.getSource())).getScene().getWindow().hide();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi!");
                    alert.setHeaderText("Có lỗi xảy ra!");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    System.out.println(e.getMessage());
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi đăng nhập!");
                alert.setHeaderText("Tên đăng nhập hoặc mật khẩu sai!");
                alert.setContentText("Vui lòng kiểm tra lại thông tin đăng nhập.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi CSDL");
            alert.setHeaderText("Có lỗi xảy ra trong quá trình đăng nhập: ");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }
}
