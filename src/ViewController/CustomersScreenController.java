package ViewController;

import Model.*;

import Utils.DBConnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


public class CustomersScreenController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML private Button LogoutBtn;
    @FXML private Button reportsBtn;
    @FXML private Button customersBtn;
    @FXML private Button modifyCustomerBtn;
    @FXML private Button searchBtn;
    @FXML private Button clearBtn;
    @FXML private Button saveBtn;
    @FXML private Button mainBtn;
    @FXML private TableView<CustomerObj> customerTable;
    @FXML private TableColumn<CustomerObj, Integer> customerIDCol;
    @FXML private TableColumn<CustomerObj, String> nameCol;
    @FXML private TableColumn<CustomerObj, String> addressCol;
    @FXML private TableColumn<CustomerObj, String> postalCol;
    @FXML private TableColumn<CustomerObj, String> phoneCol;
    @FXML private TableColumn<CustomerObj, DivisionObj> divCol;
    @FXML private Label errorLabel;
    @FXML private Label errorLabel1;
    @FXML private Label currentTime12Label;
    @FXML private Label currentTime24Label;
    @FXML private Label currentDateLabel;
    @FXML private Label custRecordLabel;
    @FXML private ComboBox<CountryObj> countryBox;
    @FXML private ComboBox<DivisionObj> divisionBox;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField address;
    @FXML private TextField postal;
    @FXML private TextField phone;
    @FXML private TextField custID;
    @FXML private TextField searchField;
    @FXML private Pane firstNameError;
    @FXML private Pane lastNameError;
    @FXML private Pane addressError;
    @FXML private Pane phoneError;
    @FXML private Pane postalError;
    @FXML private Pane countryError;
    @FXML private Pane divisionError;
    private boolean modify = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setText("");
        errorLabel1.setText("");
        custRecordLabel.setText("NEW CUSTOMER");
        System.out.println("CUSTOMERS SCREEN");

        // Loads all items from SQL database
        try {
            DBConnection.loadUsersFromDatabase();
            DBConnection.loadAppointmentsFromDatabase();
            DBConnection.loadCountriesFromDatabase();
            DBConnection.loadDivisionsFromDatabase();
            DBConnection.loadContactsFromDatabase();
            DBConnection.loadCustomersFromDatabase();
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

        //loads country list items into comboboxes
        countryBox.setItems(CountryObj.countryList);

        //disables division box until country is selected
        divisionBox.setDisable(true);

        //Lambda expression continually updates current time and date label
        currentDateTimeLabel();

        // sets error graphics as disabled until error occurs
        firstNameError.setVisible(false);
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        addressError.setVisible(false);
        phoneError.setVisible(false);
        postalError.setVisible(false);
        countryError.setVisible(false);
        divisionError.setVisible(false);
    }

    // if called, loads the customer data into the form fields for modifying
    public void prefill (CustomerObj selectedCustomer) {
        modify = true;
        custID.setText(String.valueOf(selectedCustomer.getId()));
        firstName.setText(String.valueOf(selectedCustomer.getFirstName()));
        lastName.setText(String.valueOf(selectedCustomer.getLastName()));
        address.setText(String.valueOf(selectedCustomer.getAddress()));
        postal.setText(String.valueOf(selectedCustomer.getPostalCode()));
        phone.setText(String.valueOf(selectedCustomer.getPhone()));
        countryBox.setValue(selectedCustomer.getDivID().getCountry());
        divisionBox.setValue(selectedCustomer.getDivID());
    }

    // allows correlating divisions to be selected after country
    @FXML
    private void changeCountry(ActionEvent event) {
        CountryObj selected = countryBox.getValue();
        divisionBox.setDisable(false);
        divisionBox.setItems(DivisionObj.getDivisionByCountry(selected));
    }

    // searches customer table for first or last name matches
    @FXML
    private void searchBtnClicked(ActionEvent event) {
    }

    // saves new customer or modified customer depending on if update=true
    @FXML
    private void saveBtnClicked(ActionEvent event) {
        CustomerObj selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        // display error boxes if empty
        firstNameError.setVisible(firstName.getText().trim().isEmpty());
        lastNameError.setVisible(lastName.getText().trim().isEmpty());
        addressError.setVisible(address.getText().trim().isEmpty());
        phoneError.setVisible(phone.getText().trim().isEmpty());
        countryError.setVisible(countryBox.getSelectionModel().isEmpty());
        postalError.setVisible(postal.getText().trim().isEmpty());
        divisionError.setVisible(divisionBox.getSelectionModel().isEmpty());

        // check if empty
        if (firstName.getText().trim().isEmpty() ||
                lastName.getText().trim().isEmpty() ||
                address.getText().trim().isEmpty() ||
                postal.getText().trim().isEmpty() ||
                phone.getText().trim().isEmpty() ||
                countryBox.getSelectionModel().isEmpty() ||
                divisionBox.getSelectionModel().isEmpty()) {
            errorLabel1.setText("Enter data into all fields"); }
        else {
            int tempID = 0;

            if (custRecordLabel.getText().equals("MODIFY CUSTOMER")) tempID = Integer.parseInt(custID.getText());
            String tempName = firstName.getText() + " " + lastName.getText();
            String tempAdd = address.getText();
            String tempPost = postal.getText();
            String tempPhone = phone.getText();
            DivisionObj tempDiv = divisionBox.getValue();

            CustomerObj cus = new CustomerObj(tempID, tempName, tempAdd, tempPost, tempPhone, tempDiv);

            try {
                // modify existing customer
                if (custRecordLabel.getText().equals("MODIFY CUSTOMER")) {
                    DBConnection.modifyCustomer(cus);
                    errorLabel.setText("Customer [ID: " + selectedCustomer.getId() + ", name: " + tempName + "] was modified");
                    System.out.print("Customer [ID: " + selectedCustomer.getId() + ", name: " + tempName + "] was modified\n"); }

                // add new customer
                else {
                    DBConnection.addCustomer(cus);
                    errorLabel.setText("Customer [ID: " + tempID + ", name: " + tempName + "] was added");
                    System.out.print("Customer [ID: " + tempID + ", name: " + tempName + "] was added\n"); }
            } catch (SQLException e) {
                System.out.println("SQL error: " + e);
            }
            custRecordLabel.setText("NEW CUSTOMER");
            clearAllInputFields(); // change this to read error on screen
        }
    }

    // calls prefill method to send customer information to the field boxes
    @FXML
    private void modifyCustomerBtnClicked(ActionEvent event) {
        CustomerObj selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            errorLabel.setText("Select a customer to modify");
        } else {
            errorLabel.setText("");
            custRecordLabel.setText("MODIFY CUSTOMER");
            prefill(selectedCustomer);
            System.out.println("Modifying customer... ");
            clearAllErrorBoxes();
        }
    }

    // clears all form fields and errors
    @FXML
    void clearBtnClicked(ActionEvent event) {
        custRecordLabel.setText("NEW CUSTOMER");
        clearAllInputFields();
        clearAllErrorBoxes();
    }

    // deletes a customer after confirmation
    @FXML
    void deleteBtnClicked(ActionEvent event) throws SQLException {
        CustomerObj selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        // if no customer selected, error is displayed
        if (selectedCustomer == null) {
            errorLabel.setText("Select a customer to delete");

        // if customer is selected, alert asks for delete confirmation
        } else {
            errorLabel.setText("");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Customer [name: " + selectedCustomer.getName() + ", ID: " + selectedCustomer.getId() + "] and their existing appointments will be permanently deleted.");
            alert.setContentText("Do you want to continue?");
            Optional<ButtonType> result = alert.showAndWait();

            // if ok is clicked, customer is deleted
            if (result.get() == ButtonType.OK) {
                try {
                    //delete customer and their existing appointments
                    DBConnection.deleteAppointmentByCustomer(selectedCustomer);
                    DBConnection.deleteCustomer(selectedCustomer);
                } catch (SQLException e) {
                    System.out.println("SQL error: " + e);
                }
                clearAllInputFields();
                clearAllErrorBoxes();
                System.out.println("Customer [name: " + selectedCustomer.getName() + ", ID: " + selectedCustomer.getId() + "] was deleted");
                errorLabel.setText("Customer [name: " + selectedCustomer.getName() + ", ID: " + selectedCustomer.getId() + "] was deleted");

                // if  no appointments - and alert is cancelled or closed, customer is not deleted
            } else if (result.get() == ButtonType.CANCEL || result.get() == ButtonType.CLOSE) {
                System.out.println("Customer not deleted");
            }
        }
    }

    // takes user to main schedule screen
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
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        addressError.setVisible(false);
        phoneError.setVisible(false);
        postalError.setVisible(false);
        countryError.setVisible(false);
        divisionError.setVisible(false);
        errorLabel1.setText("");
    }

    // clears all text fields
    @FXML
    private void clearAllInputFields() {
        errorLabel1.setText("");
        customerTable.getSelectionModel().clearSelection();
        custID.clear();
        firstName.clear();
        lastName.clear();
        address.clear();
        postal.clear();
        phone.clear();
        countryBox.setValue(null);
        divisionBox.setDisable(true);
    }

    private void setTableCells() {
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divCol.setCellValueFactory(new PropertyValueFactory<>("divID"));
        customerTable.setItems(CustomerObj.customerList);
    }
}
