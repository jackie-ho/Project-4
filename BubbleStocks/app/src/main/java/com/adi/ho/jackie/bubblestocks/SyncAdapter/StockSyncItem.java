package com.adi.ho.jackie.bubblestocks.SyncAdapter;

/**
 * Created by JHADI on 3/30/16.
 */
public class StockSyncItem {
    private int id;
    private String symbol;


    public StockSyncItem(int id, String symbol){
        this.id = id;
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
