package com.adi.ho.jackie.bubblestocks.StockPortfolio;

import android.os.Parcel;
import android.os.Parcelable;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Created by JHADI on 3/24/16.
 */
public class HistoricalStockQuoteWrapper extends HistoricalQuote implements Parcelable{

    private String lowPrice;
    private String highPrice;
    private String openPrice;
    private String closePrice;
    private String volume;

    public HistoricalStockQuoteWrapper(HistoricalQuote historicalQuote){
        lowPrice = historicalQuote.getLow().toString();
        highPrice = historicalQuote.getHigh().toString();
        openPrice = historicalQuote.getOpen().toString();
        closePrice = historicalQuote.getClose().toString();
        volume = String.valueOf(historicalQuote.getVolume());
    }

    protected HistoricalStockQuoteWrapper(Parcel in) {
        lowPrice = in.readString();
        highPrice = in.readString();
        openPrice = in.readString();
        closePrice = in.readString();
        volume = in.readString();
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
    }
}
