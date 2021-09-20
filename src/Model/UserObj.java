package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserObj {
    public static ObservableList<UserObj> userList = FXCollections.observableArrayList();

    private int id;
    private String name;
    private String password;

    public UserObj(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }

    public static UserObj getUserByID(int ID) {
        int i = 0;
        while(i < userList.size()) {
            if(ID == userList.get(i).getId()) return userList.get(i);
            i++;
        }
        return null;
    }
    @Override
    public String toString() {
        return this.getName();
    }
}