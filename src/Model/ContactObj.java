package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContactObj {
    public static ObservableList<ContactObj> contactList = FXCollections.observableArrayList();

    private int id;
    private String name;

    public ContactObj(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() { return id; }
    public String getName() { return name; }

    public static ContactObj getContactByID(int ID) {
        int i = 0;
        while(i < contactList.size()) {
            if(ID == contactList.get(i).getId()) return contactList.get(i);
            i++;
        }
        return null;
    }
    @Override
    public String toString() {
        return this.getName();
    }
}
