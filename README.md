# DateSaver
A desktop scheduling program used to maintain a MySQL database of customers and appointments with functions that allow each to be added, updated, or deleted.

<br><p align="center">
  <kbd>
<img src="capture-2.gif" alt="ShedulingProgram" style="border:5px solid grey"></img>
  </kbd>
</p><br>
Author: Alyssa Weiglein<br>
Contact: aweigle@wgu.edu<br>
Version: 2.0<br>
Date: 09/20/2021<br>

### Setting up the program:
IDE: IntelliJ Community 2020.03<br>
JDK: Java SE 11.0.9<br>
JavaFX: JavaFX-SDK-11.0.2<br>
MySQL Driver: mysql-connector-java-8.0.23<br>

### Running the program:
1. The program starts at a login screen where the user enters a username and password.<br>
   * Username: `test` <br>
   * Password: `test` <br>
        
2. Upon successful login, user will arrive at the appointment scheduling screen, where appointments can be created, modified, or deleted.
   <br><ins>APPOINTMENT SCREEN</ins>
   * Appointments can be filtered by 'All', 'Week', or 'Month', and will display a date label that corresponds to the selected radio button. 
   * Errors show in the bottom left corner.
   * Upcoming appointments show in the bottom middle label.
   * Current date, time, and a logout button is in the bottom right corner.
   * Reports can be viewed with the buttons in the top right corner.
      
3. Upon clicking the customer button, user will arrive at the customer database screen, where customers can be created, modified, or deleted.
   <br><ins>CUSTOMER SCREEN</ins>
   * Errors show in the bottom left corner.
   * Upcoming appointments show in the bottom middle label.
   * Current date, time, and a logout button is in the bottom right corner.
   * Reports can be viewed with the buttons in the top right corner.

### Reporting:
1. Requirement: Appointment count grouped by <b>type & month</b>.
2. Requirement: Appointments grouped by <b>contact</b> (with appointment ID, title, type, description, start date/time, end date/time, customer ID).
3. My third report choice: A display of the appointment types as a pie chart.
4. Additional report: Total successful and failed login attempts. 

### Lambda expressions:
1. Found at [Util.DBConnection] Line: 75
   * Lambda expression checks if the program is running.
   * Boolean 'checkUserData' checks that the username and password are in the database and authorizes the connection if true.
2. Found at [ViewController.AppointmentScreenController] Line: 118 & at [ViewController.CustomerScreenController] Line: 118
   * Lambda expression continually updates current time and date label.
