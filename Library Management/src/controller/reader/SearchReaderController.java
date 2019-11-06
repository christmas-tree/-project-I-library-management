package controller.reader;

import dao.ReaderDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Reader;
import util.ExHandler;

import java.sql.Date;
import java.sql.SQLException;

public class SearchReaderController {

    @FXML
    private TableView<Reader> readerTable;

    @FXML
    private TableColumn<Reader, Integer> idCol;

    @FXML
    private TableColumn<Reader, String> statusCol;

    @FXML
    private TableColumn<Reader, String> nameCol;

    @FXML
    private TableColumn<Reader, Date> dobCol;

    @FXML
    private TableColumn<Reader, Boolean> genderCol;

    @FXML
    private ChoiceBox searchChoice;

    @FXML
    private Label dieuKienLabel;

    private ObservableList<Reader> data;

    public void init() {

        try {
            data = (ObservableList<Reader>) FXCollections.observableArrayList(ReaderDAO.getInstance().getAllReaders());
        } catch (SQLException e) {
            ExHandler.handle(e);
        }

        idCol.setCellValueFactory(new PropertyValueFactory<>("rid"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("canBorrow"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        idCol.setSortType(TableColumn.SortType.ASCENDING);

        readerTable.setItems(data);
    }
}
