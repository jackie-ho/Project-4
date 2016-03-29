package com.adi.ho.jackie.bubblestocks.SyncAdapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.adi.ho.jackie.bubblestocks.Database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.Database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.HttpConnections.NasdaqIntradayHttpRequest;
import com.adi.ho.jackie.bubblestocks.HttpConnections.SpyHttpRequests;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class StockSyncAdapter extends AbstractThreadedSyncAdapter {
    private static String TAG = StockSyncAdapter.class.getCanonicalName();

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    DBStock stock1;
    private Context context;
    String date;

    /**
     * Set up the sync adapter
     */
    public StockSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        this.context = context;

    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public StockSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
//

        ExecutorService marketThreads = Executors.newFixedThreadPool(2);
        System.out.println("Sync adapter running");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                   Stock stock = YahooFinance.get("NASD");
                    //System.out.println(stock.toString());
                    System.out.println("price of stock is " + stock.getQuote());
                    System.out.println("s" + stock.getStats());
                    stock1 = setStockInfo(stock);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
     //   thread.start();
       marketThreads.execute(nasdaqRunnable);
        marketThreads.execute(spyRunnable);
    }


    private DBStock setStockInfo(Stock stock) {
        DBStock anyStock = new DBStock();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());
        anyStock.setDay(formattedDate);
        anyStock.setDayClose(stock.getQuote().getPrice().toString());
        anyStock.setDayHigh(stock.getQuote().getDayHigh().toString());
        anyStock.setDayOpen(stock.getQuote().getOpen().toString());
        anyStock.setDayLow(stock.getQuote().getDayLow().toString());
        anyStock.setSymbol(stock.getSymbol());
        anyStock.setAvgVol(stock.getQuote().getAvgVolume());
        anyStock.setDiviYield(stock.getDividend().getAnnualYieldPercent().toString());
        anyStock.setMarketCap(Double.parseDouble(stock.getStats().getMarketCap().toString()));
        anyStock.setYearLow(stock.getQuote().getYearLow().toString());
        anyStock.setYearHigh(stock.getQuote().getYearHigh().toEngineeringString());
        anyStock.setRevenue(Double.parseDouble(stock.getStats().getRevenue().toString()));
        anyStock.setOneYearPriceEstimate(stock.getStats().getOneYearTargetPrice().toString());
        anyStock.setPeg(stock.getStats().getPeg().toString());
        anyStock.setPe(stock.getStats().getPe().toPlainString());
        anyStock.setEps(stock.getStats().getEps().toString());
        //stick these in database
        anyStock.setLastTradeTime(stock.getQuote().getLastTradeTimeStr());
        anyStock.setChange(stock.getQuote().getChange().toString());
        anyStock.setPercentChange(stock.getQuote().getChangeInPercent().toString());
        stock.getQuote().getAskSize();
        return anyStock;
    }

    Runnable spyRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Stock stock = YahooFinance.get("SPY");
                String stocksymbol = "SPY";
                String lastPrice = stock.getQuote().getPrice().toString();
                ContentValues spyValues = new ContentValues();
                spyValues.put(StockDBHelper.COLUMN_STOCK_PRICE, lastPrice);
                Uri uri = Uri.parse(StockContentProvider.CONTENT_URI + "/2");
                mContentResolver.update(uri,spyValues,StockDBHelper.COLUMN_STOCK_SYMBOL + " = ? ",new String[]{"SPY"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable nasdaqRunnable = new Runnable() {
        String nasdaqData;
        @Override
        public void run() {
            try {
               nasdaqData = new NasdaqIntradayHttpRequest().run();
                nasdaqData = nasdaqData.substring(nasdaqData.length()-9,nasdaqData.length()-2);
                if (nasdaqData.contains(",")){
                    nasdaqData = nasdaqData.substring(1);
                }
                Uri uri = Uri.parse(StockContentProvider.CONTENT_URI + "/1");
                ContentValues nasdaqValues = new ContentValues();
                nasdaqValues.put(StockDBHelper.COLUMN_STOCK_PRICE, nasdaqData);
                mContentResolver.update(uri,nasdaqValues, StockDBHelper.COLUMN_STOCK_SYMBOL + " = ?", new String[]{"IXIC"});

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
