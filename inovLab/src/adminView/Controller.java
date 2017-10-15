package adminView;

import handling.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.util.Callback;
import structures.data;

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * Created by vaibhav on 12/2/17.
 */
public class Controller implements Initializable {

    private Database db;
    private int offset ,offsetSearch;
    @FXML
    private ListView<ListView> listViewSearch;
    @FXML
    private TextField username;
    @FXML private TextField userField;
    @FXML private TextField passwordField;
    @FXML private Button enroll;
    @FXML private Label status;
    @FXML
    private TabPane tabContainer;
    @FXML
    private Button search;

    private String userName;

    @FXML
    private ListView<ListView> listView;
    private ArrayList<data> from_db;

    private ObservableList<ListView> list = FXCollections.observableArrayList();
    private ObservableList<ListView> listSearch = FXCollections.observableArrayList();


    @FXML
    public void onRefresh()
    {
        int tab=tabContainer.getSelectionModel().getSelectedIndex();
        if(tab == 0) {
            offset=0;
            readyList(3);
        }
        else
        {
            offsetSearch =0;
            readySearchList(3);
        }

    }

    @FXML

    public void onNext()
    {


        int tab=tabContainer.getSelectionModel().getSelectedIndex();
        if(tab == 0) {
            offset += 15;
            readyList(0);
        }
        else
        {
            offsetSearch +=15;
            readySearchList(0);
        }
    }

    @FXML
    public void onPrev()
    {
        int tab=tabContainer.getSelectionModel().getSelectedIndex();

        if(tab == 0) {
            if(offset<=0)
            {
                offset=0;
                readyList(1);
                return;
            }
            offset-=15;
            readyList(1);
        }
       else
        {
            if(offsetSearch<=0)
            {
                offsetSearch=0;
                readySearchList(1);
                return;
            }
            offsetSearch-=15;
            readySearchList(1);
        }

    }

    public boolean readyList(int check)
    {

        from_db = db.get_data(offset);

        boolean first = true;
        if(!from_db.isEmpty()) {
            list.clear();
            while (!from_db.isEmpty()) {

                ListView<String> single_row = new ListView<String>();
                single_row.setOrientation(Orientation.HORIZONTAL);

                single_row.setMaxHeight(30);
                single_row.setMouseTransparent(true);
                single_row.setFocusTraversable(true);
                single_row.setPrefHeight(30);
                ObservableList<String> single = FXCollections.observableArrayList();
                single_row.setItems(single);
                single_row.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        ListCell lc = new ListCell<String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(null);
                                if (!empty) {
                                    setText(item);
                                }
                            }
                        };
                        lc.setPrefWidth(80);
                        return lc;
                    }

                });
                //setting headers
                if (first) {
                    single.add("USERNAME");
                    single.add("ROLL NO.");
                    single.add("IN TIME");
                    single.add("OUT TIME");
                    single.add("DATE");
                    single.add("ROOM NO.");
                    single.add("PHONE NO.");

                    first = false;

                }
                //retrieving data
                else {
                    data tmp = from_db.remove(0);
                    single.add(tmp.getUsername());
                    single.add(tmp.getRoll());
                    single.add(tmp.getCheck_in());
                    single.add(tmp.getCheck_out());
                    single.add(tmp.getDate());
                    single.add(tmp.getRoom_no());
                    single.add(tmp.getPhone_no());


                }
                list.add(single_row);
            }
            return true;
        }
        else
        {
            if(check==0)
            {
                offset-=15;
            }

            return false;
        }

    }


    public boolean readySearchList(int check)
    {

        from_db = db.getSearchData(offsetSearch,userName);

        boolean first = true;
        if(!from_db.isEmpty()) {
            listSearch.clear();
            while (!from_db.isEmpty()) {

                ListView<String> single_row = new ListView<String>();
                single_row.setOrientation(Orientation.HORIZONTAL);

                single_row.setMaxHeight(30);
                single_row.setMouseTransparent(true);
                single_row.setFocusTraversable(true);
                single_row.setPrefHeight(30);
                ObservableList<String> single = FXCollections.observableArrayList();
                single_row.setItems(single);
                single_row.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        ListCell lc = new ListCell<String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(null);
                                if (!empty) {
                                    setText(item);
                                }
                            }
                        };
                        lc.setPrefWidth(80);
                        return lc;
                    }

                });
                //setting headers
                if (first) {
                    single.add("USERNAME");
                    single.add("ROLL NO.");
                    single.add("IN TIME");
                    single.add("OUT TIME");
                    single.add("DATE");
                    single.add("ROOM NO.");
                    single.add("PHONE NO.");

                    first = false;

                }
                //retrieving data
                else {
                    data tmp = from_db.remove(0);
                    single.add(tmp.getUsername());
                    single.add(tmp.getRoll());
                    single.add(tmp.getCheck_in());
                    single.add(tmp.getCheck_out());
                    single.add(tmp.getDate());
                    single.add(tmp.getRoom_no());
                    single.add(tmp.getPhone_no());


                }
                listSearch.add(single_row);
            }
            return true;
        }
        else
        {
            if(check==0)
            {
                offsetSearch-=15;
            }

            return false;
        }

    }


    @FXML
    public void enrollUser()
    {
        String username = userField.getText();
        String password =passwordField.getText();
        if(username.isEmpty() || password.isEmpty())
        {

            status.setText("fields should not be empty!!");
        }
        else
        {

            if(db.enroll(username,password))
            {
                status.setText("success");
            }
            else
            {
                status.setText("failed!! conn. error!");

            }
            userField.clear();
            passwordField.clear();

        }
    }
    @FXML
    public void onSearch()
    {


        offsetSearch=0;
        userName = username.getText();
        if(!userName.isEmpty())
        {
            listSearch.clear();
          readySearchList(3);

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.setItems(list);
        listViewSearch.setItems(listSearch);
        db = new Database();
        offset = 0;
        offsetSearch =0;

        readyList(3);
    }
}
