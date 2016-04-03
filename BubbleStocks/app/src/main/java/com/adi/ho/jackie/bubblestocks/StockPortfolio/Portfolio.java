package com.adi.ho.jackie.bubblestocks.StockPortfolio;

import java.util.ArrayList;
import java.util.LinkedList;

import yahoofinance.Stock;

/**
 * Created by JHADI on 3/21/16.
 */
public class Portfolio {

    //Singleton portfolio
    private ArrayList<PortfolioStock> myStockPortfolio;
    private static Portfolio instance;


    private Portfolio() {
        myStockPortfolio = new ArrayList<>();
    }

    public static Portfolio getInstance() {
        if (instance == null) {
            instance = new Portfolio();
        }
        return instance;
    }

    public void initialAddToPortfolio(PortfolioStock stock){
        myStockPortfolio.add(stock);
    }

    public ArrayList<PortfolioStock> getMyStockPortfolio(){
        return myStockPortfolio;
    }

    public int getPortfolioSize(){
        return myStockPortfolio.size();
    }

}
