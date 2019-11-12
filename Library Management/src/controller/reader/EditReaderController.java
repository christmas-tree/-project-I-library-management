/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.reader;

import dao.ReaderDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import model.Reader;
import util.ExHandler;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

public class EditReaderController {

    @FXML
    private Label headerLabel;

    @FXML
    private CheckBox canBorrowCheckBox;

    @FXML
    private Button confirmBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private TextField ridTextField;

    @FXML
    private TextField createdTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private TextField idCardTextField;

    @FXML
    private ToggleButton genderToggle;

    @FXML
    private TextArea addressTextArea;

    public void init() {
        uiInit();
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (validate()) {
                    add();
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                }
            }
        });
    }

    public void init(Reader reader) {

        uiInit();

        headerLabel.setText("Sửa độc giả");
        ridTextField.setText(String.valueOf(reader.getRid()));
        createdTextField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(reader.getCreated()));
        nameTextField.setText(reader.getName());
        dobDatePicker.setValue(reader.getDob().toLocalDate());
        genderToggle.setSelected(reader.getGender());
        idCardTextField.setText(String.valueOf(reader.getIdCardNum()));
        addressTextArea.setText(reader.getAddress());
        canBorrowCheckBox.setSelected(reader.isCanBorrow());

        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (validate()) {
                    update(reader);
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

        nameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue.matches("\\\\d+")) {
                    idCardTextField.setText(newValue.replaceAll("[\\d]", ""));
                }
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }
        });
    }

    public void update(Reader reader) {
        reader.setName(nameTextField.getText());
        reader.setDob(Date.valueOf(dobDatePicker.getValue()));
        reader.setGender(genderToggle.isSelected());
        reader.setIdCardNum((idCardTextField.getText() != "") ? Long.parseLong(idCardTextField.getText()) : null);
        reader.setAddress(addressTextArea.getText());
        reader.setCanBorrow(canBorrowCheckBox.isSelected());

        boolean success = false;

        try {
            success = ReaderDAO.getInstance().updateReader(reader);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        if (success) {
            System.out.println("Sucessfully updated RID " + reader.getRid());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Cập nhật độc giả thành công");
            alert.setContentText("Độc giả RID " + reader.getRid() + ": " + reader.getName() + " đã được cập nhật thành công vào cơ sở dữ liệu.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    public void add() {
        Reader reader = new Reader();

        reader.setCreated(new Timestamp(System.currentTimeMillis()));
        reader.setName(nameTextField.getText());
        reader.setDob(Date.valueOf(dobDatePicker.getValue()));
        reader.setGender(genderToggle.isSelected());
        reader.setIdCardNum((idCardTextField.getText() != "") ? Long.parseLong(idCardTextField.getText()) : null);
        reader.setAddress(addressTextArea.getText());
        reader.setCanBorrow(canBorrowCheckBox.isSelected());

        boolean success = false;

        try {
            success = ReaderDAO.getInstance().createReader(reader);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        if (success) {
            System.out.println("Sucessfully added new user: " + reader.getName());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Thêm độc giả thành công");
            alert.setContentText("Độc giả " + reader.getName() + " đã được thêm thành công vào cơ sở dữ liệu.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    public boolean validate() {
        String err = "";
        if (nameTextField.getText().equals("")) {
            err += "Không được bỏ trống họ tên.\n";
        }
        if (dobDatePicker.getValue() == null) {
            err += "Không được bỏ trống ngày sinh.\n";
        }
        if (err.equals("")) {
            return true;
        } else {
            ExHandler.handle(new Exception(err));
            return false;
        }
    }
}

