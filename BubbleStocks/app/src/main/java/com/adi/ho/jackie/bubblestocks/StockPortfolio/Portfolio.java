package com.adi.ho.jackie.bubblestocks.stockportfolio;

import com.adi.ho.jackie.bubblestocks.customviews.PortfolioBubble;

import java.util.ArrayList;

/**
 * Created by JHADI on 3/21/16.
 */
public class Portfolio {

    //Singleton portfolio
    private ArrayList<PortfolioStock> myStockPortfolio;
    private ArrayList<PortfolioBubble> myStockBubblePortfolio;
    private static Portfolio instance;


    private Portfolio() {
        myStockPortfolio = new ArrayList<>();
        myStockBubblePortfolio = new ArrayList<>();
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

    public void addBubbleToList(PortfolioBubble bubble){
        myStockBubblePortfolio.add(bubble);
    }
    public ArrayList<PortfolioStock> getMyStockPortfolio(){
        return myStockPortfolio;
    }

    public int getPortfolioSize(){
        return myStockPortfolio.size();
    }

    public ArrayList<PortfolioBubble> getMyStockBubblePortfolio() {
        return myStockBubblePortfolio;
    }

    public int getBubbleSize(){ return myStockBubblePortfolio.size();}
}
