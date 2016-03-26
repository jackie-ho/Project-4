package com.adi.ho.jackie.bubblestocks.StockPortfolio;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JHADI on 3/25/16.
 */
public class MarketData implements Parcelable {
    private String index;
    private String price;
    private String month;



    public MarketData(){}

    protected MarketData(Parcel in) {
        index = in.readString();
        price = in.readString();
        month = in.readString();
    }

    public static final Creator<MarketData> CREATOR = new Creator<MarketData>() {
        @Override
        public MarketData createFromParcel(Parcel in) {
            return new MarketData(in);
        }

        @Override
        public MarketData[] newArray(int size) {
            return new MarketData[size];
        }
    };

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(index);
        dest.writeString(price);
        dest.writeString(month);
    }
}
