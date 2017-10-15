import com.pi4j.io.serial.*;
import handling.Database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vaibhav on 4/12/2017.
 */
public class registerForm {
    private JTextField nameField;
    private JTextField usernameField;
    private JPanel usernamePane;
    private JTextField roomField;
    private JTextField phoneField;
    private JButton fingerprntButton;
    private JProgressBar progress;
    public JPanel MainPanel;
    private JLabel status;
    private JTextField adharfield;
    private JTextField rollField;


    private Database db;
    private String name,username, phone, room, adhar, roll;
    private pi objPi;
    private Serial serial ;

    public registerForm(Database db,Serial serial,pi objPi)
    {
        progress.setIndeterminate(true);
        progress.setVisible(false);
        this.db = db;
        this.objPi = objPi;
        this.serial = serial;
        setListener();

    }

    private void setListener()
    {
        fingerprntButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objPi.reciever = " ";
                progress.setVisible(true);
                fingerprntButton.setEnabled(false);
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int count =-1;
                        if (checkCredentials()) {
                            try {
                                Boolean present = false;
                                String checkUserQuery = "select username from profile_data where username like '" + username + "';";
                                ResultSet resultSet = db.stmt.executeQuery(checkUserQuery);
                                if (resultSet.next()) {
                                    present = true;

//                                    if(newOne.wasNull())
//                                    {
//                                        System.out.println("is last");
//
//                                    }
//                                    else
//                                    {
//                                        System.out.println("isn't last");
//                                        System.out.println(newOne.toString());
//                                    }
//
//                                        count = newOne.getInt("count(*)");
//                                        System.out.println("got result" + count );
//
//

                                }
                                if (present) {
                                    status.setText("Username already exist!!");
                                } else {


                                    String countUser = "select count(*) from profile_data";
                                    ResultSet newOne = db.stmt2.executeQuery(countUser);
                                    status.setText("ready for fingerprint ");
                                    if(newOne.next())
                                    {
                                        count = newOne.getInt("count(*)");
                                        System.out.println("got result" + count );

                                    }


                                    // handed over to register finger;
                                    if(serial.isOpen())
                                    registerFinger(count);

                                }

                            } catch (SQLException e1) {
                                e1.printStackTrace();
                                status.setText("Error connecting to server!!");
                                System.out.println("error in sql ");
                            }


                        }
                        progress.setVisible(false);
                        fingerprntButton.setEnabled(true);
                    }


                });

                th.start();
            }
        });
//        setSerialListener();
    }

    private void registerFinger(int count)
    {
        try {
            serial.write('E');
            serial.write("Count"+(count+1));
            System.out.println("Count"+(count+1));
            while(true)
            {
                if(objPi.reciever.contains("Remove"))
                {
                    status.setText("Remove the finger!!");
                }

                if(objPi.reciever.contains("same finger"))
                {
                    status.setText("place same finger again!!");
                }
                if (objPi.reciever.contains("did not match"))
                {
                    status.setText("Fingerprint did not Matched!!");
                    break;
                }
                if(objPi.reciever.contains("Stored"))
                {
                    status.setText("Fingerprint stored");
                    //handed over to server part
                    doEntryDatabase(count);
                    break;
                }

            }
        } catch (IOException e) {
            status.setText("Sensor not available!!");
            e.printStackTrace();
        }
    }

    private void doEntryDatabase(int count)
    {

        String query = "INSERT INTO `profile_data`(`name`, `username`, `room_no`, `phone_no`, `id`, `adhar`, `roll`)"+
                " VALUES ('"+name+"','"+username+"','"+room+"','"+phone+"',"+ (count+1) +",'" +adhar+"','"+ roll+"')";
        try {

            db.stmt.executeUpdate(query);
            status.setText("Entry done for user "+username);
            clearAll();

        } catch (SQLException e) {
            status.setText("connection error! try again!!");
            e.printStackTrace();
        }
    }

    private void clearAll()
    {
        rollField.setText("");
        nameField.setText("");
        usernameField.setText("");
        adharfield.setText("");
        phoneField.setText("");
        roomField.setText("");
    }

    private boolean  checkCredentials()
    {
        roll = rollField.getText();
         adhar=adharfield.getText();
         name = nameField.getText();
         username = usernameField.getText();
         phone = phoneField.getText();
         room = roomField.getText();
        if(name.isEmpty() || username.isEmpty() || phone.isEmpty())
        {
            status.setText("* fields should not be empty");
            return false;
        }
        if(phone.length()!=10)
        {
            status.setText("Invalid phone no.!!");
            return false;
        }

        try
        {
            Long.parseLong(phone);
        }catch (Exception ex)
        {
            status.setText("Invalid phone no.!!");
            return false;
        }
        try{
            if (!adhar.isEmpty()) {
                if (adhar.length()!=12)
                    throw new Exception();
                Long.parseLong(adhar);
            }
        }
        catch (Exception ex){
            status.setText("Invalid Aadhar no.!!");
            ex.printStackTrace();
        }

        return true;
    }


//    private void setSerialListener()
//    {
//
//        serial.addListener(new SerialDataEventListener() {
//            @Override
//            public void dataReceived(SerialDataEvent serialDataEvent) {
//                try {
//                    String data = serialDataEvent.getAsciiString();
//                    if(data.contains("same finger") || data.contains("matched") || data.contains("did not match") || data.contains("Stored") || data.contains("Remove"))
//                    {
//                        received = data;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//    }
}
