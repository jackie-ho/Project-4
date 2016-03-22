package com.adi.ho.jackie.bubblestocks.StockPortfolio;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * Created by JHADI on 3/22/16.
 */
public class DBStock extends RealmObject implements Parcelable {
    private String symbol;
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

    public DBStock(){

    }

    protected DBStock(Parcel in) {
        symbol = in.readString();
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

    public void setAvgVol(String avgVol) {
        this.avgVol = avgVol;
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

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }


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
    }
}
