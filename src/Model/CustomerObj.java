package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomerObj {
    public static ObservableList<CustomerObj> customerList = FXCollections.observableArrayList();

    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private DivisionObj divID;

    public CustomerObj(int id, String name, String address, String postalCode, String phone, DivisionObj divID) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divID = divID;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPostalCode() { return postalCode; }
    public String getPhone() { return phone; }
    public DivisionObj getDivID() { return divID; }

    public static CustomerObj getCustomerByID(int ID) {
        int i = 0;
        while (i < customerList.size()) {
            if (ID == customerList.get(i).getId()) return customerList.get(i);
            i++;
        }
        return null;
    }

    public String getFirstName() {
        int firstSpace = name.indexOf(" ");
        return name.substring(0, firstSpace);
    }

    public String getLastName() {
        int firstSpace = name.indexOf(" ");
        return name.substring(firstSpace).trim();
    }

    // combines firstName and lastName to string
    @Override
    public String toString() {
        return this.name;
    }
}
