/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.book;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import util.DbConnection;
import util.ExHandler;

import javax.sound.sampled.Line;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportBookController {

    @FXML
    private AnchorPane reportPane;

    @FXML
    private TableView<ObservableList> bookByPubByCatTable;

    private LineChart<String, Number> bookImportByTimeChart;

    @FXML
    private Label langCount;

    @FXML
    private Label bookCount;

    @FXML
    private Label copyCount;

    @FXML
    private Label pubCount;

    @FXML
    private Label authorCount;

    @FXML
    private PieChart bookByCatChart;

    @FXML
    private Button printBtn;

    @FXML
    private LineChart<String, Integer> bookByPubYearChart;

    @FXML
    private TableView<ObservableList> bookByCatTable;

    @FXML
    private TableView<ObservableList> bookByPubYearTable;

    @FXML
    private Label catCount;

    private int bookSum;
    private int copySum;

    private ObservableList<ObservableList<String>> bookByCatData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> bookByPubYearData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> bookByPubByCatData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> bookImportByTimeData = FXCollections.observableArrayList();

    // COUNT

    private final String BOOK_COUNT = "SELECT COUNT(*) FROM book";
    private final String BOOK_SUM = "SELECT SUM(quantity) FROM book";
    private final String CATEGORY_COUNT = "SELECT COUNT(*) FROM category";
    private final String PUBLISHER_COUNT = "SELECT COUNT(*) FROM publisher";
    private final String LANGUAGE_COUNT = "SELECT COUNT(*) FROM language";
    private final String AUTHOR_COUNT = "SELECT COUNT(DISTINCT author) FROM book";

    // STATISTIC

    private final String BOOK_BY_CAT = "SELECT catName [Thể loại], COUNT(bid) [Số đầu sách], SUM(quantity) [Tổng số sách] FROM book, category WHERE book.catId=category.catId GROUP BY catName ORDER BY COUNT(bid) DESC";
    private final String BOOK_BY_PUBYEAR = "SELECT pubYear [Năm XB], COUNT(bid) [Số đầu sách], SUM(quantity) [Tổng số sách] FROM book GROUP BY pubYear ORDER BY pubYear DESC";
    private final String BOOK_BY_PUB_BY_CAT = "EXEC selectBookByPubByCat";
    private final String BOOK_IMPORT_BY_TIME = "SELECT FORMAT(created, 'yyyy-MM'), SUM(quantity) FROM book b GROUP BY FORMAT(created, 'yyyy-MM') ORDER BY FORMAT(created, 'yyyy-MM') ASC";

    public void init() {
        // COUNT INIT
        bookSum = getCount(BOOK_COUNT);
        copySum = getCount(BOOK_SUM);
        bookCount.setText(String.valueOf(bookSum));
        copyCount.setText(String.valueOf(copySum));
        pubCount.setText(String.valueOf(getCount(PUBLISHER_COUNT)));
        authorCount.setText(String.valueOf(getCount(AUTHOR_COUNT)));
        langCount.setText(String.valueOf(getCount(LANGUAGE_COUNT)));
        catCount.setText(String.valueOf(getCount(CATEGORY_COUNT)));

        // STATISTIC INIT

        populateTable(BOOK_BY_CAT, bookByCatTable, bookByCatData);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i < bookByCatData.size(); i++) {
            pieChartData.add(new PieChart.Data(bookByCatData.get(i).get(0), Double.parseDouble(bookByCatData.get(i).get(1)) / (double) bookSum));
        }
        bookByCatChart.setData(pieChartData);


        populateTable(BOOK_BY_PUBYEAR, bookByPubYearTable, bookByPubYearData);

        XYChart.Series<String, Integer> lineChartData = new XYChart.Series<>();
        lineChartData.setName("Số lượng đầu sách trong thư viện theo năm xuất bản");

        for (int i = 0; i < bookByPubYearData.size(); i++) {
            lineChartData.getData().add(new XYChart.Data(bookByPubYearData.get(i).get(0), Integer.parseInt(bookByPubYearData.get(i).get(1))));
        }
        bookByPubYearChart.getData().add(lineChartData);

        populateTable(BOOK_BY_PUB_BY_CAT, bookByPubByCatTable, bookByPubByCatData);

        // LAST CHART

        getData(BOOK_IMPORT_BY_TIME, bookImportByTimeData);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Lượng sách nhập");

        XYChart.Series series = new XYChart.Series<String, Integer>();
        series.setName("Lượng sách nhập vào kho");

        for (int i = 0; i < bookImportByTimeData.size(); i++) {
            series.getData().add(new XYChart.Data<>(bookImportByTimeData.get(i).get(0), Integer.parseInt(bookImportByTimeData.get(i).get(1))));
        }

        bookImportByTimeChart = new LineChart<>(xAxis, yAxis);
        bookImportByTimeChart.getData().add(series);

        reportPane.getChildren().add(bookImportByTimeChart);
        bookImportByTimeChart.setLayoutX(30.0);
        bookImportByTimeChart.setLayoutY(1762.0);
        bookImportByTimeChart.setPrefHeight(400.0);
        AnchorPane.setLeftAnchor(bookImportByTimeChart, 30.0);
        AnchorPane.setRightAnchor(bookImportByTimeChart, 30.0);
    }

    public int getCount(String sql) {
        Connection con;
        int result = 0;

        try {
            con = DbConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(sql);

            rs.next();
            result = rs.getInt(1);

            rs.close();
            con.close();
        } catch (SQLException e) {
            ExHandler.handle(e);
        }
        return result;

    }

    public void populateTable(String sql, TableView tableView, ObservableList<ObservableList<String>> data) {

        Connection con = null;
        try {
            con = DbConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(sql);

            // TABLE COLUMN ADDED DYNAMICALLY
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setMinWidth(125.0);
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));

                tableView.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            tableView.setItems(data);
        } catch (Exception e) {
            ExHandler.handle(e);
        }
    }

    public void getData(String sql, ObservableList<ObservableList<String>> data) {
        Connection con = null;
        try {
            con = DbConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(sql);

            // TABLE COLUMN ADDED DYNAMICALLY
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
        } catch (Exception e) {
            ExHandler.handle(e);
        }
    }
}
