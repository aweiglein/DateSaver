# DateSaver
 A desktop program used to maintain a MySQL database of customers and appointments with functions that allow each to be added, updated, or deleted.
 <br><br>
 Author: Alyssa Weiglein<br>
 Contact: aweigle@wgu.edu<br>
 Version: 2.0<br>
 Date: 07/30/2021<br>

### Setting up the program:
IDE: IntelliJ Community 2020.03<br>
JDK: Java SE 11.0.10<br>
JavaFX: JavaFX-SDK-11.0.2<br>
MySQL Driver: mysql-connector-java-8.0.23<br>

### Navigating the program:
On startup, the program will make a connection to the MySQL database where the customers and appointments data is stored. After a successful connection, the login screen will detect the users timezone and system language, and translate all error control labels to their language (English, Spanish, or French). The user then needs to enter a username and password to be validated.<br>
    * Menu buttons include: Appointments screen, Customers screen, and Reports screen.<br>
    * The user's current date and time (12hr & 24hr) is updated in real-time and displayed at the top right of the window.<br>
    * The appointment/customer form can be used to make a new record or update an exisiting one.<br>
    * Error notifications are located at the bottom of the screen, along with a logout button that sends the user back to the login page.<br>

### Running the program:
1. On startup, the program will make a connection to the MySQL database where the customers and appointments data is stored. After a successful connection, the login screen will detect the users timezone and system language, and translate all error control labels to their language (English, Spanish, or French). The user then needs to enter a username and password to be validated.<br>
    Username | Password
    ---------|----------
    `test`  | `test`
    `admin` | `admin`

2. The 'appointment' menu button will take the user to the <ins>APPOINTMENT SCREEN</ins> where appointments can be created, modified, or deleted.
   <br>
    * The user is alerted of upcoming appointments within 15 minutes of their local time -- alert includes appointment ID, type, and date.
    * The appointment table view displays all appointments which can be filtered by 'All', 'Week', or 'Month' and once selected, will display a date range label for that view period and display the filtered appointments in the table view.
    * Each existing appointment can be updated by selecting it then modifying the information in the appointment record form, or, the appointments can be deleted after confirmation.
    * When making a new appointment, the user must enter data in all fields to get past the error notifications. Appointments are created and displayed in the users local time zone, but stored in the database in UTC timezone, and checked against business hours in EST timezone.
    * Exception handling displays errors for invalid input data, appointment times that are outside of business hours, and for appointments that are overlapping.

3. The 'customer' menu button will take the user to the <ins>CUSTOMER SCREEN</ins> where customers can be created, modified, or deleted.
   <br>
    * The customer table view displays all customers that exist in the database.
    * Each existing customer can be updated by selecting it then modifying the information in the customer record form, or, the customers can be deleted after confirmation.
    * When making a new customer, the user must enter data in all fields to get past the error notifications. Customers are created and displayed in the users local time zone, but stored in the database in UTC timezone, and checked against business hours in EST timezone.
    * For the customers address, the country must be selected before the region to filter and display the correlating region options. 
    * Exception handling displays errors for invalid input data.
    
4. The 'reports' menu button will take the user to the <ins>REPORTS SCREEN</ins> where the user can find program reporting information.
   <br>
    * The contact table view displays all appointments that exist in the database for a selected contact.
    * Each of the other reports are displayed when the reports window is initialized.

### Reporting:
1. <b>Requirement</b>: Appointment count grouped by <b>type & month</b>.
2. <b>Requirement</b>: Appointment Schedule for each <b>contact</b> (with appointment ID, title, type, description, start date/time, end date/time, customer ID)
3. <b>My 3rd report choice</b>: PieChart of appointments grouped by type.
4. Additional report: Total number of successful and failed login attempts.

### Lambda expressions:
1. Found at [Util.DBConnection] Line: 75
    * Lambda expression checks if the program is running.
    * Boolean 'checkUserData' checks that the username and password are in the database and authorizes the connection if true.
2. Found at [ViewController.AppointmentScreenController] Line: 118 & at [ViewController.CustomerScreenController] Line: 118
    * Lambda expression continually updates current time and date label.
