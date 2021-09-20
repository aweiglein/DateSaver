package ViewController;

import Model.ContactObj;
import Model.ReportObj;
import Utils.DBConnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;


public class ReportsScreenController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML private Button LogoutBtn;
    @FXML private Button customersBtn;
    @FXML private Button mainBtn;
    @FXML private Label errorLabel;
    @FXML private Label currentTime12Label;
    @FXML private Label currentTime24Label;
    @FXML private Label currentDateLabel;
    @FXML private Label successfulLoginsLabel;
    @FXML private Label failedLoginsLabel;
    @FXML private ComboBox<ContactObj> contactBox;
    @FXML private PieChart pieChart;
    @FXML private TableView<ReportObj> monthTypeTable;
    @FXML private TableColumn<ReportObj, String> monthCol;
    @FXML private TableColumn<ReportObj, String> monthTypeCol;
    @FXML private TableColumn<ReportObj, String> countCol;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setText("");
        System.out.println("REPORTS SCREEN");

        // Lambda expression continually updates current time and date label
        currentDateTimeLabel();

        // Run 'by type' report
        try {
            byTypeReport();
            setPieChart();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Run 'login attempts' report
        try {
            loginAttemptsReport();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        contactBox.setItems(ContactObj.contactList);
    }


    private void loginAttemptsReport() throws FileNotFoundException {
        String loginActivity = "login_activity.txt";
        File file = new File(loginActivity);
        if (file.exists()) {
            Scanner scannedFile = new Scanner(file);
            int successfulAttempts = 0;
            int failedAttempts = 0;

            while (scannedFile.hasNext()) {
                String item = scannedFile.nextLine();

                if (item.contains("SUCCESS")) {
                    successfulAttempts++;
                }
                if (item.contains("FAIL")) {
                    failedAttempts++;
                }
            }
            scannedFile.close();
            successfulLoginsLabel.setText(String.valueOf(successfulAttempts));
            failedLoginsLabel.setText(String.valueOf(failedAttempts));
        } else {
            System.out.println("Log file does not exist");
        }
    }

    private void byTypeReport() throws SQLException {
        try {
            DBConnection.loadReportByType();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        countCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        monthTypeTable.getItems().setAll(ReportObj.typeReport);
    }

    private void setPieChart() throws SQLException {
        String sql = "SELECT appointments.type, COUNT(*) From appointments Group By type;";
        PreparedStatement ps = DBConnection.conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        while (rs.next()) {
            int number = rs.getInt("COUNT(*)");
            String typeName = rs.getString("type");
            pieChartData.add(new PieChart.Data(typeName, number));
        }
        pieChart.setData(pieChartData);
        boolean pieIsLoaded = true;
    }

    @FXML
    void mainBtnClicked(ActionEvent event) {
        try {
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("../View/MainScreen.fxml"));
            stage.setTitle("Scheduling Program - Schedule");
            stage.setScene(new Scene(scene));
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e) {
            System.out.println("SQL error: " + e);
        }
    }

    @FXML
    void customersBtnClicked(ActionEvent event) {
        try {
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("../View/CustomersScreen.fxml"));
            stage.setTitle("Scheduling Program - Customers");
            stage.setScene(new Scene(scene));
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e) {
            System.out.println("SQL error: " + e);
        }
    }

    @FXML
    void LogoutBtnClicked(ActionEvent event) {
        System.out.println("Logged out");
        try {
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("../View/LoginScreen.fxml"));
            stage.setTitle("Scheduling Program - Login");
            stage.setScene(new Scene(scene));
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e) {
            System.out.println("SQL error: " + e);
        }
    }

    //Lambda expression continually updates current time and date label
    @FXML
    public void currentDateTimeLabel() {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, event -> {
            LocalDateTime localDateTime = LocalDateTime.now();
            // Current time in 12 hr clock
            DateTimeFormatter hr12 = DateTimeFormatter.ofPattern("hh:mm a");
            currentTime12Label.setText(localDateTime.format(hr12));
            // Current time in 24 hr clock
            DateTimeFormatter hr24 = DateTimeFormatter.ofPattern("HH:mm");
            currentTime24Label.setText(localDateTime.format(hr24));
            // Current date
            DateTimeFormatter dateFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            String dateFormatStr = dateFormatted.format(localDateTime);
            currentDateLabel.setText(dateFormatStr);
        }));
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
