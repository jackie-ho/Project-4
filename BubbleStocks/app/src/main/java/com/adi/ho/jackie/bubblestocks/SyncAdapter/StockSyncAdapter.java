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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.adi.ho.jackie.bubblestocks.Database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.Database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.HttpConnections.MarkitHttpSyncRequest;
import com.adi.ho.jackie.bubblestocks.HttpConnections.NasdaqIntradayHttpRequest;
import com.adi.ho.jackie.bubblestocks.HttpConnections.SpyHttpRequests;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    Cursor dbCursor;

    /**
     * Set up the sync adapter
     */
    public StockSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        this.context = context;

    }

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
    public void onPerformSync(Account account, Bundle extras, String authority, final ContentProviderClient provider, SyncResult syncResult) {
//
        int numberOfStockItems = 0;
        List<StockSyncItem> stockPortfolioList = new ArrayList<>();
        try {
            dbCursor = provider.query(StockContentProvider.CONTENT_URI, null, null, null, null);
            numberOfStockItems = dbCursor.getCount();
            dbCursor.moveToPosition(2);
            while (!dbCursor.isAfterLast()) {
                int id = dbCursor.getInt(dbCursor.getColumnIndex(StockDBHelper.COLUMN_ID));
                String symbol = dbCursor.getString(dbCursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_SYMBOL));
                stockPortfolioList.add(new StockSyncItem(id, symbol));
                dbCursor.moveToNext();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            dbCursor.close();
            //Threadpool get market data
           // ExecutorService marketThreads = Executors.newFixedThreadPool(2);
         //   marketThreads.execute(nasdaqRunnable);
           // marketThreads.execute(spyRunnable);
            //Async task to retrieve "tracked" stocks
            for (int i = 0; i < stockPortfolioList.size(); i++) {
                new StockQuoteRequestAsync().execute(stockPortfolioList.get(i));
            }

        }


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
                mContentResolver.update(uri, spyValues, StockDBHelper.COLUMN_STOCK_SYMBOL + " = ? ", new String[]{"SPY"});
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
                nasdaqData = nasdaqData.substring(nasdaqData.length() - 9, nasdaqData.length() - 2);
                if (nasdaqData.contains(",")) {
                    nasdaqData = nasdaqData.substring(1);
                }
                Uri uri = Uri.parse(StockContentProvider.CONTENT_URI + "/1");
                ContentValues nasdaqValues = new ContentValues();
                nasdaqValues.put(StockDBHelper.COLUMN_STOCK_PRICE, nasdaqData);
                mContentResolver.update(uri, nasdaqValues, StockDBHelper.COLUMN_STOCK_SYMBOL + " = ?", new String[]{"IXIC"});

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private class StockQuoteRequestAsync extends AsyncTask<StockSyncItem, Void, String> {
        DBStock stock = new DBStock();
        String data = "";
        String id = "";
        boolean callSuccess = false;

        @Override
        protected String doInBackground(StockSyncItem... params) {
            String price = "0";
            try {
                //stock = YahooFinance.get(params[0].getSymbol());
//                price = stock.getQuote().getPrice().toString();
                id = String.valueOf(params[0].getId());
                data = new MarkitHttpSyncRequest().run(params[0].getSymbol());
                JSONObject stockObject = new JSONObject(data);
                if (stockObject.getString("Status").equals("SUCCESS")) {
                    callSuccess = true;
                    stock.setDayOpen(stockObject.getString("Open"));
                    stock.setDayClose(stockObject.getString("LastPrice"));
                    stock.setTodaysVolume(stockObject.getDouble("Volume"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (callSuccess) {
                    Uri uri = Uri.parse(StockContentProvider.CONTENT_URI + "/" + id);
                    ContentValues stockValues = new ContentValues();
                    stockValues.put(StockDBHelper.COLUMN_STOCK_PRICE, stock.getDayClose());
                    stockValues.put(StockDBHelper.COLUMN_STOCK_OPENPRICE, stock.getDayOpen());
                    stockValues.put(StockDBHelper.COLUMN_VOLUME, stock.getTodaysVolume());
                    mContentResolver.update(uri, stockValues, StockDBHelper.COLUMN_ID + " = ? ", new String[]{id});
                }
            }
            return price;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
