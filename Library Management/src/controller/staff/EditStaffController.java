/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.staff;

import dao.StaffDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import model.Staff;
import util.ExHandler;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class EditStaffController {

    @FXML
    private TextField sidTextField;

    @FXML
    private Button cancelBtn;

    @FXML
    private Label headerLabel;

    @FXML
    private TextField createdTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button confirmBtn;

    @FXML
    private ToggleButton genderToggle;

    @FXML
    private TextArea addressTextArea;

    @FXML
    private CheckBox isAdmin;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private TextField idCardTextField;

    @FXML
    private TextField usernameTextField;

    public void init() {
        uiInit();
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (validate()) {
                    if (passwordField.getText().isBlank()) {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Xác nhận");
                        confirm.setHeaderText("Mật khẩu trống!");
                        confirm.setContentText("Tài khoản nhân viên sẽ được tạo với mật khẩu mặc định: \"123456\".");
                        confirm.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        confirm.showAndWait();
                        if (confirm.getResult() != ButtonType.OK) {
                            System.out.println();
                            return;
                        }
                    }
                    add();
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                }
            }
        });
    }

    public void init(Staff staff) {

        uiInit();

        headerLabel.setText("Sửa nhân viên");
        sidTextField.setText(String.format("%06d", staff.getSid()));
        createdTextField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(staff.getSid()));
        nameTextField.setText(staff.getName());
        dobDatePicker.setValue(staff.getDob().toLocalDate());
        genderToggle.setSelected(staff.getGender());
        idCardTextField.setText(String.valueOf(staff.getIdCardNum()));
        addressTextArea.setText(staff.getAddress());
        isAdmin.setSelected(staff.isAdmin());
        usernameTextField.setText(staff.getUsername());

        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (validate()) {
                    if (!passwordField.getText().isBlank()) {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Xác nhận");
                        confirm.setHeaderText("Ghi đè mật khẩu?");
                        confirm.setContentText("Bạn sẽ ghi đè mật khẩu của nhân viên này.");
                        confirm.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        confirm.showAndWait();
                        if (confirm.getResult() != ButtonType.OK) {
                            return;
                        }
                    }
                    update(staff);
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                }
            }
        });
    }

    public void uiInit() {
        genderToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (t1) {
                    genderToggle.setText("Nam");
                } else {
                    genderToggle.setText("Nữ");
                }
            }
        });

        idCardTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    idCardTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        usernameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                usernameTextField.setText(newValue.replaceAll("[\\s*]", ""));
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }
        });
    }

    public void update(Staff staff) {

        staff.setName(nameTextField.getText());
        staff.setDob(Date.valueOf(dobDatePicker.getValue()));
        staff.setGender(genderToggle.isSelected());
        if (!idCardTextField.getText().isBlank())
            staff.setIdCardNum(Long.parseLong(idCardTextField.getText()));
        staff.setAddress(addressTextArea.getText());
        staff.setUsername(usernameTextField.getText());
        if (!passwordField.getText().isBlank())
            staff.setPassword(passwordField.getText());
        staff.setAdmin(isAdmin.isSelected());

        boolean success = false;

        try {
            success = StaffDAO.getInstance().updateStaff(staff);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        if (success) {
            System.out.println("Sucessfully updated SID " + staff.getSid());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Cập nhật nhân viên thành công");
            alert.setContentText("Nhân viên SID " + staff.getSid() + ": " + staff.getName() + " đã được cập nhật thành công vào cơ sở dữ liệu.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    public void add() {
        Staff staff = new Staff();

        staff.setCreated(new Timestamp(System.currentTimeMillis()));
        staff.setName(nameTextField.getText());
        staff.setDob(Date.valueOf(dobDatePicker.getValue()));
        staff.setGender(genderToggle.isSelected());
        if (!idCardTextField.getText().isBlank())
            staff.setIdCardNum(Long.parseLong(idCardTextField.getText()));
        staff.setAddress(addressTextArea.getText());
        staff.setUsername(usernameTextField.getText());
        if (passwordField.getText().isBlank())
            staff.setPassword("123456");
        else
            staff.setPassword(passwordField.getText());
        staff.setAdmin(isAdmin.isSelected());

        boolean success = false;

        try {
            success = StaffDAO.getInstance().createStaff(staff);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        if (success) {
            System.out.println("Sucessfully added new user: " + staff.getName());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Thêm nhân viên thành công");
            alert.setContentText("Nhân viên " + staff.getName() + " đã được thêm thành công vào cơ sở dữ liệu.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    public boolean validate() {
        String err = "";

        if (nameTextField.getText().isBlank()) {
            err += "Không được bỏ trống họ tên.\n";
        }
        if (dobDatePicker.getValue() == null) {
            err += "Không được bỏ trống ngày sinh.\n";
        }
        if (usernameTextField.getText().isBlank()) {
            err += "Không được bỏ trống tên đăng nhập.\n";
        }
        if (err.equals("")) {
            return true;
        } else {
            ExHandler.handle(new Exception(err));
            return false;
        }
    }
}
