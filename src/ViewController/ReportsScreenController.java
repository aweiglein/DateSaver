package ViewController;

import Model.*;
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
import javafx.scene.layout.StackPane;
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
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import static Model.ContactObj.contactSchedulePredicate;


public class ReportsScreenController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML private Button LogoutBtn;
    @FXML private Button customersBtn;
    @FXML private Button mainBtn;
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
    @FXML private TableView<AppointmentObj> contactTable;
    @FXML private TableColumn<AppointmentObj, Integer> apptIDCol;
    @FXML private TableColumn<AppointmentObj, String> typeCol;
    @FXML private TableColumn<AppointmentObj, String> titleCol;
    @FXML private TableColumn<AppointmentObj, String> descriptionCol;
    @FXML private TableColumn<AppointmentObj, Date> startCol;
    @FXML private TableColumn<AppointmentObj, Date> endCol;
    @FXML private TableColumn<AppointmentObj, CustomerObj> custIDCol;
    @FXML private TableColumn<AppointmentObj, ContactObj> contactCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("REPORTS SCREEN");

        // Lambda expression continually updates current time and date label
        currentDateTimeLabel();

        StackPane placeHolder = new StackPane();
        placeHolder.setStyle("-fx-background-color:linear-gradient(from 40px 14px to 40px 40px , repeat, #f2f2f2 49%, #f7f7f7 12% );");
        contactTable.setPlaceholder(placeHolder);

        // set contacts table
        contactTable.setItems(null);
        contactBox.setItems(ContactObj.contactList);
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("appTitle"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("appLocation"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("appContact"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("appType"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        // Run 'by type' and 'pie chart' reports
        try {
            DBConnection.loadUsersFromDatabase();
            DBConnection.loadCountriesFromDatabase();
            DBConnection.loadDivisionsFromDatabase();
            DBConnection.loadContactsFromDatabase();
            DBConnection.loadCustomersFromDatabase();
            DBConnection.loadAppointmentsFromDatabase();

            System.out.println("\t\tUsers\t\t\tITEM COUNT: " + UserObj.userList.size());
            System.out.println("\t\tAppointments\tITEM COUNT: " + AppointmentObj.appointmentList.size());
            System.out.println("\t\tCustomers\t\tITEM COUNT: " + CustomerObj.customerList.size());
            System.out.println("\t\tContacts\t\tITEM COUNT: " + ContactObj.contactList.size());
            System.out.println("\t\tCountries\t\tITEM COUNT: " + CountryObj.countryList.size());
            System.out.println("\t\tDivisions\t\tITEM COUNT: " + DivisionObj.divisionList.size() + "\n");

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

    @FXML
    void contactBoxFilter(ActionEvent event) {
        if (contactBox.getItems() == null) {
            contactTable.setItems(null);
        } else {
            contactTable.setItems(AppointmentObj.getAllAppointments().filtered(contactSchedulePredicate(contactBox.getValue())));
            System.out.println(">>> Viewing appointments for [contact: " + contactBox.getValue() + "]");
        }
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
