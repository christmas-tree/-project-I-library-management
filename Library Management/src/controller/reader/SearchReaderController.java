package controller.reader;

import controller.basic.IndexController;
import dao.ReaderDAO;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.Reader;
import util.ExHandler;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

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

        c.addMenu.setOnAction(e -> addBtn.fire());
        c.editMenu.setOnAction(e -> editBtn.fire());
        c.deleteMenu.setOnAction(e -> deleteBtn.fire());
        c.exportMenu.setOnAction(e -> export());
    }

    public void reloadData() {
        Runnable reload = () -> {
            try {
                data = FXCollections.observableArrayList(ReaderDAO.getInstance().getAllReaders());
                readerTable.setItems(data);
            } catch (SQLException e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ExHandler.handle(e);
                    }
                });
            }
        };
        new Thread(reload).start();
    }

    public void refresh() {
        reloadData();

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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ExHandler.handle(e);
                    }
                });
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

    }
}