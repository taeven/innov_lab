package handling;
import structures.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://172.16.26.9/innov";

	   //  Database credentials
	   static final String USER = "root";
	   static final String PASS = "pass";
           private static Connection conn = null;
           public static Statement stmt2 = null;
           public static Statement stmt = null;

        
        public Database()
        {

        }

    public static Boolean checkConn()
    {
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Object obj = conn.getContent();
            return true;
        } catch (MalformedURLException ex) {
            System.out.println(ex);
            return false;

        } catch (IOException ex) {
            System.out.println(ex);
            return false;
        }
    }
        public boolean get_connection()
        {
            
            try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver");

			      //STEP 3: Open a connection
			      System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(DB_URL,USER,PASS);

			      //STEP 4: Execute a query
			      System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      stmt2 = conn.createStatement();

			      return true;
			   }catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			      return false;
			   } catch (ClassNotFoundException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public int check_credentials(String username,String password)
        {
            String sql="SELECT type FROM account_details WHERE username = '"+
                    username+"' AND password = '"+password+"'";
            try {
                ResultSet result=stmt.executeQuery(sql);
                if(result.next())
                {
                    int type = result.getInt("type");
                    return type;
                }
                else
                {
                    return -1;
                }
                    
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
            
              return -1; 
        }


        public ArrayList<data> get_data()
        {
            ArrayList<data> list = new ArrayList<data>();
            String query = "select * from data";

            try {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next())
                {
                    data tmp = new data();
                    tmp.setUsername(rs.getString("username"));
                    String second_query = "select * from profile_data where username = '"+ tmp.getUsername()+"'";
                    ResultSet rs2 = stmt2.executeQuery(second_query);
                    if(rs2.next())
                    {
                        tmp.setRoom_no(rs2.getString("room_no"));
                        tmp.setPhone_no(rs2.getString("phone_no"));
                    }

                    tmp.setCheck_in(rs.getString("in_time"));
                    tmp.setRoll(rs.getString("roll"));
                    tmp.setCheck_out(rs.getString("out_time"));

                    tmp.setDate(rs.getString("date"));
                    list.add(tmp);
//                    tmp.setUsername(rs.getString("username"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return list;
        }
       
}
