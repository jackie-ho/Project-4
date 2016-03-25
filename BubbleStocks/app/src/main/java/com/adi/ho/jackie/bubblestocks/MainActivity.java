package com.adi.ho.jackie.bubblestocks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adi.ho.jackie.bubblestocks.Fragments.StockDetailFragment;
import com.adi.ho.jackie.bubblestocks.Fragments.StockFragment;
import com.adi.ho.jackie.bubblestocks.HttpConnections.HttpRequests;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.HistoricalStockQuoteWrapper;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuotesData;

public class MainActivity extends AppCompatActivity implements StockFragment.SelectStock {

    public static final String stockTwitsAuthenticate = "";
    DBStock stock1;
    public static final String AUTHORITY = "com.adi.ho.jackie.bubblestocks.Database.StockContentProvider";
    // Account type
    public static final String ACCOUNT_TYPE = "example.com";
    // Account
    public static final String ACCOUNT = "default_account";

    private MaterialSearchView mMaterialSearchView;

    RealmConfiguration realmConfig;
    Realm realm;
    Account mAccount;
    ContentResolver mResolver;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResolver = getContentResolver();
        mAccount = createSyncAccount(this);
        //  autoSyncStocks();

        //Toolbar search
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mMaterialSearchView = (MaterialSearchView) findViewById(R.id.material_searchview);
        setSupportActionBar(toolbar);


        StockFragment stockFragment = new StockFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction stockTransaction = fragmentManager.beginTransaction();
//            Bundle stockBundle = new Bundle();
//            stockBundle.putParcelable(stock1.getSymbol(), stock1);
//            stockFragment.setArguments(stockBundle);

        stockTransaction.replace(R.id.stock_fragmentcontainer, stockFragment).commit();

        //Search for stocks
        mMaterialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final String stockSymbol = query;
                //callStockDataFromLastHalfYear(stockSymbol);
                Runnable searchStockRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Stock stockSearch = null;
                        try {
                            stockSearch = YahooFinance.get(stockSymbol);

                            selectedStock(stockSearch);
                            Log.i("STOCKSEARCH", "Searched for " + stockSymbol);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Invalid Stock Symbol", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                Thread stockSearchThread = new Thread(searchStockRunnable);
                stockSearchThread.start();
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    private void autoSyncStocks() {
        //ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        long seconds = 20;
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, seconds);
        // Pass the settings flags by inserting them in a bundle

    }

    //Initiate Realm Database
    public void initiateRealmInstance() {
        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        realmConfig = new RealmConfiguration.Builder(MainActivity.this).build();
        // Get a Realm instance for this thread
        realm = Realm.getInstance(realmConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //realm.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mMaterialSearchView.setMenuItem(searchItem);

        return true;
    }

    @Override
    public void selectedStock(Stock searchedStock) {
        ArrayList<HistoricalStockQuoteWrapper> historicalQuoteList = new ArrayList<>();
        DBStock parcelingStock = new DBStock();
        Calendar lastSixMonths = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();

        lastSixMonths.add(Calendar.MONTH, -6);
        try {
            parcelingStock = setStockInfo(searchedStock);
            for (HistoricalQuote historicalQuote : searchedStock.getHistory(lastSixMonths, yesterday, Interval.DAILY)) {
                historicalQuoteList.add(new HistoricalStockQuoteWrapper(historicalQuote));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (parcelingStock != null && !parcelingStock.getSymbol().isEmpty() && historicalQuoteList.size() > 0) {
                StockDetailFragment stockDetailFragment = new StockDetailFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction stockTransaction = fragmentManager.beginTransaction();
                Bundle stockBundle = new Bundle();
                stockBundle.putParcelable("EXSTOCK", parcelingStock);
                stockBundle.putParcelableArrayList("HISTORICALQUOTE", historicalQuoteList);
                stockDetailFragment.setArguments(stockBundle);
                stockTransaction.replace(R.id.stock_fragmentcontainer, stockDetailFragment).addToBackStack(null).commit();
            }
        }
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
        //stick these in database
        anyStock.setLastTradeTime(stock.getQuote().getLastTradeTimeStr());
        anyStock.setChange(stock.getQuote().getChange().toString());
        anyStock.setPercentChange(stock.getQuote().getChangeInPercent().toString());
        stock.getQuote().getAskSize();
        return anyStock;
    }

}
