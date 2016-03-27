package com.adi.ho.jackie.bubblestocks.StockPortfolio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by JHADI on 3/26/16.
 */
public class IntradayStockData {
    private Date timestamp;
    private String closePrice;
    private String highPrice;
    private String lowPrice;
    private String openPrice;
    private String volume;

    public IntradayStockData(JSONObject intradayJsonObject) throws JSONException {

        this.timestamp = new Date( intradayJsonObject.getLong("timestamp")*1000);
        this.closePrice = intradayJsonObject.getString("close");
        this.highPrice = intradayJsonObject.getString("high");
        this.lowPrice = intradayJsonObject.getString("low");
        this.openPrice = intradayJsonObject.getString("open");
        this.volume = intradayJsonObject.getString("volume");

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
}
