package com.adi.ho.jackie.bubblestocks.StockPortfolio;

/**
 * Created by JHADI on 4/3/16.
 */
public class PortfolioStock {
    private String mSymbol;
    private String mPrice;
    private String mOpenPrice;

    public PortfolioStock(String symbol, String price, String mOpenPrice){
        this.mSymbol = symbol;
        this.mPrice = price;
        this.mOpenPrice = mOpenPrice;
    }

    public String getmSymbol() {
        return mSymbol;
    }

    public void setmSymbol(String mSymbol) {
        this.mSymbol = mSymbol;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getmOpenPrice() {
        return mOpenPrice;
    }

    public void setmOpenPrice(String mOpenPrice) {
        this.mOpenPrice = mOpenPrice;
    }
}
