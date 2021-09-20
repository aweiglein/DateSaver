package ViewController;

import Model.*;
import Utils.DBConnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML private ToggleGroup viewPeriod;
    @FXML private RadioButton weekRadio;
    @FXML private RadioButton allRadio;
    @FXML private RadioButton monthRadio;
    @FXML private Button backDateButton;
    @FXML private Button nextDateButton;
    @FXML private Button LogoutBtn;
    @FXML private Button reportsBtn;
    @FXML private Button customersBtn;
    @FXML private Button modifyAppointmentBtn;
    @FXML private Button deleteBtn;
    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Label errorLabel;
    @FXML private Label currentTime12Label;
    @FXML private Label currentTime24Label;
    @FXML private Label currentDateLabel;
    @FXML private Label errorLabel1;
    @FXML private Label apptRecordLabel;
    @FXML private Label apptViewDateLabel;
    @FXML private ComboBox<UserObj> userBox;
    @FXML private ChoiceBox<String> typeBox;
    @FXML private ComboBox<CustomerObj> customerBox;
    @FXML private ComboBox<ContactObj> contactBox;
    @FXML private TableView<AppointmentObj> apptTable;
    @FXML private TableColumn<AppointmentObj, Integer> apptIDCol;
    @FXML private TableColumn<AppointmentObj, String> titleCol;
    @FXML private TableColumn<AppointmentObj, String> descriptionCol;
    @FXML private TableColumn<AppointmentObj, String> locationCol;
    @FXML private TableColumn<AppointmentObj, CustomerObj> contactCol;
    @FXML private TableColumn<AppointmentObj, String> typeCol;
    @FXML private TableColumn<AppointmentObj, Date> startCol;
    @FXML private TableColumn<AppointmentObj, Date> endCol;
    @FXML private TableColumn<AppointmentObj, CustomerObj> customerCol;
    @FXML private TableColumn<AppointmentObj, UserObj> userCol;
    @FXML private DatePicker date;
    @FXML private TextField startHour;
    @FXML private TextField startMin;
    @FXML private TextField endHour;
    @FXML private TextField endMin;
    @FXML private TextField location;
    @FXML private TextField title;
    @FXML private TextField apptID;
    @FXML private TextField description;
    @FXML private Pane typeError;
    @FXML private Pane dateError;
    @FXML private Pane startHourError;
    @FXML private Pane startMinError;
    @FXML private Pane endHourError;
    @FXML private Pane endMinError;
    @FXML private Pane locationError;
    @FXML private Pane titleError;
    @FXML private Pane descriptionError;
    @FXML private Pane customerError;
    @FXML private Pane contactError;
    @FXML private Pane userError;
    private boolean modify = false;
    String[] appointmentType = {"Planning Session", "Business", "Conference", "Video Call", "Other"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setText("");
        errorLabel1.setText("");
        apptRecordLabel.setText("NEW APPOINTMENT");
        System.out.println("SCHEDULE SCREEN");

        // Loads all items from SQL database
        try {
            DBConnection.loadUsersFromDatabase();
            DBConnection.loadCountriesFromDatabase();
            DBConnection.loadDivisionsFromDatabase();
            DBConnection.loadContactsFromDatabase();
            DBConnection.loadCustomersFromDatabase();
            DBConnection.loadAppointmentsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("\t\tUsers\t\t\tITEM COUNT: " + UserObj.userList.size());
        System.out.println("\t\tAppointments\tITEM COUNT: " + AppointmentObj.appointmentList.size());
        System.out.println("\t\tCustomers\t\tITEM COUNT: " + CustomerObj.customerList.size());
        System.out.println("\t\tContacts\t\tITEM COUNT: " + ContactObj.contactList.size());
        System.out.println("\t\tCountries\t\tITEM COUNT: " + CountryObj.countryList.size());
        System.out.println("\t\tDivisions\t\tITEM COUNT: " + DivisionObj.divisionList.size() + "\n");

        setTableCells();

        //loads list items into comboboxes
        userBox.setItems(UserObj.userList);
        contactBox.setItems(ContactObj.contactList);
        customerBox.setItems(CustomerObj.customerList);
        typeBox.getItems().setAll(appointmentType);

        // sets initial tableview to all appointments
        allRadio.fire();

        //Lambda expression continually updates current time and date label
        currentDateTimeLabel();

        // sets error graphics as disabled until error occurs
        typeError.setVisible(false);
        dateError.setVisible(false);
        startHourError.setVisible(false);
        startMinError.setVisible(false);
        endHourError.setVisible(false);
        endMinError.setVisible(false);
        locationError.setVisible(false);
        titleError.setVisible(false);
        descriptionError.setVisible(false);
        customerError.setVisible(false);
        contactError.setVisible(false);
        userError.setVisible(false);


        // if appt starts within 15 minutes, highlight green
    }

    // if called, loads the customer data into the form fields for modifying
    public void prefill (AppointmentObj selectedAppointment) {
        modify = true;
        apptID.setText(String.valueOf(selectedAppointment.getAppointmentID()));
        location.setText(String.valueOf(selectedAppointment.getAppLocation()));
        description.setText(String.valueOf(selectedAppointment.getAppDescription()));
        title.setText(String.valueOf(selectedAppointment.getAppTitle()));
        customerBox.setValue(selectedAppointment.getCustomerID());
        typeBox.setValue(selectedAppointment.getAppType());
        contactBox.setValue(selectedAppointment.getContactID());
        userBox.setValue(selectedAppointment.getUserID());
        ZonedDateTime convertedDate = selectedAppointment.getStartDateObj().withZoneSameInstant(ZoneId.systemDefault());
        date.setValue(convertedDate.toLocalDate());
        startHour.setText(String.valueOf(selectedAppointment.getStartDateObj().getHour()));
        startMin.setText(String.valueOf(selectedAppointment.getStartDateObj().getMinute()));
        endHour.setText(String.valueOf(selectedAppointment.getEndDateObj().getHour()));
        endMin.setText(String.valueOf(selectedAppointment.getEndDateObj().getMinute()));
    }

    // filters tableview to display appointments by all, weekly, or monthly
    @FXML
    private void viewSelectedRadio(ActionEvent event) {
        // All appointments radio button
        if(viewPeriod.getSelectedToggle().equals(allRadio)){
            System.out.println(">>> Viewing all appointments");
            apptViewDateLabel.setText("ALL APPOINTMENTS");
            apptViewDateLabel.setVisible(true);
            nextDateButton.setVisible(false);
            backDateButton.setVisible(false);
            // display all appointments
            setTableCells();
        }

        // Week appointments radio button
        if(viewPeriod.getSelectedToggle().equals(weekRadio)){
            System.out.println(">>> Viewing appointments by week");
            apptViewDateLabel.setText(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " — " + ZonedDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            apptViewDateLabel.setVisible(true);
            nextDateButton.setVisible(true);
            backDateButton.setVisible(true);
            setTableCells();

            // filter tableview
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowPlus7 = now.plusDays(7);
            FilteredList<AppointmentObj> filteredData = new FilteredList<>(AppointmentObj.getAllAppointments());
            filteredData.setPredicate(row -> {
                LocalDateTime rowDate = row.getStartDateObj().toLocalDateTime();
                return rowDate.isAfter(now) && rowDate.isBefore(nowPlus7); });

            //set tableview with filtered display
            apptTable.setItems(filteredData);
        }


        // Month appointments radio button
        if(viewPeriod.getSelectedToggle().equals(monthRadio)){
            System.out.println(">>> Viewing appointments by month");
            apptViewDateLabel.setText(String.valueOf(ZonedDateTime.now().getMonth()));
            apptViewDateLabel.setVisible(true);
            nextDateButton.setVisible(true);
            backDateButton.setVisible(true);
            setTableCells();

            // filter table
            int monthInt = LocalDateTime.now().getMonthValue();

            //set tableview with filtered display
            apptTable.setItems(AppointmentObj.getAllAppointments().filtered(AppointmentObj.appointmentDateIntPredicate(monthInt)));
        }
    }


    // displays previous appointments
    /**
     * On 'viewBackDate' event, the back arrow button filters to show previous appointments.
     * @param event Back arrow button clicked
     */
    @FXML
    private void viewBackDate(ActionEvent event) {
        if(viewPeriod.getSelectedToggle().equals(weekRadio)){
            System.out.println("\tViewing back");
            errorLabel.setText("");
            errorLabel1.setText("");
            }

        if(viewPeriod.getSelectedToggle().equals(monthRadio)){
            System.out.println("\tViewing back");
            errorLabel.setText("");
            errorLabel1.setText("");
            }
        }

    // displays future appointments
    /**
     * On 'viewNextDate' event, the forward arrow button filters to show future appointments.
     * @param event Forward arrow button clicked
     */
    @FXML
    private void viewNextDate(ActionEvent event) throws ParseException {
        if (viewPeriod.getSelectedToggle().equals(weekRadio)) {
            System.out.println("\tViewing next");
            errorLabel.setText("");
            errorLabel1.setText("");
            }

        if (viewPeriod.getSelectedToggle().equals(monthRadio)) {
            System.out.println("\tViewing back");
            errorLabel.setText("");
            errorLabel1.setText("");
            }
    }

    // calls prefill method to send customer information to the field boxes
    @FXML
    private void modifyAppointmentBtnClicked(ActionEvent event) {
        AppointmentObj selectedAppointment = apptTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            errorLabel.setText("Select an appointment to modify");
        } else {
            errorLabel.setText("");
            apptRecordLabel.setText("MODIFY APPOINTMENT");
            prefill(selectedAppointment);
            System.out.println("Modifying appointment... ");
            clearAllErrorBoxes();
        }
    }

    // saves new customer or modified customer depending on if update=true
    @FXML
    void saveBtnClicked(ActionEvent event) throws SQLException {
        AppointmentObj selectedAppointment = apptTable.getSelectionModel().getSelectedItem();

        // display error boxes if empty
        locationError.setVisible(location.getText().trim().isEmpty());
        descriptionError.setVisible(description.getText().trim().isEmpty());
        titleError.setVisible(title.getText().trim().isEmpty());
        startHourError.setVisible(startHour.getText().trim().isEmpty() || startHour.getText().length() > 2);
        startMinError.setVisible(startMin.getText().trim().isEmpty() || startMin.getText().length() > 2);
        endHourError.setVisible(endHour.getText().trim().isEmpty() || endHour.getText().length() > 2);
        endMinError.setVisible(endMin.getText().trim().isEmpty() || endMin.getText().length() > 2);
        customerError.setVisible(customerBox.getSelectionModel().isEmpty());
        contactError.setVisible(contactBox.getSelectionModel().isEmpty());
        userError.setVisible(userBox.getSelectionModel().isEmpty());
        typeError.setVisible(typeBox.getSelectionModel().isEmpty());
        dateError.setVisible(date.getValue() == null);

        // check if empty
        if (location.getText().trim().isEmpty() ||
                description.getText().trim().isEmpty() ||
                title.getText().trim().isEmpty() ||
                startHour.getText().trim().isEmpty() ||
                customerBox.getSelectionModel().isEmpty() ||
                contactBox.getSelectionModel().isEmpty() ||
                userBox.getSelectionModel().isEmpty() ||
                typeBox.getSelectionModel().isEmpty() ||
                date.getValue() == null ||
                startHour.getText().trim().isEmpty() ||
                startMin.getText().trim().isEmpty() ||
                endHour.getText().trim().isEmpty() ||
                endMin.getText().trim().isEmpty()) {
            errorLabel1.setText("Enter data into all fields");

        // check if time digits are 1 or 2
        } else if (endMin.getText().length() > 2 ||
                endHour.getText().length() > 2 ||
                startMin.getText().length() > 2 ||
                startHour.getText().length() > 2) {
            errorLabel1.setText("Enter 2 digits only");

        // if no errors, then try to modify or add new appointment
        } else {
            String tmp;
            tmp = apptID.getText();
            int ID = 0;

            if (apptRecordLabel.getText().equals("MODIFY APPOINTMENT")) ID = Integer.parseInt(tmp);
            String tempTime = title.getText();
            String tempDescription = description.getText();
            String tempLocation = location.getText();
            ContactObj tempContact = contactBox.getValue();
            String tempType = typeBox.getValue();
            UserObj tempUser = userBox.getValue();
            CustomerObj tempCustomer = customerBox.getValue();

            //begin start date/time - works but time isnt correct
            LocalTime beginTime = LocalTime.parse(startHour.getText() + ":" + startMin.getText());
            ZonedDateTime begin = ZonedDateTime.of(date.getValue(), beginTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC+0"));

            //end start date/time - works but time isnt correct
            LocalTime endTime = LocalTime.parse(endHour.getText() + ":" + endMin.getText());
            ZonedDateTime end = ZonedDateTime.of(date.getValue(), endTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC+0"));

            AppointmentObj appt = new AppointmentObj(ID, tempTime, tempDescription, tempLocation, tempContact, tempType, begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), tempCustomer, tempUser);

            // check isbusinesshours() check isoverlapping()
            try {
                // check boolean is within business hours?
                // check boolean is appt overlapping?

                // modify existing appointment
                if (apptRecordLabel.getText().equals("MODIFY APPOINTMENT")) {
                    //works, just incorrect time
                    DBConnection.modifyAppointment(appt);
                    errorLabel.setText("Appointment [ID: " + selectedAppointment.getAppointmentID() + ", type: " + selectedAppointment.getAppType() + "] was modified");
                    System.out.print("Appointment [ID: " + selectedAppointment.getAppointmentID() + ", type: " + selectedAppointment.getAppType() + "] was modified\n"); }

                // add new appointment
                else {
                    //works, just incorrect time
                    DBConnection.addAppointment(appt);
                    errorLabel.setText("Appointment [ID: " + ID + ", type: " + tempType + "] was added");
                    System.out.print("Appointment [ID: " + ID + ", type: " + tempType + "] was added\n"); }
            } catch (Exception e) {
                e.printStackTrace();
            }
            apptRecordLabel.setText("NEW APPOINTMENT");
            clearAllInputFields(); // change this to read error on screen
        }
    }


    // clears all form fields and errors
    @FXML
    void clearBtnClicked(ActionEvent event) {
        apptRecordLabel.setText("NEW APPOINTMENT");
        clearAllInputFields();
        clearAllErrorBoxes();
    }

    // deletes an appointment after confirmation
    @FXML
    void deleteBtnClicked(ActionEvent event) {
        AppointmentObj selectedAppointment = apptTable.getSelectionModel().getSelectedItem();

        // if no appointment selected, error is displayed
        if (selectedAppointment == null) {
            errorLabel.setText("Select an appointment to delete");

        // if appointment is selected, alert asks for delete confirmation
        } else {
            errorLabel.setText("");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText(selectedAppointment.getAppType() + " appointment [ID: " + selectedAppointment.getAppointmentID() + "] for customer [name: " + selectedAppointment.getCustomerID().getName() + "] will be permanently deleted.");
            alert.setContentText("Do you want to continue?");
            Optional<ButtonType> result = alert.showAndWait();

            // if ok is clicked, appointment is deleted
            if (result.get() == ButtonType.OK) {
                try {
                    DBConnection.deleteAppointment(selectedAppointment);
                } catch (SQLException e) {
                    System.out.println("SQL error: " + e);
                }
                clearAllInputFields();
                clearAllErrorBoxes();
                System.out.println("Appointment [ID: " + selectedAppointment.getAppointmentID() + "] was deleted");;
                errorLabel.setText("Appointment [ID: " + selectedAppointment.getAppointmentID() + "] was deleted");

            // if alert is cancelled or closed, appointment is not deleted
            } else if (result.get() == ButtonType.CANCEL || result.get() == ButtonType.CLOSE) {
                System.out.println("Appointment not deleted");
            }
        }
    }

    // takes user to customers screen
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

    // takes user to reports screen
    @FXML
    void reportsBtnClicked(ActionEvent event) {
        try {
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("../View/ReportsScreen.fxml"));
            stage.setTitle("Scheduling Program - Reports");
            stage.setScene(new Scene(scene));
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e) {
            System.out.println("SQL error: " + e);
        }
    }

    // takes user to login screen
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

    // Lambda expression continually updates current time and date label
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

    // clears all error boxes
    @FXML
    private void clearAllErrorBoxes() {
        typeError.setVisible(false);
        dateError.setVisible(false);
        startHourError.setVisible(false);
        startMinError.setVisible(false);
        endHourError.setVisible(false);
        endMinError.setVisible(false);
        locationError.setVisible(false);
        titleError.setVisible(false);
        descriptionError.setVisible(false);
        customerError.setVisible(false);
        contactError.setVisible(false);
        userError.setVisible(false);
        errorLabel1.setText("");
    }

    // clears all text fields
    @FXML
    private void clearAllInputFields() {
        errorLabel1.setText("");
        apptTable.getSelectionModel().clearSelection();
        apptID.clear();
        location.clear();
        description.clear();
        title.clear();
        customerBox.getSelectionModel().clearSelection();
        contactBox.getSelectionModel().clearSelection();
        typeBox.getSelectionModel().clearSelection();
        userBox.getSelectionModel().clearSelection();
        date.setValue(null);
        startHour.clear();
        startMin.clear();
        endHour.clear();
        endMin.clear();
    }

    private void setTableCells() {
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("appTitle"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("appDescription"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("appLocation"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("appType"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        apptTable.setItems(AppointmentObj.getAllAppointments());
    }
}
