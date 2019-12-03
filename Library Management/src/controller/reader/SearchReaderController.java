package controller.reader;

import controller.basic.IndexController;
import dao.ReaderDAO;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.Reader;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import util.ExHandler;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

public class SearchReaderController {

    @FXML
    private AnchorPane searchReaderPane;

    @FXML
    private TableView<Reader> readerTable;

    @FXML
    private TableColumn<Reader, String> idCol;

    @FXML
    private TableColumn<Reader, String> statusCol;

    @FXML
    private TableColumn<Reader, String> nameCol;

    @FXML
    private TableColumn<Reader, Date> dobCol;

    @FXML
    private TableColumn<Reader, String> genderCol;

    @FXML
    private ChoiceBox searchChoiceBox;

    @FXML
    private Label dieuKienLabel;

    @FXML
    private ChoiceBox<String> searchConditionChoiceBox;

    @FXML
    private DatePicker searchStartDate;

    @FXML
    private DatePicker searchEndDate;

    @FXML
    private TextField searchInputField;

    @FXML
    private Button searchBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private ImageView refreshIcon;

    private ObservableList<Reader> data;

    private int searchType = -1;

    public void init(IndexController c) {

        idCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%06d", p.getValue().getRid())));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        genderCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<String>(p.getValue().getGender() ? "Nam" : "Nữ"));
        statusCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().isCanBorrow() ? "Được mượn" : "Không được mượn"));

        idCol.setSortType(TableColumn.SortType.ASCENDING);

        reloadData();

        String searchChoices[] = {"Mã độc giả", "Tên", "Thời gian tạo", "Giới tính", "Trạng thái"};

        searchChoiceBox.getItems().addAll(searchChoices);

        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, numPre, numPost) -> {
            dieuKienLabel.setVisible(true);

            searchInputField.setVisible(false);
            searchBtn.setVisible(false);
            searchConditionChoiceBox.setVisible(false);
            searchStartDate.setVisible(false);
            searchEndDate.setVisible(false);

            switch (numPost.intValue()) {
                case 0:
                    searchType = 0;
                    searchInputField.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 620.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 1:
                    searchType = 1;
                    searchInputField.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 620.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 2:
                    searchType = 2;
                    searchStartDate.setVisible(true);
                    searchEndDate.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 670.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 3:
                    searchType = 3;
                    searchConditionChoiceBox.getItems().clear();
                    searchConditionChoiceBox.getItems().addAll("Nam", "Nữ");
                    searchConditionChoiceBox.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 520.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
                case 4:
                    searchType = 4;
                    searchConditionChoiceBox.getItems().clear();
                    searchConditionChoiceBox.getItems().addAll("Được mượn", "Không được mượn");
                    searchConditionChoiceBox.setVisible(true);
                    AnchorPane.clearConstraints(searchBtn);
                    AnchorPane.setLeftAnchor(searchBtn, 520.0);
                    AnchorPane.setTopAnchor(searchBtn, 15.0);
                    searchBtn.setVisible(true);
                    break;
            }
        });

        readerTable.setRowFactory(tv -> {
            TableRow<Reader> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()))
                    edit();
            });
            return row;
        });

        searchBtn.setOnAction(event -> search());
        editBtn.setOnAction(event -> edit());
        addBtn.setOnAction(event -> add());
        deleteBtn.setOnAction(event -> delete());
        refreshBtn.setOnAction(event -> refresh());

        c.addMenu.setDisable(false);
        c.editMenu.setDisable(false);
        c.deleteMenu.setDisable(false);
        c.exportMenu.setDisable(false);
        c.importMenu.setDisable(false);

        c.addMenu.setOnAction(e -> addBtn.fire());
        c.editMenu.setOnAction(e -> editBtn.fire());
        c.deleteMenu.setOnAction(e -> deleteBtn.fire());
        c.exportMenu.setOnAction(e -> export());
        c.importMenu.setOnAction(event -> importData());
    }

    public void reloadData() {
        Runnable reload = () -> {
            try {
                data = FXCollections.observableArrayList(ReaderDAO.getInstance().getAllReaders());
                readerTable.setItems(data);
            } catch (SQLException e) {
                Platform.runLater(() -> ExHandler.handle(e));
            }
        };
        new Thread(reload).start();
    }

    public void refresh() {
        reloadData();

        searchChoiceBox.getSelectionModel().clearSelection();
        searchType = -1;

        RotateTransition rt = new RotateTransition(Duration.millis(750), refreshIcon);
        rt.setByAngle(360 * 3);
        rt.setCycleCount(1);
        rt.setInterpolator(Interpolator.EASE_BOTH);
        rt.play();
    }

    // ISSUES WITH THE FIRST 2 SEARCHES
    public void search() {

        Runnable searchTask = () -> {
            try {
                switch (searchType) {
                    case 0:
                    case 1:
                        System.out.println("Searching case " + searchType);
                        System.out.println(searchInputField.getText());
                        data = FXCollections.observableArrayList(ReaderDAO.getInstance().searchReader(searchType, searchInputField.getText()));
                        break;
                    case 2:
                        System.out.println("Searching case " + searchType);
                        Timestamp startDate = Timestamp.valueOf(searchStartDate.getValue().atStartOfDay());
                        Timestamp endDate = Timestamp.valueOf(searchEndDate.getValue().atStartOfDay());
                        data = FXCollections.observableArrayList(ReaderDAO.getInstance().searchReaderByCreatedTime(startDate, endDate));
                        break;
                    case 3:
                    case 4:
                        System.out.println("Searching case " + searchType);
                        String value = searchConditionChoiceBox.getSelectionModel().getSelectedItem();
                        data = FXCollections.observableArrayList(ReaderDAO.getInstance().searchReader(searchType, value));
                        break;
                }
                Platform.runLater(() -> {
                    readerTable.setItems(data);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> ExHandler.handle(e));
            }
        };

        new Thread(searchTask).start();
    }

    public void add() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/reader/editreader.fxml"));
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot, 600, 500);

            stage.setTitle("Thêm độc giả");
            stage.setScene(scene);
            stage.setResizable(false);

            EditReaderController editReaderController = loader.getController();
            editReaderController.init();

            stage.showAndWait();
            reloadData();

        } catch (IOException e) {
            ExHandler.handle(e);
        }

    }

    public void delete() {
        Reader focusedReader = readerTable.getSelectionModel().getSelectedItem();

        if (focusedReader == null) {
            ExHandler.handle(new RuntimeException("Bạn chưa chọn độc giả nào."));
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xoá");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xoá?");
        confirmAlert.setContentText("Bạn đang thực hiện xoá Độc giả ID " + focusedReader.getRid() + " - " + focusedReader.getName() + ".");
        confirmAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> choice = confirmAlert.showAndWait();

        if (choice.get() == ButtonType.OK) {
            try {
                if (ReaderDAO.getInstance().deleteReader(focusedReader)) {
                    Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                    resultAlert.setTitle("Kết quả xoá");
                    resultAlert.setHeaderText("Xoá thành công!");
                    resultAlert.setContentText("Đã xoá độc giả ID " + focusedReader.getRid() + " - " + focusedReader.getName() + " thành công.");
                    resultAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    resultAlert.showAndWait();
                    reloadData();
                } else {
                    Alert resultAlert = new Alert(Alert.AlertType.ERROR);
                    resultAlert.setTitle("Kết quả xoá");
                    resultAlert.setHeaderText("Xoá thất bại!");
                    resultAlert.setContentText("Độc giả ID " + focusedReader.getRid() + " - " + focusedReader.getName() + " chưa xoá khỏi CSDL.");
                    resultAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    resultAlert.showAndWait();
                }
                ;
            } catch (SQLException e) {
                ExHandler.handle(e);
            }
        }
    }

    public void edit() {
        Reader focusedReader = readerTable.getSelectionModel().getSelectedItem();

        if (focusedReader == null) {
            ExHandler.handle(new RuntimeException("Bạn chưa chọn độc giả nào."));
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("view/reader/editreader.fxml"));
        try {
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot, 600, 500);
            stage.setTitle("Sửa độc giả");
            stage.setScene(scene);
            stage.setResizable(false);
            EditReaderController editReaderController = loader.getController();
            editReaderController.init(focusedReader);

            stage.showAndWait();
        } catch (IOException e) {
            ExHandler.handle(e);
        }

        reloadData();
    }

    public void export() {
        if (data.size() == 0) {
            ExHandler.handle(new Exception("Không tìm thấy dữ liệu phù hợp với lựa chọn tìm kiếm."));
        } else {
            File file = new File("src/resources/form/DsDocGia.xlsx");

            XSSFWorkbook workbook;

            try {
                FileInputStream inputStream = new FileInputStream(file);
                workbook = new XSSFWorkbook(inputStream);
                inputStream.close();
            } catch (IOException e) {
                ExHandler.handle(e);
                return;
            }

            XSSFSheet sheet = workbook.getSheetAt(0);

            // CELL STYLES
            XSSFCellStyle tableElementStyle = workbook.createCellStyle();
            tableElementStyle.setBorderLeft(BorderStyle.THIN);
            tableElementStyle.setBorderRight(BorderStyle.THIN);
            tableElementStyle.setFont(workbook.createFont());
            tableElementStyle.getFont().setFontHeightInPoints((short) 9);
            tableElementStyle.getFont().setFontName("Arial");
            tableElementStyle.setWrapText(true);

            XSSFCellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setFont(workbook.createFont());
            dateStyle.getFont().setFontHeightInPoints((short) 9);
            dateStyle.getFont().setFontName("Arial");
            dateStyle.setAlignment(HorizontalAlignment.CENTER);
            dateStyle.getFont().setItalic(true);

            XSSFCellStyle searchInfoStyle = workbook.createCellStyle();
            searchInfoStyle.setFont(workbook.createFont());
            searchInfoStyle.getFont().setFontHeightInPoints((short) 9);
            searchInfoStyle.setAlignment(HorizontalAlignment.CENTER);
            searchInfoStyle.getFont().setFontName("Arial");

            XSSFCell cell = null;

            Reader reader;

            // DATE
            cell = sheet.getRow(1).getCell(5);
            cell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy")));
            cell.setCellStyle(dateStyle);

            // SEARCH TYPE
            if (searchType != -1) {
                String searchChoices[] = {"Mã độc giả", "Tên", "Thời gian tạo", "Giới tính", "Trạng thái"};
                //                             0            1       2               3           4
                String searchInfoString = searchChoices[searchType];

                switch (searchType) {
                    case 0:
                        searchInfoString += ": " + searchInputField.getText();
                        break;
                    case 1:
                        searchInfoString += " có chữ: " + searchInputField.getText();
                        break;
                    case 2:
                        searchInfoString += " từ " +
                                DateTimeFormatter.ofPattern("dd/MM/YYYY").format(searchStartDate.getValue()) + " đến " +
                                DateTimeFormatter.ofPattern("dd/MM/YYYY").format(searchEndDate.getValue());
                        break;
                    case 3:
                    case 4:
                        searchInfoString += ": " + searchConditionChoiceBox.getSelectionModel().getSelectedItem();
                        break;
                }

                cell = sheet.getRow(5).getCell(0);
                cell.setCellStyle(searchInfoStyle);
                cell.setCellValue(searchInfoString);
            }

            // DATA
            for (int i = 0; i < data.size(); i++) {
                reader = data.get(i);
                int row = 8 + i;
                sheet.createRow(row);

                cell = sheet.getRow(row).createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(1);
                cell.setCellValue(String.format("%06d", reader.getRid()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(2);
                cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(reader.getCreated()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(3);
                cell.setCellValue(reader.getName());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(4);
                cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(reader.getDob()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(5);
                cell.setCellValue(reader.getGender() ? "Nam" : "Nữ");
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(6);
                cell.setCellValue(String.valueOf(reader.getIdCardNum()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(7);
                cell.setCellValue(reader.getAddress());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(8);
                cell.setCellValue(reader.isCanBorrow() ? "Được mượn" : "Không được mượn");
                cell.setCellStyle(tableElementStyle);
            }

            // Ghi file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn vị trí lưu.");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialFileName("Thong Tin Doc Gia " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedFile = fileChooser.showSaveDialog(readerTable.getScene().getWindow());

            try {
                FileOutputStream output = new FileOutputStream(selectedFile);
                workbook.write(output);
                output.close();
                Desktop.getDesktop().open(selectedFile);
            } catch (IOException e) {
                ExHandler.handle(e);
            }
        }
    }

    public void importData() {

        ArrayList<Reader> newReaders = new ArrayList<>();
        Reader newReader;

        XSSFWorkbook excelWorkBook;

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file.");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedFile = fileChooser.showOpenDialog(readerTable.getScene().getWindow());


            FileInputStream inputStream = new FileInputStream(selectedFile);
            excelWorkBook = new XSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            ExHandler.handle(e);
            return;
        }

        XSSFSheet sheet = excelWorkBook.getSheetAt(0);

        // DATA
        Iterator rows = sheet.rowIterator();
        XSSFRow row = (XSSFRow) rows.next();
        if (row.getLastCellNum() >= 6) {
            while (rows.hasNext()) {
                row = (XSSFRow) rows.next();
                newReader = new Reader();

                newReader.setName(row.getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue());
                newReader.setDob(new Date(row.getCell(1, CREATE_NULL_AS_BLANK).getDateCellValue().getTime()));
                newReader.setGender(row.getCell(2, CREATE_NULL_AS_BLANK).getBooleanCellValue());
                newReader.setIdCardNum((long) row.getCell(3, CREATE_NULL_AS_BLANK).getNumericCellValue());
                newReader.setAddress(row.getCell(4, CREATE_NULL_AS_BLANK).getStringCellValue());
                newReader.setCanBorrow(row.getCell(5, CREATE_NULL_AS_BLANK).getBooleanCellValue());

                newReaders.add(newReader);
            }
        } else
            ExHandler.handle(new Exception("File không đúng định dạng." + row.getLastCellNum()));

        ReaderDAO.getInstance().importReader(newReaders);
        refresh();
    }
}