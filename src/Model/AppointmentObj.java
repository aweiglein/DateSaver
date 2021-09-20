package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class AppointmentObj {
    public static ObservableList<AppointmentObj> appointmentList = FXCollections.observableArrayList();

    private int appointmentID;
    private String appTitle;
    private String appDescription;
    private String appLocation;
    private ContactObj contactID;
    private String appType;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private CustomerObj customerID;
    private UserObj userID;


    public AppointmentObj(int appointmentID, String appTitle, String appDescription, String appLocation, ContactObj contactID, String appType, String startDate, String endDate, CustomerObj customerID, UserObj userID) {
        this.appointmentID = appointmentID;
        this.appTitle = appTitle ;
        this.appDescription = appDescription;
        this.appLocation = appLocation;
        this.contactID = contactID;
        this.appType = appType;
        this.startDate = ZonedDateTime.of(LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("UTC+0"));
        this.endDate = ZonedDateTime.of(LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("UTC+0"));
        this.customerID = customerID;
        this.userID = userID;
    }

    public int getAppointmentID() {
        return appointmentID;
    }
    public String getAppTitle() {
        return appTitle;
    }
    public String getAppDescription() {
        return appDescription;
    }
    public String getAppLocation() {
        return appLocation;
    }
    public ContactObj getContactID() { return contactID; }
    public String getAppType() { return appType; }

    /////////////////////////////
    public String getStartDate() {
        ZonedDateTime localTime = this.startDate.withZoneSameLocal(ZoneId.of("UTC+0"));
        return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public ZonedDateTime getStartDateObj() {
        return this.startDate;
    }

    public String getStartDateUTC() {
        return this.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    //////////////////////////////
    public String getEndDate() {
        ZonedDateTime localTime = this.endDate.withZoneSameLocal(ZoneId.of("UTC+0"));
        return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public ZonedDateTime getEndDateObj() {
        return this.endDate;
    }

    public String getEndDateUTC() {
        return this.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public CustomerObj getCustomerID() { return customerID; }
    public UserObj getUserID() { return userID; }

    public static ObservableList<AppointmentObj> getAllAppointments() {
        return appointmentList;
    }

    public static Predicate<AppointmentObj> appointmentDateIntPredicate(Integer monthValue) {
        return Appointment -> Appointment.getStartDateObj().getMonthValue() == monthValue;
    }
}
