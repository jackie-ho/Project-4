package com.adi.ho.jackie.bubblestocks.stockportfolio;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by JHADI on 3/26/16.
 */
public class IntradayStockData implements Comparable {
    private String timestamp;
    private String closePrice;
    private String highPrice;
    private String lowPrice;
    private String openPrice;
    private String volume;
    private long tradeTimeStamp;

    public IntradayStockData(JSONObject intradayJsonObject) throws JSONException {

        if (intradayJsonObject.optLong("timestamp", -1) == -1) {
            Date date = new Date(intradayJsonObject.getLong("Timestamp") * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
            this.timestamp = String.valueOf( sdf.format(date));
            this.tradeTimeStamp = intradayJsonObject.getLong("Timestamp") * 1000;
        } else {
            Date date = new Date(intradayJsonObject.getLong("timestamp") * 1000);
            this.timestamp = String.valueOf( date.getTime());
            this.tradeTimeStamp = intradayJsonObject.getLong("timestamp") * 1000;
        }
        this.closePrice = intradayJsonObject.getString("close");
        this.highPrice = intradayJsonObject.getString("high");
        this.lowPrice = intradayJsonObject.getString("low");
        this.openPrice = intradayJsonObject.getString("open");
        this.volume = intradayJsonObject.getString("volume");

    }

    public static String getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat newSDF = new SimpleDateFormat("h:mm");
        return newSDF.format(cal.getTime());
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTradeTimeStamp(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date date = new Date(tradeTimeStamp);
        cal.setTime(date);
        SimpleDateFormat tradeSdf = new SimpleDateFormat("MM-dd HH:mm z");
        return "Timestamp: "+ tradeSdf.format(cal.getTime());

    }


    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    @Override
    public int compareTo(Object other) {
       IntradayStockData otherStock = (IntradayStockData)other;
        if (Float.parseFloat(getClosePrice()) < Float.parseFloat(otherStock.getClosePrice())){
            return -1;
        } else if (Float.parseFloat(getClosePrice()) == Float.parseFloat(otherStock.getClosePrice())){
            return 0;
        } else {
            return 1;
        }
    }
}
