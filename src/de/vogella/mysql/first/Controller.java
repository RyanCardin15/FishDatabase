package de.vogella.mysql.first;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Button entry, submit, back, dataView, showData, queryBack, add;
    @FXML
    private
    ComboBox<String> time = new ComboBox<>();
    @FXML
    DatePicker date = new DatePicker();
    @FXML
    ChoiceBox<String> fishAmount = new ChoiceBox<>();
    @FXML
    ChoiceBox<String> fishName = new ChoiceBox<>();
    @FXML
    ChoiceBox<String> fishMedium = new ChoiceBox<String>();
    @FXML
    ChoiceBox<String> fishSize = new ChoiceBox<>();
    @FXML
    ChoiceBox<String> fishLocation = new ChoiceBox<String>();
    @FXML
    ChoiceBox<String> fishClarity = new ChoiceBox<>();
    @FXML
    TextArea queryList = new TextArea();
    @FXML
    ComboBox<String> view = new ComboBox<>();

    int listnum = 0;
    int[] arr = new int[24];

    Parse one = new Parse();
    private Statement statement = null;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet = null;
    private Connection con = null;

    static Document doc;
    private String database = "fishdbtest";







    public void change_page(Button b, String page) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(page));
        Stage stage = (Stage)b.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void entry_click(ActionEvent actionEvent) throws IOException {
        change_page(entry,"MakeEntry.fxml");
    }

    public void recordEntry(ActionEvent actionEvent) throws SQLException, IOException {
        String Parsable = checkLocation(fishLocation.getValue().toString());

        String temp = time.getValue().toString();
        String[] temp2 = temp.split("\\s+");
        LocalDate ld = date.getValue();
        String gotDate = ""+ld.getDayOfMonth();
        int realTime = checkTime(temp2);
        String cleanish = one.subStringBetween(Parsable, "Min.", "D a t e");
        String[] info = one.parseinfo(cleanish, realTime, gotDate, fishLocation.getValue().toString());
        String[] tempChange = one.changeVar(cleanish, gotDate, realTime, fishLocation.getValue().toString());
        String[] change = one.netChange(tempChange, info);
        String moonCycle = one.parseMoon();





        //displaying the info
        /*
        info[0] = wind direction
        info[1] = wind speed
        info[2] = visibility
        info[3] = air temp
        info[4] = water temp
        info[5] = humidity
        info[6] = water pressure
        change[0] = 4 hr change in air temp
        change[1] = 4 hr change in water temp
        change[2] = 4 hr change in pressure
        change[3] = 8 hr change in air temp
        change[4] = 8 hr change in water temp
        change[5] = 8 hr change in pressure
        change[6] = 24 hr change in air temp
        change[7] = 24 hr change in water temp
        change[8] = 24 hr change in pressure
         */

        preparedStatement = con.prepareStatement("insert into "+database+".fish (fish_id, fish_name, fish_size, fish_amount, fish_location, wind_speed, water_pressure, month, moon_cycle, medium, wind_direction, air_temperature, time, visibility, water_temperature, humidity, water_clarity, 4hr_air_change, 4hr_water_change, 4hr_pressure_change, 8hr_air_change, 8hr_water_change, 8hr_pressure_change, 24hr_air_change, 24hr_water_change, 24hr_pressure_change) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");//database
        preparedStatement.setString(1,getId(fishName.getValue().toString()));
        preparedStatement.setString(2,fishName.getValue().toString());
        preparedStatement.setString(3,fishSize.getValue().toString());
        preparedStatement.setString(4,fishAmount.getValue().toString());
        preparedStatement.setString(5,fishLocation.getValue().toString());
        preparedStatement.setString(6,info[1]);
        preparedStatement.setString(7,info[6]);
        preparedStatement.setString(8,ld.getMonth().toString());
        preparedStatement.setString(9,moonCycle);
        preparedStatement.setString(10,fishMedium.getValue().toString());
        preparedStatement.setString(11,info[0]);
        preparedStatement.setString(12,info[3]);
        preparedStatement.setString(13,time.getValue().toString());
        preparedStatement.setString(14,info[2]);
        preparedStatement.setString(15,info[4]);
        preparedStatement.setString(16,info[5]);
        preparedStatement.setString(17,fishClarity.getValue().toString());
        preparedStatement.setString(18,change[0]);
        preparedStatement.setString(19,change[1]);
        preparedStatement.setString(20,change[2]);
        preparedStatement.setString(21,change[3]);
        preparedStatement.setString(22,change[4]);
        preparedStatement.setString(23,change[5]);
        preparedStatement.setString(24,change[6]);
        preparedStatement.setString(25,change[7]);
        preparedStatement.setString(26,change[8]);
        preparedStatement.executeUpdate();

        change_page(submit,"Success.fxml");
    }

    private String checkLocation(String loc) {

        if(loc.equals("Corpus Christi")){
                try {
                    doc = Jsoup.connect("https://w1.weather.gov/obhistory/KNGP.html").get();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

        }else if(loc.equals("Port Aransas")){
                try {
                    doc = Jsoup.connect("https://w1.weather.gov/data/obhistory/KRAS.html").get();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        }

        String Parsable = doc.text();
        return Parsable;
    }

    private int checkTime(String[] temp2) {
        if(temp2[0].equals("12") && temp2[1].equals("pm")){
            return 12;
        }else if(temp2[0].equals("12") && temp2[1].equals("am")){
            return 0;
        }else if(temp2[1].equals("am")){
            return Integer.parseInt(temp2[0]);
        }else if(temp2[1].equals("pm")){
            return Integer.parseInt(temp2[0]) + 12;
        }else return -1;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.time.getItems().addAll("12 am","1 am","2 am","3 am","4 am","5 am","6 am","7 am","8 am","9 am","10 am","11 am","12 pm","1 pm","2 pm","3 pm","4 pm","5 pm","6 pm","7 pm","8 pm","9 pm","10 pm","11 pm");
        this.fishAmount.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20");
        this.fishMedium.getItems().addAll("Artificial", "Live Bait", "Cut Bait");
        this.fishSize.getItems().addAll("Dink", "Keeper", "Decent", "Big Boi");
        this.fishName.getItems().addAll("Speckled Trout", "Redfish", "Flounder", "Sheepshead", "Black Drum");
        this.fishClarity.getItems().addAll("Crystal Clear", "Clear", "Ehh", "Cloudy", "Bruh I'm Blind");
        this.fishLocation.getItems().addAll("Corpus Christi", "Port Aransas");
        this.view.getItems().addAll("Fish Name", "Fish Size", "Fish Amount", "Fish_Location", "Wind Speed", "Water Pressure", "Month", "Moon Cycle", "Medium", "Wind Direction", "Air Temperature", "Visibility", "Water Temperature", "Humidity", "Water Clarity", "4hr Change in Air Temp", "4hr Change in Water Temp", "4hr Change in Pressure", "8hr Change in Air Temp", "8hr Change in Water Temp", "8hr Change in Pressure", "24hr Change in Air Temp", "8hr Change in Water Temp", "24hr Change in Pressure", "View All");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/"+database+"?" + "user=root&password=Rhino1515");//database
            con.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String getId(String fishName){
        String id = "";
        if(fishName.equals("Speckled Trout")){
            id = "1";
        }else if(fishName.equals("Redfish")){
            id = "2";
        }else if(fishName.equals("Flounder")){
            id = "3";
        }else if(fishName.equals("Black Drum")){
            id = "4";
        }else if(fishName.equals("Sheepshead")){
            id = "5";
        }else
            id = "6";


        return id;
    }

    public void back_click(ActionEvent actionEvent) throws IOException {
        change_page(back,"MainMenu.fxml");
    }



    public void queryData(ActionEvent actionEvent) throws SQLException, IOException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select * from fish");
        int count = 1;
        int check = 0;

        if (listnum == 0) {
            queryList.appendText("Please Select Some Values\n");
        } else {
            while (rs.next()) {

                String fishname = rs.getString("Fish_Name");
                String fishsize = rs.getString("Fish_Size");
                String fishamount = rs.getString("Fish_Amount");
                String fishlocation = rs.getString("Fish_Location");
                String windspeed = rs.getString("Wind_Speed");
                String waterpressure = rs.getString("Water_pressure");
                String month = rs.getString("Month");
                String mooncycle = rs.getString("Moon_Cycle");
                String medium = rs.getString("Medium");
                String winddirection = rs.getString("Wind_Direction");
                String air_temperature = rs.getString("Air_Temperature");
                String visibility = rs.getString("Visibility");
                String watertemperature = rs.getString("Water_Temperature");
                String humidity = rs.getString("Humidity");
                String waterclarity = rs.getString("Water_Clarity");
                String waterchange4h = rs.getString("4hr_Water_Change");
                String airchange4h = rs.getString("4hr_Air_Change");
                String pressurechange4h = rs.getString("4hr_Pressure_Change");
                String waterchange8h = rs.getString("8hr_Water_Change");
                String airchange8h = rs.getString("8hr_Air_Change");
                String pressurechange8h = rs.getString("8hr_Pressure_Change");
                String waterchange24h = rs.getString("24hr_Water_Change");
                String airchange24h = rs.getString("24hr_Air_Change");
                String pressurechange24h = rs.getString("24hr_Pressure_Change");
                    queryList.appendText("Entry Number " + count + ": \n");
                    queryList.appendText("Fish: " + fishname + "\n");
                    queryList.appendText("Size: " + fishsize + "\n");
                    queryList.appendText("Amount: " + fishamount + "\n");
                    queryList.appendText("Location: " + fishlocation + "\n");
                    queryList.appendText("Medium: " + medium + "\n");
                    queryList.appendText("Month: " + month + "\n");
                    queryList.appendText("Moon Cycle: " + mooncycle + "\n");
                    queryList.appendText("Wind Speed: " + windspeed + "\n");
                    queryList.appendText("Wind Direction: " + winddirection + "\n");
                    queryList.appendText("Size: " + fishsize + "\n");
                    queryList.appendText("Water Temp: " + watertemperature + "\n");
                    queryList.appendText("Pressure: " + waterpressure + "\n");
                    queryList.appendText("Air Temp: " + air_temperature + "\n");
                    queryList.appendText("Visibility: " + visibility + "\n");
                    queryList.appendText("Water Clarity: " + waterclarity + "\n");
                    queryList.appendText("Humidity: " + humidity + "\n");
                    queryList.appendText("---------------4hr Change---------------\n");
                    queryList.appendText("Air Temp Change: " + airchange4h + "\n");
                    queryList.appendText("Water Temp Change: " + waterchange4h + "\n");
                    queryList.appendText("Pressure Change: " + pressurechange4h + "\n");
                    queryList.appendText("---------------8hr Change---------------\n");
                    queryList.appendText("Air Temp Change: " + airchange8h + "\n");
                    queryList.appendText("Water Temp Change: " + waterchange8h + "\n");
                    queryList.appendText("Pressure Change: " + pressurechange8h + "\n");
                    queryList.appendText("---------------24hr Change---------------\n");
                    queryList.appendText("Air Temp Change: " + airchange24h + "\n");
                    queryList.appendText("Water Temp Change: " + waterchange24h + "\n");
                    queryList.appendText("Pressure Change: " + pressurechange24h + "\n");
                    queryList.appendText("----------------------------------------\n");
                    queryList.appendText("----------------------------------------\n");
                    queryList.appendText("----------------------------------------\n");
                    queryList.appendText("----------------------------------------\n");
                        count++;
                        check++;
                    }

                }
            }


    public void viewData(ActionEvent actionEvent) throws IOException {
        change_page(dataView, "QueryData.fxml");
    }

    public void queryBack_click(ActionEvent actionEvent) throws IOException {
        change_page(queryBack, "MainMenu.fxml");
    }


    public void add_click(ActionEvent actionEvent) {
        String added = view.getValue().toString();
        queryList.appendText(added+ "\n");

        switch (added){
            case "Fish Name":
                arr[listnum] =  1;
                break;
            case "Fish Size":
                arr[listnum] = 2;
                break;
            case "Fish Amount":
                arr[listnum] = 3;
                break;
            case "Fish Location":
                arr[listnum] = 4;
                break;
            case "Wind Speed":
                arr[listnum] = 5;
                break;
            case "Water Pressure":
                arr[listnum] = 6;
                break;
            case "Month":
                arr[listnum] = 7;
            case "Moon Cycle":
                arr[listnum] = 8;
                break;
            case "Medium":
                arr[listnum] = 9;
                break;
            case "Wind Direction":
                arr[listnum] = 10;
                break;
            case "Air Temperature":
                arr[listnum] = 11;
                break;
            case "Visibility":
                arr[listnum] = 12;
                break;
            case "Water Temperature":
                arr[listnum] = 13;
                break;
            case "Humidity":
                arr[listnum] = 14;
                break;
            case "Water Clarity":
                arr[listnum] = 15;
                break;
            case "4hr Change in Air Temp":
                arr[listnum] = 16;
                break;
            case "4hr Change in Water Temp":
                arr[listnum] = 17;
                break;
            case "4hr Change in Pressure":
                arr[listnum] = 18;
                break;
            case "8hr Change in Air Temp":
                arr[listnum] = 19;
                break;
            case "8hr Change in Water Temp":
                arr[listnum] = 20;
                break;
            case "8hr Change in Pressure":
                arr[listnum] = 21;
                break;
            case "24hr Change in Air Temp":
                arr[listnum] = 22;
                break;
            case "24hr Change in Water Temp":
                arr[listnum] = 23;
                break;
            case "24hr Change in Pressure":
                arr[listnum] = 24;
                break;
            case "View All":
                arr[0] = 50;
                break;
        }
        listnum++;
    }
}


