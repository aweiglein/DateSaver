package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CountryObj {
    public static ObservableList<CountryObj> countryList = FXCollections.observableArrayList();

    private int id;
    private String name;

    public CountryObj(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() { return id; }
    public String getName() { return name; }

    public static CountryObj getCountryById(int ID) {
        int i = 0;
        while (i < countryList.size()) {
            if (ID == countryList.get(i).id)
                return countryList.get(i);
            i++;
        }
        return null;
    }
    @Override
    public String toString() {
        return this.name;
    }
}
