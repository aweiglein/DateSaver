package ViewController;

import Utils.DBConnection;
import Utils.Log;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;


public class LoginScreenController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML private Button loginBtn;
    @FXML private Button clearBtn;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Label errorLabel;
    @FXML private Label languageLabel;
    @FXML private Label locationLabel;
    @FXML private ImageView USAFlag;
    @FXML private ImageView mexicoFlag;
    public static String verifiedUser;

    ResourceBundle rb = ResourceBundle.getBundle("Language/Lang", Locale.getDefault());

    @Override
    public void initialize(URL url, ResourceBundle rb)  {
        rb = ResourceBundle.getBundle("Language/Lang", Locale.getDefault());
        ZoneId id = ZoneId.systemDefault();

        USAFlag.setVisible(false);
        mexicoFlag.setVisible(false);
        errorLabel.setText("");
        username.setPromptText(rb.getString("Username"));
        password.setPromptText(rb.getString("Password"));
        loginBtn.setText(rb.getString("Login"));
        clearBtn.setText(rb.getString("Clear"));
        locationLabel.setText(id.toString());
        languageLabel.setText(rb.getString("SysLang"));

        String country = Locale.getDefault().getDisplayCountry();
        if(country.equals("United States")) {
            USAFlag.setVisible(true);
        } else if(country.equals("Mexico")) {
            mexicoFlag.setVisible(true);
        }
        System.out.println("System country: " + Locale.getDefault().getDisplayCountry());
        System.out.println("System language: " + rb.getString("SysLang"));
    }

    @FXML
    private void loginBtnClicked(ActionEvent event) throws IOException, SQLException {
        System.out.println("Logging in...");
        if (DBConnection.verifyLogin(username.getText().toLowerCase(), password.getText())) {

            verifiedUser = username.getText();

            Log.write("\t[username: " + username.getText() + "] \tlogin SUCCESS\n");

            // Lambda expression checks if JavaFX Application Thread is running
            Runnable runProcess = () -> System.out.println("\t" + Thread.currentThread().getName() + " is running");
            runProcess.run();

            try {
                Parent root = FXMLLoader.load(getClass().getResource("../View/MainScreen.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Scheduling Program - Appointments");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
        // change this window
                ((Node) (event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                System.out.println("SQL error: " + e);
            }
        } else if (username.getText().equals("")) {
            Log.write("\t[username: " + username.getText() + "] \t\tlogin FAIL\n");
            errorLabel.setText(rb.getString("FailedLogin"));
        } else {
            Log.write("\t[username: " + username.getText() + "] \tlogin FAIL\n");
            errorLabel.setText(rb.getString("FailedLogin"));
        }
    }

    @FXML
    private void clearBtnClicked(ActionEvent event) {
        username.clear();
        password.clear();
        errorLabel.setText("");
    }
}
