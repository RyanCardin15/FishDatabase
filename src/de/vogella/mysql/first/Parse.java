package de.vogella.mysql.first;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
    Document doc;
    LocalDate currentdate = LocalDate.now();





    public static String subStringBetween(String text, String after, String before) {
        Pattern pattern = Pattern.compile("(?<=\\s|^)"+after +"\\s(.*?)\\s+"+ before, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return "Nomatches"; //or null or whatever you need

    }


    public static String[] parseinfo(String a, int time, String date, String loc){


        String[] temp = formatTime(loc, time);
        String after = temp[0];
        String before = temp[1];
        String date1 = formatDate(date);
        String lines = subStringBetween(a,date1 + " "+ after,before);
        String[] clean = lines.split("\\s+");
        String[] redemtion = cleanParseData(clean, a, date, time, loc);
        return redemtion;
    }

    private static String formatDate(String date) {
        String tempdate = "";
        if(Integer.parseInt(date) < 10){
            tempdate = "0" + Integer.parseInt(date);
        }else
            tempdate = date;

        return tempdate;
    }

    public static String[] cleanParseData(String[] clean, String text, String date, int time, String loc) throws NullPointerException {
        String[] redemtion = new String[25];

        int count = 1;

        if (clean[0].equals("Calm")) {
            redemtion[0] = "0";
            redemtion[1] = "0";
            count++;
        } else {
            redemtion[0] = clean[0];
        }

        for (int i = 1; i < clean.length; i++) {
            if (isNumeric(clean[i])) {
                redemtion[count] = clean[i];
                count++;
            }
        }
        /*
        NOT PARSING CORRECTLY
         */
        int a = 0;
        boolean humidity = false;
        int i = 5;

        //Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        //Matcher matcher = pattern.matcher(redemtion[5]);
        int timeminus = 1;
        if (redemtion[2] == null) {
            parseinfo(text, time - timeminus, date, loc);
            timeminus++;
        }else {
            if (redemtion[2].contains(".")) {
                while (!humidity) {
                    if (!redemtion[i].contains("%")) {
                        i++;
                    } else {
                        if (i > 5) {
                            redemtion[5] = redemtion[i];
                            redemtion[6] = redemtion[i + 1];
                        }
                        humidity = true;
                    }
                }
            } else {
                redemtion[2] = redemtion[3];
                redemtion[3] = redemtion[4];
                redemtion[4] = redemtion[5];
                redemtion[5] = redemtion[6];
                redemtion[6] = redemtion[7];

                while (!humidity) {
                    if (!redemtion[i].contains("%")) {
                        i++;
                    } else {
                        if (i > 5) {
                            redemtion[5] = redemtion[i];
                            redemtion[6] = redemtion[i + 1];
                        }
                        humidity = true;
                    }
                }
            }


            boolean pressure = false;
            int x = 6;

            if (!redemtion[6].contains(".")) {
                while (!pressure) {
                    if (!redemtion[x].contains(".")) {
                        x++;
                    } else {
                        if (x > 6) {
                            redemtion[6] = redemtion[x];
                        }
                        pressure = true;
                    }
                }
            }
        }


        for(int temp = 0; temp < redemtion.length; temp++){
            System.out.println(redemtion[temp]);
        }

        return redemtion;
    }


    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            if(strNum.contains("%")){
                return true;
            }else
            return false;
        }
        return true;
    }

    public static String[] formatTime(String loc, int time){
        String after = "";
        String before = "";
        String[] temp = new String[2];
        if(loc.equals("Corpus Christi")) {
            if (time == 0) {
                after = "23:56";
                before = "22:56";
            } else if (time == 1) {
                after = "00:56";
                before = "23:56";
            } else if (time < 10) {
                after = "0" + (time - 1) + ":56";
                before = "0" + (time - 2) + ":56";
            } else if (time == 10) {
                after = "09:56";
                before = "08:56";
            } else if (time == 11) {
                after = "10:56";
                before = "09:56";
            } else {
                after = (time - 1) + ":56";
                before = (time - 2) + ":56";
            }
        }else if(loc.equals("Port Aransas")){
            if(time == 0){
                after = "23:55";
                before = "23:35";
            }else if(time < 11) {
                after = "0" + (time - 1) + ":55";
                before = "0" + (time - 1) + ":35";
            }else{
                after = (time - 1) + ":55";
                before = (time - 1) + ":35";
            }
        }
        temp[0] = after;
        temp[1] = before;
        return temp;
    }

    public String[] changeVar(String text, String date, int time, String loc){

        int yesterday = Integer.parseInt(date) - 1;
        String yest = ""+yesterday;
        String[] temp = new String[9];
        String[] updated4hrtimeanddate = subTime(time,4,date,text,loc);
        String[] updated8hrtimeanddate = subTime(time,8,date,text,loc);
        int changein4hrtime = Integer.parseInt(updated4hrtimeanddate[0]);
        String changein4hrdate = updated4hrtimeanddate[1];
        int changein8hrtime = Integer.parseInt(updated8hrtimeanddate[0]);
        String changein8hrdate = updated8hrtimeanddate[1];

        String[] redemption4 = parseinfo(text,changein4hrtime,changein4hrdate,loc);
        String[] redemption8 = parseinfo(text,changein8hrtime,changein8hrdate,loc);
        String[] redemption24 = parseinfo(text, time, yest, loc);
        temp[0] = redemption4[3];
        temp[1] = redemption4[4];
        temp[2] = redemption4[6];
        temp[3] = redemption8[3];
        temp[4] = redemption8[4];
        temp[5] = redemption8[6];
        temp[6] = redemption24[3];
        temp[7] = redemption24[4];
        temp[8] = redemption24[6];

        return temp;
    }

    public String[] netChange(String[] change, String[] curr){
        String[] storage = new String[change.length];
        DecimalFormat df = new DecimalFormat("###.###");
        for(int i = 0; i <= 6; i = i + 3) {
            double temp = (Double.parseDouble(curr[3])) - (Double.parseDouble(change[i]));
            double temp2 = (Double.parseDouble(curr[4])) - (Double.parseDouble(change[i + 1]));
            double temp3 = (Double.parseDouble(curr[6])) - (Double.parseDouble(change[i + 2]));
            if (temp < 0) {
                storage[i] = "-" + (df.format(temp * -1)) + "";
            } else
                storage[i] = "+" + temp + "";

            if (temp2 < 0) {
                storage[i+1] = "-" + (df.format(temp2 * -1)) + "";
            } else
                storage[i+1] = "+" + temp2 + "";

            if (temp3 < 0) {
                storage[i+2] = "-" + (df.format(temp3 * -1)) + "";
            } else
                storage[i+2] = "+" + temp3 + "";
        }


        return storage;
    }

    public String[] subTime(int time, int subBy, String date, String text, String loc){
        String temp[] = new String[2];
        if(time - subBy >= 0){
            time = time - subBy;
            temp[0] = time+"";
            temp[1] = date;
            return temp;
        }else if(time - subBy < 0){
            time = 24 + (time - subBy);
            int yesterday = Integer.parseInt(date) - 1;
            String yest = ""+yesterday;
            temp[0] = time+"";
            temp[1] = yest;
            return temp;
        }
        return temp;
    }

    public String parseMoon(){
        int date = currentdate.getDayOfMonth();
        try {
            doc = Jsoup.connect("https://lunaf.com/lunar-calendar/").get();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String parseable = doc.text();
        String after = "Today's Moon phase";
        String before = "in";
        String lines = subStringBetween(parseable,after,before);
        String clean[] = lines.split("\\s+");

        String moonCycle = clean[13] + clean[14];
        return moonCycle;
    }
}
