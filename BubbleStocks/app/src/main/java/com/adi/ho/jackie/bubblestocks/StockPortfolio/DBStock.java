package com.adi.ho.jackie.bubblestocks.StockPortfolio;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by JHADI on 3/22/16.
 */
public class DBStock extends RealmObject implements Parcelable {
    private String symbol;
    private String currentTime;
    private String percentChange;
    private String todaysVolume;
    private String change;
    private String oneYearPriceEstimate;
    private String day;
    private String dayHigh;
    private String dayLow;
    private String dayOpen;
    private String dayClose;
    private String yearHigh;
    private String yearLow;
    private String eps;
    private String avgVol;
    private String diviYield;
    private String pe;
    private String peg;
    private String marketCap;
    private String revenue;
    private String profit;
    private String avgVolBarEntries;


    public DBStock() {

    }


    protected DBStock(Parcel in) {
        symbol = in.readString();
        currentTime = in.readString();
        percentChange = in.readString();
        todaysVolume = in.readString();
        change = in.readString();
        day = in.readString();
        dayHigh = in.readString();
        dayLow = in.readString();
        dayOpen = in.readString();
        dayClose = in.readString();
        yearHigh = in.readString();
        yearLow = in.readString();
        eps = in.readString();
        avgVol = in.readString();
        diviYield = in.readString();
        pe = in.readString();
        peg = in.readString();
        marketCap = in.readString();
        revenue = in.readString();
        profit = in.readString();
        oneYearPriceEstimate = in.readString();
    }

    public static final Creator<DBStock> CREATOR = new Creator<DBStock>() {
        @Override
        public DBStock createFromParcel(Parcel in) {
            return new DBStock(in);
        }

        @Override
        public DBStock[] newArray(int size) {
            return new DBStock[size];
        }
    };

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(String dayHigh) {
        this.dayHigh = dayHigh;
    }

    public String getDayLow() {
        return dayLow;
    }

    public void setDayLow(String dayLow) {
        this.dayLow = dayLow;
    }

    public String getDayOpen() {
        return dayOpen;
    }

    public void setDayOpen(String dayOpen) {
        this.dayOpen = dayOpen;
    }

    public String getDayClose() {
        return dayClose;
    }

    public void setDayClose(String dayClose) {
        this.dayClose = dayClose;
    }

    public String getYearHigh() {
        return yearHigh;
    }

    public void setYearHigh(String yearHigh) {
        this.yearHigh = yearHigh;
    }

    public String getYearLow() {
        return yearLow;
    }

    public void setYearLow(String yearLow) {
        this.yearLow = yearLow;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getAvgVol() {
        return avgVol;
    }

    public void setAvgVolBarEntries(String avgVol){
        avgVolBarEntries = avgVol;
    }

    public String getAvgVolBarEntries(){
        return avgVolBarEntries;
    }

    public void setAvgVol(double avgVol) {
        if (avgVol >=1000000 && avgVol < 1000000000) {
            this.avgVol = String.format("%.2fM", avgVol / 1000000);
        } else if (avgVol >= 1000000000){
            this.avgVol = String.format("%.2fB", avgVol/ 1000000000);
        } else {
            this.avgVol = String.valueOf(avgVol);
        }
    }

    public String getDiviYield() {
        return diviYield;
    }

    public void setDiviYield(String diviYield) {
        this.diviYield = diviYield;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }

    public String getPeg() {
        return peg;
    }

    public void setPeg(String peg) {
        this.peg = peg;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        if (marketCap >=1000000 && marketCap < 1000000000) {
            this.marketCap = String.format("%.2fM", marketCap / 1000000);
        } else if (marketCap >= 1000000000){
            this.marketCap = String.format("%.2fB", marketCap/ 1000000000);
        } else {
            this.marketCap = String.valueOf(marketCap);
        }
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        if (revenue >=1000000 && revenue < 1000000000) {
            this.revenue = String.format("%.2fM", revenue / 1000000);
        } else if (revenue >= 1000000000){
            this.revenue = String.format("%.2fB", revenue/ 1000000000);
        } else {
            this.revenue = String.valueOf(revenue);
        }
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getLastTradeTime() {
        return currentTime;
    }

    public void setLastTradeTime(String lastTradeTime) {
        this.currentTime = lastTradeTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    public String getTodaysVolume() {
        return todaysVolume;
    }

    public void setTodaysVolume(double todaysVolume) {
        if (todaysVolume >=1000000 && todaysVolume < 1000000000) {
            this.todaysVolume = String.format("%.2fM", todaysVolume / 1000000);
        } else if (todaysVolume >= 1000000000){
            this.todaysVolume = String.format("%.2fB", todaysVolume/ 1000000000);
        } else {
            this.todaysVolume = String.valueOf(todaysVolume);
        }
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getOneYearPriceEstimate(){return oneYearPriceEstimate;}

    public void setOneYearPriceEstimate(String oneYearPriceEstimate){this.oneYearPriceEstimate = oneYearPriceEstimate;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(symbol);
        dest.writeString(day);
        dest.writeString(dayHigh);
        dest.writeString(dayLow);
        dest.writeString(dayOpen);
        dest.writeString(dayClose);
        dest.writeString(yearHigh);
        dest.writeString(yearLow);
        dest.writeString(eps);
        dest.writeString(avgVol);
        dest.writeString(diviYield);
        dest.writeString(pe);
        dest.writeString(peg);
        dest.writeString(marketCap);
        dest.writeString(revenue);
        dest.writeString(profit);
        dest.writeString(currentTime);
        dest.writeString(percentChange);
        dest.writeString(todaysVolume);
        dest.writeString(change);
        dest.writeString(oneYearPriceEstimate);

    }
}
