package Utils;
import Model.*;

import ViewController.LoginScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DBConnection {
    public static DBConnection database;
    // JDBC URL parts
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//localhost:3306/";
    private static final String dbName = "client_schedule";
    private static final String convertTimeZone = "?connectionTimeZone=SERVER";

    // JDBC URL
    private static final String databaseURL = protocol + vendorName + ipAddress + dbName + convertTimeZone;

    // Driver interface reference
    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";
    public static Connection conn = null;

    // Username and password
    private static final String username = "root";
    private static final String password = "mysC@RT08";
    private static int verifiedUserID;
    private static String verifiedUserLogin;

    // Starts database connection
    /**
     * 'startConnection' uses the specified database connection input to connect the database.
     * @return Connection
     */
    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(databaseURL, username, password);
            System.out.println("Connected *******************");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return conn;
    }

    // Calls connection without re-starting it
    public static Connection getConnection(){
        return conn;
    }

    // Closes database connection
    /**
     * 'closeConnection' closes the database connection.
     */
    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection closed");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Verifies username and password combinations
    public static Boolean verifyLogin(String user, String pw) throws SQLException {
        System.out.print("\tVerifying username and password... ");

        String sql = "SELECT * FROM users WHERE User_Name='" + user + "' AND Password='" + pw + "';";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);

        if(rs.next()) {
            if(rs.getString("User_Name").equals(user) && rs.getString("password").equals(pw)) {
                System.out.print("✔\n");
                verifiedUserID = rs.getInt("User_ID");
                verifiedUserLogin = rs.getString("User_Name");

                // Returns TRUE if username and password match
                return true;
            }
        }
        // Returns FALSE if username and password don't match
        System.out.print("✘\n");
        return false;
    }

    // gets list of all users from MySQL
    public static void loadUsersFromDatabase() throws SQLException {
        String sql = "SELECT * FROM users;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);

        // clears list to prepare for item count
        UserObj.userList.clear();

        while(rs.next()) {
            UserObj.userList.add
                    (new UserObj(rs.getInt("User_ID"),
                            rs.getString("User_Name"),
                            rs.getString("Password")));
        }
    }

    // gets list of all appointments from MySQL
    public static void loadAppointmentsFromDatabase() throws SQLException {
        String sql = "SELECT * FROM appointments;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);

        // clears list to prepare for item count
        AppointmentObj.appointmentList.clear();

        while(rs.next()) {
            AppointmentObj.appointmentList.add(
                    new AppointmentObj(rs.getInt("Appointment_ID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("Location"),
                            ContactObj.getContactByID(rs.getInt("Contact_ID")),
                            rs.getString("Type"),
                            rs.getString("Start"),
                            rs.getString("End"),
                            CustomerObj.getCustomerByID(rs.getInt("Customer_ID")),
                            UserObj.getUserByID(rs.getInt("User_ID"))));
        }
    }

    // gets list of all countries from MySQL
    public static void loadCountriesFromDatabase() throws SQLException {
        String sql = "SELECT * FROM countries;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);

        // clear list
        CountryObj.countryList.clear();

        while(rs.next()) {
            CountryObj.countryList.add
                    (new CountryObj(rs.getInt("Country_ID"),
                            rs.getString("Country")));
        }
    }

    // gets list of all divisions from MySQL
    public static void loadDivisionsFromDatabase() throws SQLException {
        String sql = "SELECT * FROM first_level_divisions;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);

        // clear list
        DivisionObj.divisionList.clear();

        while(rs.next()) {
                DivisionObj.divisionList.add
                    (new DivisionObj(rs.getInt("Division_ID"),
                            rs.getString("Division"),
                            CountryObj.getCountryById(rs.getInt("Country_ID"))));

        }
    }

    // gets list of all contacts from MySQL
    public static void loadContactsFromDatabase() throws SQLException {
        String sql = "SELECT * FROM contacts;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);

        // clear list
        ContactObj.contactList.clear();

        while(rs.next()) {
            ContactObj.contactList.add
                    (new ContactObj(rs.getInt("Contact_ID"),
                            rs.getString("Contact_Name")));
        }
    }

    // gets list of all customers from MySQL
    public static void loadCustomersFromDatabase() throws SQLException {
        String sql = "SELECT * FROM customers;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        // clear list
        CustomerObj.customerList.clear();

        while(rs.next()) {
            CustomerObj.customerList.add
                    (new CustomerObj(rs.getInt("Customer_ID"),
                            rs.getString("Customer_Name"),
                            rs.getString("Address"),
                            rs.getString("Postal_Code"),
                            rs.getString("Phone"),
                            DivisionObj.getDivisionByID(rs.getInt("Division_ID"))));
        }
    }

    // gets list of appointments grouped by month and type from MySQL
    public static void loadReportByType() throws SQLException {
        String sql = "SELECT MonthName(Start) AS monthName , Type AS typeName, COUNT(*) AS amount FROM appointments GROUP BY MONTHNAME(Start), Type;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        // clear list
        ReportObj.typeReport.clear();

        while (rs.next()) {
            String month = rs.getString("monthName");
            String type = rs.getString("typeName");
            String amount = rs.getString("amount");
            ReportObj.typeReport.add(new ReportObj(month, type, amount));
        }
    }

    // adds new customer into MySQL
    public static void addCustomer(CustomerObj customer) throws SQLException {
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Created_By, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, customer.getName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getPostalCode());
        ps.setString(4, customer.getPhone());
        ps.setString(5, LoginScreenController.verifiedUser);
        ps.setString(6, LoginScreenController.verifiedUser);
        ps.setInt(7, customer.getDivID().getDivisionID());
        ps.executeUpdate();

        loadCustomersFromDatabase();
    }

    // updates existing customer in MySQL
    public static void modifyCustomer(CustomerObj customer) throws SQLException {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = NOW(), Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, customer.getName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getPostalCode());
        ps.setString(4, customer.getPhone());
        ps.setString(5, LoginScreenController.verifiedUser);
        ps.setInt(6, customer.getDivID().getDivisionID());
        ps.setInt(7, customer.getId());
        ps.executeUpdate();

        loadCustomersFromDatabase();
    }

    // deletes customer from MySQL
    public static void deleteCustomer(CustomerObj customer) throws SQLException {
        String sql = "DELETE FROM customers WHERE Customer_ID = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, customer.getId());
        ps.executeUpdate();

        loadCustomersFromDatabase();
    }

    // adds new appointment into MySQL
    public static void addAppointment(AppointmentObj appt) throws SQLException {
        String sql = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?);";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, appt.getAppointmentID());
        ps.setString(2, appt.getAppTitle());
        ps.setString(3, appt.getAppDescription());
        ps.setString(4, appt.getAppLocation());
        ps.setString(5, appt.getAppType());
        ps.setString(6, appt.getStartDateUTC());
        ps.setString(7, appt.getEndDateUTC());
        ps.setString(8, verifiedUserLogin);
        ps.setString(9, verifiedUserLogin);
        ps.setInt(10, appt.getCustomerID().getId());
        ps.setInt(11, verifiedUserID);
        ps.setInt(12, appt.getContactID().getId());
        ps.executeUpdate();

        loadAppointmentsFromDatabase();
    }

    // updates existing appointment in MySQL
    public static void modifyAppointment(AppointmentObj appt) throws SQLException {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = NOW(), Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?;";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, appt.getAppTitle());
        ps.setString(2, appt.getAppDescription());
        ps.setString(3, appt.getAppLocation());
        ps.setString(4, appt.getAppType());
        ps.setString(5, appt.getStartDateUTC());
        ps.setString(6, appt.getEndDateUTC());
        ps.setString(7, verifiedUserLogin);
        ps.setInt(8, appt.getCustomerID().getId());
        ps.setInt(9, appt.getUserID().getId());
        ps.setInt(10, appt.getContactID().getId());
        ps.setInt(11, appt.getAppointmentID());
        ps.executeUpdate();

        loadAppointmentsFromDatabase();
    }

    // deletes appointment from MySQL
    public static void deleteAppointment(AppointmentObj appointment) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, appointment.getAppointmentID());
        ps.executeUpdate();

        loadAppointmentsFromDatabase();
    }

    // deletes customers appointments from MySQL when deleting a selected customer
    public static void deleteAppointmentByCustomer(CustomerObj customer) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Customer_ID = ?;";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, customer.getId());
        ps.executeUpdate();

        loadAppointmentsFromDatabase();
    }

    public static void updateAppointmentList(String timeStart, String timeEnd) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE (End BETWEEN ? AND ?);";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, timeStart);
        ps.setString(2, timeEnd);
        ResultSet rs = ps.executeQuery();

        AppointmentObj.appointmentList.clear();

        while(rs.next()) {
            AppointmentObj.appointmentList.add(
                    new AppointmentObj(rs.getInt("Appointment_ID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("Location"),
                            ContactObj.getContactByID(rs.getInt("Contact_ID")),
                            rs.getString("Type"), rs.getString("Start"),
                            rs.getString("End"),
                            CustomerObj.getCustomerByID(rs.getInt("Customer_ID")),
                            UserObj.getUserByID(rs.getInt("User_ID"))));
        }
    }

    //check for overlapping
    public static ObservableList<AppointmentObj> getAppointmentList(String timeStart, String timeEnd) throws SQLException {
        ObservableList<AppointmentObj> appointmentList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments WHERE (Start BETWEEN ? AND ?);";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, timeStart);
        ps.setString(2, timeEnd);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            AppointmentObj.appointmentList.add(
                    new AppointmentObj(rs.getInt("Appointment_ID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("Location"),
                            ContactObj.getContactByID(rs.getInt("Contact_ID")),
                            rs.getString("Type"),
                            rs.getString("Start"),
                            rs.getString("End"),
                            CustomerObj.getCustomerByID(rs.getInt("Customer_ID")),
                            UserObj.getUserByID(rs.getInt("User_ID"))));
        }
        System.out.println("\t\tAppointment list\tITEM COUNT: " + AppointmentObj.appointmentList.size());
        return appointmentList;
    }

    public static void updateAppointmentListByStart(String timeStart, String timeEnd) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE (Start BETWEEN ? AND ?);";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, timeStart);
        ps.setString(2, timeEnd);
        ResultSet rs = ps.executeQuery();

        AppointmentObj.appointmentList.clear();

        while(rs.next()) {
            AppointmentObj.appointmentList.add(
                    new AppointmentObj(rs.getInt("Appointment_ID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("Location"),
                            ContactObj.getContactByID(rs.getInt("Contact_ID")),
                            rs.getString("Type"),
                            rs.getString("Start"),
                            rs.getString("End"),
                            CustomerObj.getCustomerByID(rs.getInt("Customer_ID")),
                            UserObj.getUserByID(rs.getInt("User_ID"))));
        }
    }
}
