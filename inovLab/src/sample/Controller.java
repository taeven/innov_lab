package sample;


import handling.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    Database database;
   public Controller()
    {

    }
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private ProgressBar progress;

    @FXML
    private Button login_but;


    public void user_clicked()
    {
        password.requestFocus();
    }


    public void perform_login()
    {
        String usr = username.getText();
        String pass= password.getText();
        System.out.println(usr + " " + pass);
        progress.setVisible(true);

        if(database!=null)
        {
            switch (database.check_credentials(usr,pass))
            {
                case 1:
                    System.out.println("1");

                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("/adminView/sample.fxml"));
                        Stage primaryStage = new Stage();
                        primaryStage.setScene(new Scene(root, 600, 400));
                        primaryStage.setTitle("Login");
                        primaryStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    progress.setVisible(false);

                    break;
                case -1:
                    System.out.println("-1");

                    progress.setVisible(false);
                    break;
                case 0:

                    try {
                        Parent other = null;
                        other = FXMLLoader.load(getClass().getResource("/Student/sample1.fxml"));
                        Stage primaryStage = new Stage();
                        primaryStage.setTitle("CheckIn-CheckOut");
                        primaryStage.setScene(new Scene(other, 600, 400));
                        primaryStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("0");

                    progress.setVisible(false);
                    break;

            }
        }else
        {

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Database.checkConn())
            database = new Database();

    }
}
