/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.reader;

import controller.basic.IndexController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import util.DbConnection;
import util.ExHandler;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportReaderController {
    @FXML
    private AnchorPane reportPane;

    @FXML
    private PieChart pieChart1;

    @FXML
    private TableView<ObservableList<String>> table2;

    @FXML
    private TableView<ObservableList<String>> table1;

    @FXML
    private Button printBtn;

    @FXML
    private Label canBorrowCount;

    @FXML
    private Label cannotBorrowCount;

    @FXML
    private Label readerCount;

    private ObservableList<ObservableList<String>> table1Data = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> table2Data = FXCollections.observableArrayList();

    private int readerNumber;

    // COUNT

    private final String SQL1 = "SELECT COUNT(*) FROM reader";
    private final String SQL2 = "SELECT COUNT(*) FROM reader WHERE canBorrow=1";
    private final String SQL3 = "SELECT COUNT(*) FROM reader WHERE canBorrow=0";

    // STATISTIC

    private final String SQLTable1 = "SELECT [Giới tính] = (CASE WHEN gender=1 THEN N'Nam' ELSE N'Nữ' END), COUNT(*) [Số lượng] FROM reader GROUP BY gender";
    private final String SQLTable2 = "SELECT FORMAT(created, 'yyyy-MM') [Thời gian], COUNT(NULLIF(0, gender)) [Nam], COUNT(NULLIF(1, gender)) [Nữ] FROM reader GROUP BY FORMAT(created, 'yyyy-MM') ORDER BY FORMAT(created, 'yyyy-MM') DESC";

    public void init(IndexController c) {

        // COUNT INIT
        readerNumber = getCount(SQL1);
        readerCount.setText(String.valueOf(readerNumber));
        canBorrowCount.setText(String.valueOf(getCount(SQL2)));
        cannotBorrowCount.setText(String.valueOf(getCount(SQL3)));

        populateTable(SQLTable1, table1, table1Data);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i < table1Data.size(); i++) {
            pieChartData.add(new PieChart.Data(table1Data.get(i).get(0), Double.parseDouble(table1Data.get(i).get(1)) / (double) readerNumber));
        }
        pieChart1.setData(pieChartData);

        // TABLE 2
        populateTable(SQLTable2, table2, table2Data);

        // BUTTON
        printBtn.setOnAction(event -> export());

// MENU
        c.addMenu.setDisable(true);
        c.editMenu.setDisable(true);
        c.deleteMenu.setDisable(true);
        c.importMenu.setDisable(true);

        c.exportMenu.setDisable(false);
        c.exportMenu.setOnAction(event -> printBtn.fire());
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

    private boolean export() {
        File file = new File("src/resources/form/BaoCaoDocGia.xlsx");

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

        sheet.getRow(6).getCell(2).setCellValue(Integer.parseInt(readerCount.getText()));
        sheet.getRow(6).getCell(4).setCellValue(Integer.parseInt(canBorrowCount.getText()));
        sheet.getRow(6).getCell(6).setCellValue(Integer.parseInt(cannotBorrowCount.getText()));

        // CELL STYLES
        XSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setFont(workbook.createFont());
        dateStyle.getFont().setFontHeightInPoints((short) 10);
        dateStyle.getFont().setFontName("Arial");
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.getFont().setItalic(true);

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
        cell = sheet.getRow(1).getCell(5);
        cell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy")));
        cell.setCellStyle(dateStyle);

        // TABLE 1
        for (int i = 0; i < table1Data.size(); i++) {
            cell = sheet.createRow(10 + i).createCell(1);
            cell.setCellValue(table1Data.get(i).get(0));
            cell.setCellStyle(tableFirstElementStyle);

            cell = sheet.getRow(10 + i).createCell(2);
            cell.setCellValue(Integer.parseInt(((table1Data.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);
        }

        // TABLE 2
        for (int i = 0; i < table2Data.size(); i++) {
            XSSFRow row = sheet.getRow(10 + i);
            if (row == null) row = sheet.createRow(10 + i);

            cell = row.createCell(4);
            cell.setCellValue(table2Data.get(i).get(0));
            cell.setCellStyle(tableFirstElementStyle);

            cell = row.createCell(5);
            cell.setCellValue(Integer.parseInt(((table2Data.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);

            cell = row.createCell(6);
            cell.setCellValue(Integer.parseInt(((table2Data.get(i).get(2)))));
            cell.setCellStyle(tableElementStyle);
        }

        // Ghi file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn vị trí lưu.");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("ThongKeDocGia - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
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
