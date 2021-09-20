package Main;

import Utils.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle rb = ResourceBundle.getBundle("Language/Lang", Locale.getDefault());
        if(Locale.getDefault().getLanguage().equals("en") || Locale.getDefault().getLanguage().equals("en")) {
            System.out.println(rb.getString("SchedulingProgram"));
        }
        Parent root = FXMLLoader.load(getClass().getResource("../View/LoginScreen.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 1203, 663));
        primaryStage.show();
        DBConnection.startConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
