/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller;

import controller.basic.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("view/basic/login.fxml"));
        Parent root = loader.load();
        Scene firstScene = new Scene(root, 1000, 600);
        firstScene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
        new JMetro(root, Style.DARK);
        primaryStage.setTitle("Đăng nhập - QLTV");
        primaryStage.setScene(firstScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/resources/icon/app-icon.png"));
        primaryStage.show();
        LoginController loginController = loader.getController();
        loginController.init();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
