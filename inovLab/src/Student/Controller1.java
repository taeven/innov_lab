package Student;
import handling.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Created by ashutoshiit on 2/16/2017.
 */
    public class Controller1 implements Initializable{
    @FXML
    public TextField roll_no;

    @FXML
    public Label username;

    @FXML
    public Label check_in;

    @FXML
    public Label out;

    @FXML
    private Label date;

    @FXML
    public ProgressBar progress;

    @FXML  private Label status;


    private Database db;

//    public static Statement stmt = null;



    public void display_details()
    {



        String roll = roll_no.getText();
        if(roll.contentEquals("") )
        {
            status.setText("Enter Roll no. ");
        }
        else
        {
            progress.setVisible(true);
            String user ="SELECT * FROM data WHERE username = '"+
                    roll+"'";
            ResultSet rs;
            try {
                rs = db.stmt.executeQuery(user);
                if(rs.next()) {
                    username.setText(rs.getString("username"));
                    check_in.setText(rs.getString("in_time"));
                    out.setText(rs.getString("out_time"));



                    date.setText(rs.getString("date"));
                    status.setText("");
                }else
                {
                    username.setText("");
                    check_in.setText("");
                    out.setText("");
                    date.setText("");
                    status.setText("invalid roll no. or entry doesn't exist");
                }
                progress.setVisible(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        db= new Database();
    }
}
