package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReportObj {
    public static ObservableList<ReportObj> typeReport = FXCollections.observableArrayList();


    private String month;
    private String type;
    private String amount;

    public ReportObj(String month, String type, String amount) {
        this.month = month;
        this.type = type;
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }
}