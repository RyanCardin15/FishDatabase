package de.vogella.mysql.first;

public class DataSet {
    String entryNo, fishName, fishAmount, pressurechange4hr, pressurechange8hr, pressurechange24hr;

    DataSet(String entryNo, String fishName, String fishAmount, String pressurechange4hr, String pressurechange8hr, String pressurechange24hr){
        this.entryNo = entryNo;
        this. fishName = fishName;
        this.fishAmount = fishAmount;
        this.pressurechange4hr = pressurechange4hr;
        this.pressurechange8hr = pressurechange8hr;
        this.pressurechange24hr = pressurechange24hr;
    }
}
