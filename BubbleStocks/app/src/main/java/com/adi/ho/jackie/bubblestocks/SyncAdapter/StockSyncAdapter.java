package com.adi.ho.jackie.bubblestocks.SyncAdapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

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

import io.realm.Realm;
import io.realm.RealmConfiguration;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class StockSyncAdapter extends AbstractThreadedSyncAdapter {
    private static String TAG = StockSyncAdapter.class.getCanonicalName();

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    RealmConfiguration realmConfig;
    Realm realm;

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
        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        realmConfig = new RealmConfiguration.Builder(context).build();
        // Get a Realm instance for this thread
        Realm realm = Realm.getInstance(realmConfig);
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
//        String data = "";
//        String[] stocks = {"AAPL", "GOOGL", "TSLA", "NFLX", "AMZN"};
//        String urlString = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=";
//
//        for (int i = 0; i < stocks.length; i++) {
//            try {
//                URL url = new URL(urlString + stocks[i]);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.connect();
//                InputStream inStream = connection.getInputStream();
//                data = getInputData(inStream);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//
//            Gson gson = new Gson();
//            StockItem stockItem = gson.fromJson(data, StockItem.class);
//
//            Log.d(TAG, "The Stock: " + stockItem.getName() + " " + stockItem.getLastPrice());
//        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Stock stock = YahooFinance.get("AAPL");
                    //System.out.println(stock.toString());
                    System.out.println("price of stock is " + stock.getQuote());
                    System.out.println("s" + stock.getStats());
                    DBStock stock1 = setStockInfo(stock);
                    realm.beginTransaction();
                    realm.copyToRealm(stock1);
                    realm.commitTransaction();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private String getInputData(InputStream inStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        String data = null;

        while ((data = reader.readLine()) != null) {
            builder.append(data);
        }

        reader.close();

        return builder.toString();
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
        anyStock.setAvgVol(String.valueOf(stock.getQuote().getAvgVolume()));
        anyStock.setDiviYield(stock.getDividend().getAnnualYieldPercent().toString());
        anyStock.setMarketCap(stock.getStats().getMarketCap().toString());
        anyStock.setYearLow(stock.getQuote().getYearLow().toString());
        anyStock.setYearHigh(stock.getQuote().getYearHigh().toEngineeringString());
        anyStock.setRevenue(stock.getStats().getRevenue().toString());
        anyStock.setPeg(stock.getStats().getPeg().toString());
        anyStock.setPe(stock.getStats().getPe().toPlainString());
        anyStock.setEps(stock.getStats().getEps().toString());
        return anyStock;
    }
}
