/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.transaction;

import dao.BookDAO;
import dao.ReaderDAO;
import dao.TransactionDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import util.ExHandler;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EditTransactionController {

    @FXML
    private AnchorPane editTransactionAP;

    @FXML
    private TextField sidTextField;

    @FXML
    private TextField ridTextField;

    @FXML
    private ComboBox<Staff> snameComboBox;

    @FXML
    private Button deleteDetailBtn;

    @FXML
    private TextField borrowDateTextField;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button addDetailBtn;

    @FXML
    private Button confirmBtn;

    @FXML
    private TextField depositSumTextField;

    @FXML
    private TextField transactIdTextField;

    @FXML
    private ComboBox<Reader> rnameComboBox;

    @FXML
    private TextField dueDateTextField;

    @FXML
    private Button printBtn;

    @FXML
    private TableView<Transaction.TransactionDetail> detailTableView;

    @FXML
    private TableColumn<Transaction.TransactionDetail, String> indexCol;

    @FXML
    private TableColumn<Transaction.TransactionDetail, String> bidCol;

    @FXML
    private TableColumn<Transaction.TransactionDetail, Book> bnameCol;

    @FXML
    private TableColumn<Transaction.TransactionDetail, Timestamp> returnDateCol;

    @FXML
    private TableColumn<Transaction.TransactionDetail, Staff> returnStaffCol;

    @FXML
    private TableColumn<Transaction.TransactionDetail, Long> depositCol;

    @FXML
    private TableColumn<Transaction.TransactionDetail, Boolean> isExtendedCol;

    @FXML
    private TableColumn<Transaction.TransactionDetail, Long> fineCol;

    @FXML
    private TextField newDepositTextField;

    @FXML
    private ComboBox<Book> newBookComboBox;

    @FXML
    private Button returnAllBtn;

    @FXML
    private Button returnBtn;

    // VARIABLES

    private User currentUser;
    private Transaction transaction;
    private ArrayList<Transaction.TransactionDetail> pendingDelete = new ArrayList<>();
    private ObservableList<Book> books;

    // UI INITIALIZE

    public void init(User currentUser) {
        this.currentUser = currentUser;

        transaction = new Transaction();

        uiInit();

        returnBtn.setVisible(false);
        returnAllBtn.setVisible(false);

        snameComboBox.getSelectionModel().select(currentUser);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.add(Calendar.MONTH, 2);
        Timestamp due = new Timestamp(cal.getTime().getTime());
        borrowDateTextField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(now));
        dueDateTextField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(due));

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

    public void init(User currentUser, Transaction passedTransaction) {
        this.currentUser = currentUser;
        try {
            this.transaction = TransactionDAO.getInstance().getTransaction(passedTransaction.getTransactId());
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        uiInit();

        System.out.println(transaction.getTransactId());
        transactIdTextField.setText(String.valueOf(transaction.getTransactId()));
        ridTextField.setText(String.valueOf(transaction.getBorrower().getRid()));
        rnameComboBox.getSelectionModel().select(transaction.getBorrower());
        sidTextField.setText(String.valueOf(transaction.getBorrowStaff().getSid()));
        snameComboBox.getSelectionModel().select(transaction.getBorrowStaff());
        borrowDateTextField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(transaction.getBorrowingDate()));
        dueDateTextField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(transaction.getDueDate()));

        int size = transaction.getAllDetails().size();
        long depositSum = 0;
        for (int i = 0; i < size; i++) {
            depositSum += transaction.getAllDetails().get(i).getDeposit();
        }
        depositSumTextField.setText(String.format("%,d",depositSum));

        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (validate()) {
                    update();
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                }
            }
        });
    }

    public void uiInit() {

        // COMBOBOX VALUES INITIALIZE

        try {
            rnameComboBox.getItems().addAll(ReaderDAO.getInstance().getAllReaders());
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        try {
            books = BookDAO.getInstance().getAllBooks();
        } catch (SQLException e) {
            ExHandler.handle(e);
        }
        newBookComboBox.getItems().addAll(books);

        // TABLE INITIALIZE

        indexCol.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if (isEmpty() || index < 0)
                    setText(null);
                else
                    setText(Integer.toString(index+1));
            }
        });

        bidCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getBook().getBid()));
        bnameCol.setCellValueFactory(new PropertyValueFactory<>("book"));

        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        returnStaffCol.setCellValueFactory(new PropertyValueFactory<>("returnStaff"));

        depositCol.setCellValueFactory(new PropertyValueFactory<>("deposit"));
        depositCol.setCellFactory(p -> {
            TextFieldTableCell<Transaction.TransactionDetail, Long> cell = new TextFieldTableCell<>();
            cell.setConverter(new StringConverter<>() {
                @Override
                public String toString(Long deposit) {
                    return String.format("%,d", deposit);
                }
                @Override
                public Long fromString(String string) {
                    return Long.parseLong(string.replaceAll("[^\\d]", ""));
                }
            });
            return cell;
        });

        depositCol.setOnEditCommit((event) -> {
            TablePosition<Transaction.TransactionDetail, Long> pos = event.getTablePosition();
            Long newDeposit = event.getNewValue();
            int row = pos.getRow();
            Transaction.TransactionDetail detail = event.getTableView().getItems().get(row);
            detail.setDeposit(newDeposit);
        });

        isExtendedCol.setCellValueFactory(p -> {
            Transaction.TransactionDetail detail = p.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(detail.isExtended());
            booleanProp.addListener((observable, oldValue, newValue) -> detail.setExtended(newValue));
            return booleanProp;
        });
        isExtendedCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Transaction.TransactionDetail, Boolean> call(TableColumn<Transaction.TransactionDetail, Boolean> p) {
                CheckBoxTableCell<Transaction.TransactionDetail, Boolean> cell = new CheckBoxTableCell<>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });


        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
        fineCol.setCellFactory(p -> {
            TextFieldTableCell<Transaction.TransactionDetail, Long> cell = new TextFieldTableCell<>();
            cell.setConverter(new StringConverter<>() {
                @Override
                public String toString(Long fineCol) {
                    return String.format("%,d", fineCol);
                }
                @Override
                public Long fromString(String string) {
                    return Long.parseLong(string.replaceAll("[^\\d]", ""));
                }
            });
            return cell;
        });
        fineCol.setOnEditCommit((event) -> {
            TablePosition<Transaction.TransactionDetail, Long> pos = event.getTablePosition();
            Long newFine = event.getNewValue();
            int row = pos.getRow();
            Transaction.TransactionDetail detail = event.getTableView().getItems().get(row);
            detail.setFine(newFine);
        });

        detailTableView.setItems(transaction.getAllDetails());

        // FIELDS LISTENERS

        newDepositTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                newDepositTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        transaction.getAllDetails().addListener(new ListChangeListener<Transaction.TransactionDetail>() {
            @Override
            public void onChanged(Change<? extends Transaction.TransactionDetail> change) {
                int size = transaction.getAllDetails().size();
                long depositSum = 0;
                for (int i = 0; i < size; i++) {
                    depositSum += transaction.getAllDetails().get(i).getDeposit();
                }
                depositSumTextField.setText(String.format("%,d",depositSum));
            }
        });

        // BUTTON ACTIONS

        addDetailBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (validateDetail()) {
                    Transaction.TransactionDetail newDetail = transaction.addDetail(newBookComboBox.getSelectionModel().getSelectedItem(), Long.parseLong(newDepositTextField.getText()));
                    Book book = newDetail.getBook();
                    book.setAvailQuantity(book.getAvailQuantity() - 1);
                }
            }
        });

        deleteDetailBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Transaction.TransactionDetail toBeDeletedDetail = detailTableView.getSelectionModel().getSelectedItem();
                if (toBeDeletedDetail.getReturnDate() != null)
                    ExHandler.handle(new Exception("Không thể xoá sách đã trả."));
                else {
                    pendingDelete.add(toBeDeletedDetail);
                    transaction.getAllDetails().remove(toBeDeletedDetail);
                    Book book = toBeDeletedDetail.getBook();
                    book.setAvailQuantity(book.getAvailQuantity() + 1);
                    detailTableView.getSelectionModel().clearSelection();
                }
            }
        });

        returnBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Transaction.TransactionDetail detail = detailTableView.getSelectionModel().getSelectedItem();
                if (detail == null) {
                    ExHandler.handle(new RuntimeException("Bạn chưa chọn chi tiết mượn trả nào."));
                } else {
                    detail.setReturnStaff(currentUser);
                    detail.setReturnDate(new Timestamp(System.currentTimeMillis()));
                    Book book = detail.getBook();
                    book.setAvailQuantity(book.getAvailQuantity() + 1);
                    detailTableView.refresh();
                }
            }
        });

        returnAllBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                transaction.getAllDetails().forEach(detail -> {
                    if (detail.getReturnDate() == null) {
                        detail.setReturnStaff(currentUser);
                        detail.setReturnDate(new Timestamp(System.currentTimeMillis()));
                        Book book = detail.getBook();
                        book.setAvailQuantity(book.getAvailQuantity() + 1);
                    }
                });
                detailTableView.refresh();
            }
        });

        printBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xuất phiếu");
                alert.setHeaderText("Lưu thay đổi?");
                alert.setContentText("Dữ liệu cần được lưu trước khi xuất phiếu.\nLưu dữ liệu?");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    confirmBtn.fire();
                    export();
                }
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }
        });

        // COMBOBOX LISTENERS

        snameComboBox.valueProperty().addListener(new ChangeListener<Staff>() {
            @Override
            public void changed(ObservableValue<? extends Staff> observableValue, Staff staff, Staff t1) {
                sidTextField.setText(String.valueOf(t1.getSid()));
            }
        });

        rnameComboBox.valueProperty().addListener(new ChangeListener<Reader>() {
            @Override
            public void changed(ObservableValue<? extends Reader> observableValue, Reader reader, Reader t1) {
                ridTextField.setText(String.valueOf(t1.getRid()));
            }
        });
        newBookComboBox.valueProperty().addListener(new ChangeListener<Book>() {
            @Override
            public void changed(ObservableValue<? extends Book> observableValue, Book book, Book t1) {
                newDepositTextField.setText(String.valueOf(t1.getPrice()*50/100));
            }
        });
    }

    //// END OF UI INITIALIZATION

    public void update() {
        transaction.setBorrower(rnameComboBox.getSelectionModel().getSelectedItem());
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            java.util.Date parsedDate = dateFormat.parse(dueDateTextField.getText());
            transaction.setDueDate(new java.sql.Timestamp(parsedDate.getTime()));
        } catch (Exception e) {
            ExHandler.handle(e);
        }

        boolean success = false;

        try {
            success = TransactionDAO.getInstance().update(transaction);
            success = TransactionDAO.getInstance().deleteDetails(transaction.getTransactId(), pendingDelete);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        transaction.getAllDetails().forEach(transactionDetail -> {
            try {
                BookDAO.getInstance().updateBookAvailQuantity(transactionDetail.getBook());
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        pendingDelete.forEach(transactionDetail -> {
            try {
                BookDAO.getInstance().updateBookAvailQuantity(transactionDetail.getBook());
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        if (success) {
            System.out.println("Sucessfully updated Transaction " + transaction.getTransactId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Cập nhật thông tin mượn trả thành công");
            alert.setContentText("Phiếu mượn trả ID " + transaction.getTransactId() + " đã được cập nhật thành công.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    public void add() {

        transaction.setBorrower(rnameComboBox.getSelectionModel().getSelectedItem());
        transaction.setBorrowStaff(snameComboBox.getSelectionModel().getSelectedItem());
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            java.util.Date parsedDate = dateFormat.parse(borrowDateTextField.getText());
            transaction.setBorrowingDate(new java.sql.Timestamp(parsedDate.getTime()));
        } catch (Exception e) {
            ExHandler.handle(e);
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            java.util.Date parsedDate = dateFormat.parse(dueDateTextField.getText());
            transaction.setDueDate(new java.sql.Timestamp(parsedDate.getTime()));
        } catch (Exception e) {
            ExHandler.handle(e);
        }

        boolean success = false;

        try {
            success = TransactionDAO.getInstance().createTransaction(transaction);
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        transaction.getAllDetails().forEach(transactionDetail -> {
            try {
                BookDAO.getInstance().updateBookAvailQuantity(transactionDetail.getBook());
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        });

        if (success) {
            System.out.println("Sucessfully added new transaction ID " + transaction.getTransactId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Thêm mượn trả thành công");
            alert.setContentText("Phiếu mượn trả ID " + transaction.getTransactId() + " đã được thêm thành công.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    // INPUT VALIDATION METHODS

    public boolean validate() {
        String err = "";
        Reader reader = rnameComboBox.getSelectionModel().getSelectedItem();

        if (reader == null)
            err += "Không được bỏ trống độc giả.\n";
        else {
            if (!reader.isCanBorrow())
                err += "Độc giả " + reader.getName() + " không được phép mượn.\n";
            else {
                if (dueDateTextField.getText().isBlank())
                    err += "Không được bỏ trống hạn trả.\n";
                if (transaction.getAllDetails().size() == 0)
                    err += "Chưa có sách nào được mượn.\n";
            }
        }

        if (err.equals("")) {
            return true;
        } else {
            ExHandler.handle(new Exception(err));
            return false;
        }
    }

    public boolean validateDetail() {
        String err = "";
        Book newBook = newBookComboBox.getSelectionModel().getSelectedItem();
        if (newBook == null) {
            err += "Chưa có thông tin sách.\n";
        } else if (newBook.getAvailQuantity() < 1) {
            err += "Số lượng sách còn lại là 0 - Không thể mượn.\n";
        } else {
            int size = transaction.getAllDetails().size();
            for (int i = 0; i < size; i++) {
                if (transaction.getAllDetails().get(i).getBook().equals(newBook)) {
                    err += "Sách đã có trong danh sách mượn.\n";
                    break;
                }
            }
        }
        if (newDepositTextField.getText().isBlank()) {
            err += "Không được bỏ trống tiền cọc.\n";
        }
        if (err.equals("")) {
            return true;
        } else {
            ExHandler.handle(new Exception(err));
            return false;
        }
    }

    // EXPORT

    public boolean export() {
        XWPFDocument doc;
        System.out.println(System.getProperty("user.dir"));
        // CURRENT FOLDER = E:\HUST\IT3150 Project I - CNPM\Quan ly thu vien\Library Management

        try {
            doc = new XWPFDocument(OPCPackage.open("src/resources/form/PhieuMuonTra.docx"));
        } catch (InvalidFormatException | IOException e) {
            ExHandler.handle(e);
            return false;
        }

        // INFO TABLE
        XWPFTable basicInfoTable = doc.getTables().get(1);

        replaceCellText(basicInfoTable.getRow(0).getCell(1), String.valueOf(transaction.getTransactId()));

        String rid = String.valueOf(transaction.getBorrower().getRid());
        while (rid.length() < 6) rid = "0" + rid;
        replaceCellText(basicInfoTable.getRow(1).getCell(1), rid);
        replaceCellText(basicInfoTable.getRow(1).getCell(3), transaction.getBorrower().getName());

        String sid1 = String.valueOf(transaction.getBorrowStaff().getSid());
        while (sid1.length() < 6) sid1 = "0" + sid1;
        replaceCellText(basicInfoTable.getRow(2).getCell(1), sid1);
        replaceCellText(basicInfoTable.getRow(2).getCell(3), transaction.getBorrowStaff().getName());

        replaceCellText(basicInfoTable.getRow(3).getCell(1), borrowDateTextField.getText());
        replaceCellText(basicInfoTable.getRow(3).getCell(3), dueDateTextField.getText());

        replaceCellText(basicInfoTable.getRow(4).getCell(1), depositSumTextField.getText());

        // SIGNATURE TABLE
        XWPFTable signatureTable = doc.getTables().get(3);

        LocalDate now = LocalDate.now();

        replaceCellText(signatureTable.getRow(0).getCell(1), "Ngày "+now.getDayOfMonth()+" tháng "+now.getMonthValue()+" năm "+now.getYear());
        replaceCellText(signatureTable.getRow(2).getCell(0), transaction.getBorrower().getName());
        replaceCellText(signatureTable.getRow(2).getCell(1), currentUser.getName());

        // DETAILS TABLE
        XWPFTable detailsTable = doc.getTables().get(2);
        int size = transaction.getAllDetails().size();
        for (int i = 0; i < size; i++) {
            Transaction.TransactionDetail detail = transaction.getAllDetails().get(i);
            int j = i+1;
            replaceCellText(detailsTable.getRow(j).getCell(0), String.valueOf(j));
            replaceCellText(detailsTable.getRow(j).getCell(1), detail.getBook().getBid());
            replaceCellText(detailsTable.getRow(j).getCell(2), detail.getBook().getBookName());
            replaceCellText(detailsTable.getRow(j).getCell(3), (detail.getReturnDate()==null)?"":new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(detail.getReturnDate()));
            replaceCellText(detailsTable.getRow(j).getCell(4), (detail.getReturnStaff()==null)?"":detail.getReturnStaff().getName());
            replaceCellText(detailsTable.getRow(j).getCell(5), String.format("%,d", detail.getDeposit()));
            replaceCellText(detailsTable.getRow(j).getCell(6), detail.isExtended()?"Có":"Không");
            replaceCellText(detailsTable.getRow(j).getCell(7), String.format("%,d", detail.getFine()));

            createNewRowWithFormat(detailsTable);
        }

        detailsTable.removeRow(1);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn vị trí lưu.");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Word Files", "*.docx")
        );
        fileChooser.setInitialFileName("PhieuMuonTra - " + now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".docx");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFile = fileChooser.showSaveDialog(editTransactionAP.getScene().getWindow());

        try {
            FileOutputStream file = new FileOutputStream(selectedFile);
            doc.write(file);
            file.close();
            Desktop.getDesktop().open(selectedFile);
            return true;
        } catch (IOException e) {
            ExHandler.handle(e);
            return false;
        }
    }

    public static void replaceCellText(XWPFTableCell cell, String replacement) {
        cell.getParagraphs().forEach(paragraph -> {
            // REMOVE RUNS EXCEPT THE 1st
            int size = paragraph.getRuns().size() - 1;
            if (size >= 0)
                for (int i = size; i > 0; i--)
                    paragraph.removeRun(i);
            else
                paragraph.createRun();
            paragraph.getRuns().get(0).setText(replacement, 0);
        });
    }

    public static void createNewRowWithFormat(XWPFTable table) {
        XWPFTableRow lastRow = table.getRows().get(table.getNumberOfRows() - 1);
        CTRow ctrow;
        try {
            ctrow = CTRow.Factory.parse(lastRow.getCtRow().newInputStream());
            table.addRow(new XWPFTableRow(ctrow, table));

        } catch (XmlException | IOException e) {
            ExHandler.handle(e);
        }
    }
}
