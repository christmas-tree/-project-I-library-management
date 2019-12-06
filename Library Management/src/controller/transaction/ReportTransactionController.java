/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.transaction;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import controller.basic.IndexController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
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

public class ReportTransactionController {
    @FXML
    private AnchorPane reportPane;

    @FXML
    private Label figure2;

    @FXML
    private Label figure3;

    @FXML
    private Label headerLabel;

    @FXML
    private Label figure1;

    @FXML
    private TableView<ObservableList<String>> table4;

    @FXML
    private TableView<ObservableList<String>> table2;

    @FXML
    private TableView<ObservableList<String>> table1;

    @FXML
    private Button printBtn;

    @FXML
    private LineChart<String, Integer> lineChart4;

    @FXML
    private TableView<ObservableList<String>> table3L;

    @FXML
    private Label figure6;

    @FXML
    private LineChart<String, Integer> lineChart1;

    @FXML
    private Label figure7;

    @FXML
    private Label figure4;

    @FXML
    private Label figure5;

    @FXML
    private TableView<ObservableList<String>> table3R;

    @FXML
    private BarChart<String, Integer> barChart2;

    @FXML
    private Label figure8;

    private ObservableList<ObservableList<String>> table1Data = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> table2Data = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> table3LData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> table3RData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> table4Data = FXCollections.observableArrayList();

    // COUNT

    private final String SQL1 = "SELECT COUNT(*) FROM [transaction]";
    private final String SQL2 = "SELECT COUNT(*) FROM transactionDetail";
    private final String SQL3 = "SELECT COUNT(*) FROM transactionDetail WHERE returnDate IS NULL";
    private final String SQL4 = "SELECT COUNT(DISTINCT transactId) FROM transactionDetail WHERE returnDate IS NULL";
    private final String SQL5 = "SELECT AVG(depositSum) FROM (SELECT SUM(deposit) depositSum FROM transactionDetail GROUP BY transactId) t";
    private final String SQL6 = "SELECT AVG(fineSum) FROM (SELECT SUM(fine) fineSum FROM transactionDetail GROUP BY transactId) t";
    private final String SQL7 = "SELECT SUM(deposit) FROM transactionDetail";
    private final String SQL8 = "SELECT SUM(fine) FROM transactionDetail";

    private int number1, number2, number3, number4, number5, number6, number7, number8;

    // STATISTIC

    private final String SQLTable1 = "SELECT FORMAT(t.borrowingDate, 'yyyy-MM') [Thời gian], COUNT(*) [Số lượt] FROM transactionDetail dt, [transaction] t WHERE dt.transactId=t.transactId GROUP BY FORMAT(t.borrowingDate, 'yyyy-MM') ORDER BY FORMAT(t.borrowingDate, 'yyyy-MM') DESC";
    private final String SQLTable2 = "SELECT catName [Thể loại], COUNT(*) [Số lượt mượn], [Thời gian mượn TB] = ISNULL(AVG(DATEDIFF(day, borrowingDate, returnDate)), 0)\n" +
            "FROM [transaction] t, transactionDetail dt, book b, category c\n" +
            "WHERE t.transactId = dt.transactId AND dt.bid = b.bid AND b.catId = c.catId\n" +
            "GROUP BY catName";
    private final String SQLTable3L = "SELECT TOP 100 bookName [Tên sách], COUNT(transactId) [Số lượt] FROM book b LEFT JOIN transactionDetail dt ON dt.bid=b.bid GROUP BY bookName ORDER BY [Số lượt] DESC";
    private final String SQLTable3R = "SELECT TOP 100 bookName [Tên sách], COUNT(transactId) [Số lượt] FROM book b LEFT JOIN transactionDetail dt ON dt.bid=b.bid GROUP BY bookName ORDER BY [Số lượt] ASC";
    private final String SQLTable4 = "SELECT FORMAT(borrowingDate, 'yyyy - MM') [Thời gian], [Tổng tiền phạt] = ISNULL(SUM(fine), 0), [Trung bình] = ISNULL(AVG(fine), 0)\n" +
            "FROM transactionDetail dt,\n" +
            "     [transaction] t\n" +
            "WHERE dt.transactId = t.transactId\n" +
            "GROUP BY FORMAT(borrowingDate, 'yyyy - MM')\n";

