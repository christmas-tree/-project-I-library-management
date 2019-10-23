package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.AlertPanel;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/login.fxml"));
        primaryStage.setTitle("Libary Management");
        primaryStage.setScene(new Scene(root, 520, 320));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
