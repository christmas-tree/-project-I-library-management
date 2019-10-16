package controller;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import util.AlertPanel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class Login {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBtn;

    public void login() throws SQLException, ClassNotFoundException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        UserDAO userDao = new UserDAO();

//        try {
            User user = userDao.authenticate(username, password);

            if (user != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Login successful!");
                alert.setHeaderText("Login successful!");
                alert.setContentText("You are now logged in.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login failed!");
                alert.setHeaderText("Login failed!");
                alert.setContentText("You are NOT logged in.");
                alert.showAndWait();
            }

//        } catch (SQLException | ClassNotFoundException ex) {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            ex.printStackTrace(pw);
//            String exceptionText = sw.toString();
//            AlertPanel.createExpandable("Co loi xay ra", "Co loi da xay ra", "Thong tin chi tiet loi:", exceptionText);
//            System.out.println(exceptionText);
//        }
    }

}
