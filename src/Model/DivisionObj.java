package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DivisionObj {
    public static ObservableList<DivisionObj> divisionList = FXCollections.observableArrayList();

    private final int divisionID;
    private final String divisionName;
    private final CountryObj countryID;

    public DivisionObj(int divisionID, String divisionName, CountryObj countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }
    public int getDivisionID() { return divisionID; }

    public String getName() {
        return this.divisionName;
    }

    public CountryObj getCountry() {
        return this.countryID;
    }

    public static ObservableList<DivisionObj> getDivisionByCountry(CountryObj country) {
        ObservableList<DivisionObj> temp = FXCollections.observableArrayList();
        int i = 0;
        while (i < divisionList.size()) {
            if (country == divisionList.get(i).getCountry())
                temp.add(divisionList.get(i));
            i++;
        }
        return temp;
    }

    public static DivisionObj getDivisionByID(int ID) {
        int i = 0;
        while (i < divisionList.size()) {
            if (ID == divisionList.get(i).getDivisionID())
                return divisionList.get(i);
            i++;
        }
        return null;
    }

    // get division by country
    @Override
    public String toString() {
        return(divisionName);
    }
}
