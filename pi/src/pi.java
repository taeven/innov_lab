import com.pi4j.io.serial.*;
import handling.Database;
import structures.data;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vaibhav on 4/3/2017.
 */
public class pi {
    private JButton inButton;
    private JButton outButton;
    private JLabel status;
    private JPanel mainPanel;
    private JPanel buttonPane;
    public static String reciever = " ";
    private Database db ;
    private JPanel labelPane;
    private JProgressBar progress;
    private JButton registerButton;
    private JLabel headerLabel;
    final Serial serial = SerialFactory.createInstance();

    public pi() {
        db = new Database();
        db.get_connection();
        progress.setIndeterminate(true);
        progress.setEnabled(true);
        progress.setVisible(false);
      //  progress.setVisible(true);

        /////add runnable thread......
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!serial.isOpen())
                    openSerialConn();
            }
        });
        th.start();

     setListeners();
    }

    public void sendComm(String which)  {
        progress.setVisible(true);
        inButton.setEnabled(false);
        outButton.setEnabled(false);
        registerButton.setEnabled(false);


        try {
            serial.write('R');

        reciever = " ";
        Thread th =new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    if(reciever.contains("Found ID") )
                    {
                        String tmp = reciever.substring(reciever.indexOf('#')+1,reciever.lastIndexOf('#'));

                        // create data set for system date and time and send it to myaql server.......
                        //done

                        doEntryDatabase(Integer.parseInt(tmp),which);


                         //status.setText(tmp);
                        break;
                    }
                    else if (reciever.startsWith("Did not find"))
                    {
                        status.setText(reciever);
                        break;
                    }
                    else if(reciever.contains("msg-arduino"))
                    {
                        status.setText("can't detect any fingerprint!! Try Again!");
                        break;
                    }

                    status.setText("place your finger... ");

                }

                inButton.setEnabled(true);
                registerButton.setEnabled(true);
                outButton.setEnabled(true);
                progress.setVisible(false);

        }
        });
        th.start();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[])
    {
        JFrame frame = new JFrame("Check In/Out ");
        frame.setContentPane(new pi().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,500);
        frame.pack();
        frame.setVisible(true);
    }


    private void setListeners()
    {



        inButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if (db.checkConn()) {
                    //to check whether stament is made
                    if (db.stmt==null) {
                        //get connection if not
                        if (db.get_connection())
                        {
                            sendComm("in_time");
                        }
                        else
                            status.setText("Error connecting to the server!! \n try again!!");

                    }//if statement is made already
                    else
                    {
                        sendComm("in_time");
                    }

                }
            }

        });

        outButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (db.checkConn()) {
                    //to check whether stament is made
                    if (db.stmt==null) {
                        //get connection if not
                        if (db.get_connection())
                        {
                            sendComm("out_time");
                        }
                        else
                            status.setText("Error connecting to the server!! \n try again!!");

                    }//if statement is made already
                    else
                    {
                        sendComm("out_time");
                    }

                }

            }

        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

    }

    private void openSerialConn()
    {
        try {
            SerialConfig config = new SerialConfig();
            config.device("/dev/ttyACM0")
                    .baud(Baud._19200)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);

            serial.addListener(new SerialDataEventListener() {
                @Override
                public void dataReceived(SerialDataEvent serialDataEvent) {
                    try {

                        String data = serialDataEvent.getAsciiString();
                        //String hex = serialDataEvent.getHexByteString();

                        if(data.contains("Found ID") || data.startsWith("Did not find") ||
                                data.contains("msg-arduino") ||data.contains("same finger") ||
                                data.contains("matched") || data.contains("did not match") ||
                                data.contains("Stored") || data.contains("Remove"))
                        {
                            reciever = data;
                        }
                       // status.setText(data);
                        System.out.println(data);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });


            serial.open(config);

            //serial.write("start");
        } catch (IOException e) {
            e.printStackTrace();
            status.setText("Sensor not found!!!");
        }

    }

    public void doEntryDatabase(int id,String in_out)
    {
        String forUsername = "select username from profile_data where id = " + id ;
        String username = null;
        String mainQuery;

        Date date =new Date();
        Calendar calendar = Calendar.getInstance();

        try {
            ResultSet result=db.stmt.executeQuery(forUsername);
            if(result.next())
            {
                 username = result.getString("username");


            }
            else
            {
              System.out.println("no user with id found");

            }
            if(username != null)
            {
                String queryDate = calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
                System.out.println(queryDate);
                String queryTime = calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);

                mainQuery = "INSERT INTO `data` (`username`, `"+in_out+"`, `date`)" +
                        "VALUES ('"+username+"', '"+queryTime+"', '"+queryDate+"');";
                db.stmt2.executeUpdate(mainQuery);
                System.out.println("query execution success ");
                status.setText("Entry done for user "+ username);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void register()
    {
        JFrame frame =new JFrame("Register Form");
        frame.setContentPane(new registerForm(db,serial,this).MainPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(300,500);
        frame.pack();
        frame.setVisible(true);
    }
}
