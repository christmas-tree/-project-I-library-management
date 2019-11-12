/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package util;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class ExHandler {

    public static void handle(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Có lỗi xảy ra!");
        alert.setHeaderText("Có lỗi xảy ra!");
        alert.setContentText(e.getMessage());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
        e.printStackTrace();
    }
}
