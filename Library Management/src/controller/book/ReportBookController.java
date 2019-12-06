/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.book;

import controller.basic.IndexController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.chart.Chart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.*;
import util.DbConnection;
import util.ExHandler;

import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    public void init(IndexController c) {
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

        printBtn.setOnAction(event -> export());
        c.exportMenu.setDisable(false);
        c.exportMenu.setOnAction(event -> printBtn.fire());
        c.importMenu.setDisable(true);


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

    private boolean export() {
        File file = new File("src/resources/form/BaoCaoSach.xlsx");

        XSSFWorkbook workbook;

        try {
            FileInputStream inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            ExHandler.handle(e);
            return false;
        }

        XSSFSheet sheet = workbook.getSheetAt(0);

        sheet.getRow(6).getCell(2).setCellValue(bookSum);
        sheet.getRow(7).getCell(2).setCellValue(copySum);
        sheet.getRow(6).getCell(4).setCellValue(Integer.parseInt(pubCount.getText()));
        sheet.getRow(7).getCell(4).setCellValue(Integer.parseInt(catCount.getText()));
        sheet.getRow(6).getCell(6).setCellValue(Integer.parseInt(langCount.getText()));
        sheet.getRow(7).getCell(6).setCellValue(Integer.parseInt(authorCount.getText()));

        // CELL STYLES
        XSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setFont(workbook.createFont());
        dateStyle.getFont().setFontHeightInPoints((short) 10);
        dateStyle.getFont().setFontName("Arial");
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.getFont().setItalic(true);

        XSSFCellStyle tableHeaderStyle = workbook.createCellStyle();
        tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderStyle.setBorderRight(BorderStyle.THIN);
        tableHeaderStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderStyle.setFont(workbook.createFont());
        tableHeaderStyle.getFont().setBold(true);
        tableHeaderStyle.getFont().setFontHeightInPoints((short) 10);
        tableHeaderStyle.getFont().setFontName("Arial");

        XSSFCellStyle tableElementStyle = workbook.createCellStyle();
        tableElementStyle.setAlignment(HorizontalAlignment.CENTER);
        tableElementStyle.setBorderLeft(BorderStyle.THIN);
        tableElementStyle.setBorderRight(BorderStyle.THIN);
        tableElementStyle.setBorderTop(BorderStyle.THIN);
        tableElementStyle.setBorderBottom(BorderStyle.THIN);
        tableElementStyle.setFont(workbook.createFont());
        tableElementStyle.getFont().setFontHeightInPoints((short) 10);
        tableElementStyle.getFont().setFontName("Arial");

        XSSFCellStyle tableFirstElementStyle = workbook.createCellStyle();
        tableFirstElementStyle.setAlignment(HorizontalAlignment.LEFT);
        tableFirstElementStyle.setBorderLeft(BorderStyle.THIN);
        tableFirstElementStyle.setBorderRight(BorderStyle.THIN);
        tableFirstElementStyle.setBorderTop(BorderStyle.THIN);
        tableFirstElementStyle.setBorderBottom(BorderStyle.THIN);
        tableFirstElementStyle.setFont(workbook.createFont());
        tableFirstElementStyle.getFont().setFontHeightInPoints((short) 10);
        tableFirstElementStyle.getFont().setFontName("Arial");

        XSSFCell cell = null;

        // DATE
        cell = sheet.getRow(1).getCell(6);
        cell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy")));
        cell.setCellStyle(dateStyle);

        // BOOK BY CAT
        sheet.shiftRows(11, sheet.getLastRowNum(), bookByCatData.size() - 1);
        for (int i = 0; i < bookByCatData.size(); i++) {
            cell = sheet.createRow(11 + i).createCell(1);
            cell.setCellValue(bookByCatData.get(i).get(0));
            cell.setCellStyle(tableFirstElementStyle);
            cell = sheet.getRow(11 + i).createCell(2);
            cell.setCellValue(Integer.parseInt(((bookByCatData.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);
            cell = sheet.getRow(11 + i).createCell(3);
            cell.setCellValue(Integer.parseInt(((bookByCatData.get(i).get(2)))));
            cell.setCellStyle(tableElementStyle);
        }

//        addImageOfChartToSheet(sheet, bookByCatChart, 5, 12, 11, 16);

        int newRowPos = 11 + bookByCatData.size() + 4;

        // BOOK BY PUB YEAR
        sheet.shiftRows(newRowPos, sheet.getLastRowNum(), bookByPubYearData.size() - 1);
        for (int i = 0; i < bookByPubYearData.size(); i++) {
            cell = sheet.createRow(newRowPos + i).createCell(1);
            cell.setCellValue(Integer.parseInt(((bookByPubYearData.get(i).get(0)))));
            cell.setCellStyle(tableFirstElementStyle);

            cell = sheet.getRow(newRowPos + i).createCell(2);
            cell.setCellValue(Integer.parseInt(((bookByPubYearData.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);

            cell = sheet.getRow(newRowPos + i).createCell(3);
            cell.setCellValue(Integer.parseInt(((bookByPubYearData.get(i).get(2)))));
            cell.setCellStyle(tableElementStyle);

        }

        newRowPos = newRowPos + bookByPubYearData.size() + 3;

        // BOOK BY PUB BY CAT
        sheet.shiftRows(newRowPos, sheet.getLastRowNum(), bookByPubByCatData.size());

        int colNum = bookByPubByCatTable.getColumns().size();
        sheet.createRow(newRowPos);
        for (int j = 0; j < colNum; j++) {
            cell = sheet.getRow(newRowPos).createCell(j + 1);
            cell.setCellValue(bookByPubByCatTable.getColumns().get(j).getText());
            cell.setCellStyle(tableHeaderStyle);
        }
        newRowPos++;
        for (int i = 0; i < bookByPubByCatData.size(); i++) {
            sheet.createRow(newRowPos + i);
            for (int j = 0; j < colNum; j++) {
                cell = sheet.getRow(newRowPos + i).createCell(j + 1);
                if (j == 0) {
                    cell.setCellValue(bookByPubByCatData.get(i).get(j));
                    cell.setCellStyle(tableFirstElementStyle);
                } else {
                    cell.setCellValue(Integer.parseInt(bookByPubByCatData.get(i).get(j)));
                    cell.setCellStyle(tableElementStyle);
                }
            }
        }
        newRowPos = newRowPos + bookByPubByCatData.size() + 4;

//         BOOK IMPORT BY YEAR

        sheet.shiftRows(newRowPos, sheet.getLastRowNum(), bookImportByTimeData.size() - 1);
        for (int i = 0; i < bookImportByTimeData.size(); i++) {
            cell = sheet.createRow(newRowPos + i).createCell(1);
            cell.setCellValue(bookImportByTimeData.get(i).get(0));
            cell.setCellStyle(tableFirstElementStyle);

            cell = sheet.getRow(newRowPos + i).createCell(2);
            cell.setCellValue(Integer.parseInt(((bookImportByTimeData.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);

        }

        // Ghi file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn vị trí lưu.");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("ThongKeSach - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFile = fileChooser.showSaveDialog(reportPane.getScene().getWindow());

        try {
            FileOutputStream output = new FileOutputStream(selectedFile);
            workbook.write(output);
            output.close();
            Desktop.getDesktop().open(selectedFile);
            return true;
        } catch (IOException e) {
            ExHandler.handle(e);
            return false;
        }
    }

}
