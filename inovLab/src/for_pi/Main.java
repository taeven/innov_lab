package for_pi;/**
 * Created by vaibhav on 12/2/17.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root, 300, 200));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
