package com.adi.ho.jackie.bubblestocks.stockportfolio;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Created by JHADI on 3/24/16.
 */
public class HistoricalStockQuoteWrapper extends HistoricalQuote implements Parcelable, Comparable{

    private String lowPrice;
    private String highPrice;
    private String openPrice;
    private String closePrice;
    private String volume;
    private String date;

    public HistoricalStockQuoteWrapper(HistoricalQuote historicalQuote){
        lowPrice = historicalQuote.getLow().toString();
        highPrice = historicalQuote.getHigh().toString();
        openPrice = historicalQuote.getOpen().toString();
        closePrice = historicalQuote.getClose().toString();
        volume = String.valueOf(historicalQuote.getVolume());
        Date dayOfQuote = historicalQuote.getDate().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        date = sdf.format(dayOfQuote);

    }

    protected HistoricalStockQuoteWrapper(Parcel in) {
        lowPrice = in.readString();
        highPrice = in.readString();
        openPrice = in.readString();
        closePrice = in.readString();
        volume = in.readString();
        date = in.readString();
    }

    public static final Creator<HistoricalStockQuoteWrapper> CREATOR = new Creator<HistoricalStockQuoteWrapper>() {
        @Override
        public HistoricalStockQuoteWrapper createFromParcel(Parcel in) {
            return new HistoricalStockQuoteWrapper(in);
        }

        @Override
        public HistoricalStockQuoteWrapper[] newArray(int size) {
            return new HistoricalStockQuoteWrapper[size];
        }
    };

    public String getDayOfQuote(){
        return date;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public String getClosePrice() {
        return closePrice;
    }
    public float getDayVolume(){
        //commented out until i figure how to put a string label onto the yaxis

//        double todaysVolume = Double.parseDouble(volume);
//        if (todaysVolume >=1000000 && todaysVolume < 1000000000) {
//            volume = String.format("%.1fM", todaysVolume / 1000000);
//        } else if (todaysVolume >= 1000000000){
//            volume = String.format("%.1fB", todaysVolume/ 1000000000);
//        }
//        return Float.parseFloat(volume);
        return Float.parseFloat(volume);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lowPrice);
        dest.writeString(highPrice);
        dest.writeString(openPrice);
        dest.writeString(closePrice);
        dest.writeString(volume);
        dest.writeString(date);
    }

    @Override
    public int compareTo(Object another) {
        HistoricalStockQuoteWrapper otherStock = (HistoricalStockQuoteWrapper)another;
        if (Float.parseFloat(closePrice) < Float.parseFloat(otherStock.getClosePrice())){
            return -1;
        } else if (Float.parseFloat(closePrice) == Float.parseFloat(otherStock.getClosePrice())){
            return 0;
        } else {
            return 1;
        }
    }
}