    public void init(IndexController c) {

        // COUNT INIT
        number1 = getCount(SQL1);
        number2 = getCount(SQL2);
        number3 = getCount(SQL3);
        number4 = getCount(SQL4);
        number5 = getCount(SQL5);
        number6 = getCount(SQL6);
        number7 = getCount(SQL7);
        number8 = getCount(SQL8);

        figure1.setText(String.valueOf(number1));
        figure2.setText(String.valueOf(number2));
        figure3.setText(String.valueOf(number3));
        figure4.setText(String.valueOf(number4));
        figure5.setText(String.format("%,d", number5));
        figure6.setText(String.format("%,d", number6));
        figure7.setText(String.format("%,d", number7));
        figure8.setText(String.format("%,d", number8));

        // TABLE 1
        populateTable(SQLTable1, table1, table1Data);

        // CHART 1
        XYChart.Series<String, Integer> lineChart1Data = new XYChart.Series<>();
        lineChart1Data.setName("Số lượt mượn sách theo thời gian");
        for (int i = 0; i < table1Data.size(); i++) {
            lineChart1Data.getData().add(new XYChart.Data(table1Data.get(i).get(0), Integer.parseInt(table1Data.get(i).get(1))));
        }
        lineChart1.getData().add(lineChart1Data);


        // TABLE 2
        populateTable(SQLTable2, table2, table2Data);

        // CHART 2
        XYChart.Series<String, Integer> barChart2Data = new XYChart.Series<>();
        barChart2Data.setName("Tổng số lượt mượn sách theo thể loại");
        for (int j = 0; j < table2Data.size(); j++) {
            barChart2Data.getData().add(new XYChart.Data(table2Data.get(j).get(0), Integer.parseInt(table2Data.get(j).get(1))));
        }
        barChart2.getData().add(barChart2Data);

        // TABLE 3
        populateTable(SQLTable3L, table3L, table3LData);
        populateTable(SQLTable3R, table3R, table3RData);

        // TABLE 4
        populateTable(SQLTable4, table4, table4Data);

        // CHART 4
        XYChart.Series<String, Integer> lineChart4Data1 = new XYChart.Series<>();
        lineChart4Data1.setName("Tổng số tiền phạt theo thời gian");
        XYChart.Series<String, Integer> lineChart4Data2 = new XYChart.Series<>();
        lineChart4Data2.setName("Số tiền phạt trung bình theo thời gian");

        for (int j = 0; j < table4Data.size(); j++) {
            lineChart4Data1.getData().add(new XYChart.Data(table4Data.get(j).get(0), Integer.parseInt(table4Data.get(j).get(1))));
            lineChart4Data2.getData().add(new XYChart.Data(table4Data.get(j).get(0), Integer.parseInt(table4Data.get(j).get(2))));
        }

        lineChart4.getData().addAll(lineChart4Data1, lineChart4Data2);

        // BUTTONS
        printBtn.setOnAction(event -> export());
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
        File file = new File("src/resources/form/BaoCaoMuonTra.xlsx");

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

        sheet.getRow(6).getCell(2).setCellValue(number1);
        sheet.getRow(7).getCell(2).setCellValue(number2);
        sheet.getRow(6).getCell(4).setCellValue(number3);
        sheet.getRow(7).getCell(4).setCellValue(number4);
        sheet.getRow(6).getCell(6).setCellValue(number5);
        sheet.getRow(7).getCell(6).setCellValue(number6);
        sheet.getRow(6).getCell(8).setCellValue(number7);
        sheet.getRow(7).getCell(8).setCellValue(number8);



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

        int nextRowIndex = 11;
        int shiftedRows = 0;

        if (table2Data.size() > table1Data.size())
            shiftedRows =  table2Data.size() - 1;
        else
            shiftedRows = table1Data.size() - 1;

        // TABLE 1
        sheet.shiftRows(nextRowIndex, sheet.getLastRowNum(), shiftedRows);
        for (int i = 0; i < table1Data.size(); i++) {
            cell = sheet.createRow(11 + i).createCell(1);
            cell.setCellValue(table1Data.get(i).get(0));
            cell.setCellStyle(tableFirstElementStyle);

            cell = sheet.getRow(11 + i).createCell(2);
            cell.setCellValue(Integer.parseInt(((table1Data.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);
        }

        // TABLE 2
        for (int i = 0; i < table2Data.size(); i++) {
            XSSFRow row = sheet.getRow(11+i);
            if (row == null) row = sheet.createRow(11+i);

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

        nextRowIndex = nextRowIndex + shiftedRows + 5;

        // TABLE 3L
        sheet.shiftRows(nextRowIndex, sheet.getLastRowNum(), 99);
        for (int i = 0; i < table3LData.size(); i++) {
            cell = sheet.createRow(nextRowIndex + i).createCell(1);
            cell.setCellValue(table3LData.get(i).get(0));
            cell.setCellStyle(tableFirstElementStyle);

            cell = sheet.getRow(nextRowIndex + i).createCell(2);
            cell.setCellValue(Integer.parseInt(((table3LData.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);
        }

        // TABLE 3R
        for (int i = 0; i < table3RData.size(); i++) {
            cell = sheet.getRow(nextRowIndex + i).createCell(4);
            cell.setCellValue(table3RData.get(i).get(0));
            cell.setCellStyle(tableFirstElementStyle);

            cell = sheet.getRow(nextRowIndex + i).createCell(5);
            cell.setCellValue(Integer.parseInt(((table3RData.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);
        }

        nextRowIndex = nextRowIndex + 104;

        for (int i = 0; i < table4Data.size(); i++) {
            cell = sheet.createRow(nextRowIndex + i).createCell(1);
            cell.setCellValue(table4Data.get(i).get(0));
            cell.setCellStyle(tableFirstElementStyle);

            cell = sheet.getRow(nextRowIndex + i).createCell(2);
            cell.setCellValue(Integer.parseInt(((table4Data.get(i).get(1)))));
            cell.setCellStyle(tableElementStyle);

            cell = sheet.getRow(nextRowIndex + i).createCell(3);
            cell.setCellValue(Integer.parseInt(((table4Data.get(i).get(2)))));
            cell.setCellStyle(tableElementStyle);
        }

        // Ghi file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn vị trí lưu.");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("ThongKeMuonTra - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
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
