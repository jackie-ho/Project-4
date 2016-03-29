package com.adi.ho.jackie.bubblestocks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.adi.ho.jackie.bubblestocks.Database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.Database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.Fragments.MarketDataFragment;
import com.adi.ho.jackie.bubblestocks.Fragments.StockDetailFragment;
import com.adi.ho.jackie.bubblestocks.Fragments.StockFragment;
import com.adi.ho.jackie.bubblestocks.HttpConnections.DowHttpRequests;
import com.adi.ho.jackie.bubblestocks.HttpConnections.IntradayMarketDataRequest;
import com.adi.ho.jackie.bubblestocks.HttpConnections.NasdaqHttpRequest;
import com.adi.ho.jackie.bubblestocks.HttpConnections.NyseHttpRequest;
import com.adi.ho.jackie.bubblestocks.HttpConnections.SpyHttpRequests;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.HistoricalStockQuoteWrapper;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.MarketData;
import com.adi.ho.jackie.bubblestocks.oauth.TradeKingClient;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class MainActivity extends AppCompatActivity implements StockFragment.SelectStock, OnChartValueSelectedListener {

    public static final String stockTwitsAuthenticate = "";
    public static final String AUTHORITY = "com.adi.ho.jackie.bubblestocks.Database.StockContentProvider";
    // Account type
    public static final String ACCOUNT_TYPE = "example.com";
    // Account
    public static final String ACCOUNT = "default_account";
    private MaterialSearchView mMaterialSearchView;

    Account mAccount;
    ContentResolver mResolver;
    Toolbar toolbar;
    private ContentObserver mObserver;
    private PieChart mMainNavigationTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResolver = getContentResolver();
        mAccount = createSyncAccount(this);
        //Check if database is initialized
        Cursor cursor = mResolver.query(StockContentProvider.CONTENT_URI , null, null, null, null);
        if (cursor.getCount()==0) {
            initializeDatabase();
        }

        cursor.close();
        autoSyncStocks();
        //Register content observer
        mObserver = new StockContentObserver(new Handler());
        getContentResolver().registerContentObserver(StockContentProvider.CONTENT_URI,true,mObserver);
        //Toolbar search
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mMaterialSearchView = (MaterialSearchView) findViewById(R.id.material_searchview);
        mMainNavigationTool = (PieChart) findViewById(R.id.home_navigationmenu);
        setSupportActionBar(toolbar);

        /*  Oauth permission for Tradeking
         *
         *
         */


        //Get data for US stock exchanges
        //getMarketData();

        //Search for stocks
        mMaterialSearchView.setHint("Enter stock symbol.");
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
                mMaterialSearchView.closeSearch();
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //Create nav menu
        homeNavigationMenu();
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
        long seconds = 120;
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, seconds);
        // Pass the settings flags by inserting them in a bundle

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
        //Get historical data for the searched stock.
        ArrayList<HistoricalStockQuoteWrapper> historicalQuoteList = new ArrayList<>();
        DBStock parcelingStock = new DBStock();
        Calendar lastSixMonths = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        String intradayData = "";
        lastSixMonths.add(Calendar.MONTH, -6);
        try {
            parcelingStock = setStockInfo(searchedStock);
            //get intraday data
            intradayData = new IntradayMarketDataRequest().run(searchedStock.getSymbol());
            //historical data includes open,close, high, and low prices as well as volume
            for (HistoricalQuote historicalQuote : searchedStock.getHistory(lastSixMonths, yesterday, Interval.DAILY)) {
                historicalQuoteList.add(0, new HistoricalStockQuoteWrapper(historicalQuote));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (parcelingStock != null && parcelingStock.getSymbol() != null && historicalQuoteList.size() > 0) {
                StockDetailFragment stockDetailFragment = new StockDetailFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction stockTransaction = fragmentManager.beginTransaction();
                Bundle stockBundle = new Bundle();
                stockBundle.putParcelable("EXSTOCK", parcelingStock);
                stockBundle.putParcelableArrayList("HISTORICALQUOTE", historicalQuoteList);
                stockBundle.putString("INTRADAY", intradayData);
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
        anyStock.setAvgVolBarEntries(String.valueOf(stock.getQuote().getAvgVolume()));
        //stick these in database
        anyStock.setLastTradeTime(stock.getQuote().getLastTradeTimeStr());
        anyStock.setChange(stock.getQuote().getChange().toString());
        anyStock.setPercentChange(stock.getQuote().getChangeInPercent().toString());
        return anyStock;
    }

    public void getMarketData() {
        Runnable runnable = new Runnable() {
            String dowIndexAvg = "";
            String nasdaqIndexAvg = "";
            String spIndexAvg = "";
            String nyseIndexAvg = "";

            @Override
            public void run() {
                try {
                    Calendar fiveYearsAgo = Calendar.getInstance();
                    Stock marketStock = YahooFinance.get("SPY", true);
                    fiveYearsAgo.add(Calendar.YEAR, -5);
                    String format = "yyyy-MM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                    String fiveYearsAgoDate = sdf.format(fiveYearsAgo.getTime());

                    //Get indexes through quandl api
                    dowIndexAvg = new DowHttpRequests().run(fiveYearsAgoDate);
                    nasdaqIndexAvg = new NasdaqHttpRequest().run(fiveYearsAgoDate);
                    spIndexAvg = new SpyHttpRequests().run(fiveYearsAgoDate);
                    nyseIndexAvg = new NyseHttpRequest().run(fiveYearsAgoDate);


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (dowIndexAvg.length() > 0 && nasdaqIndexAvg.length() > 0 && spIndexAvg.length() > 0 && nyseIndexAvg.length() > 0) {
                        MarketDataFragment marketDataFragment = new MarketDataFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction marketDataTransaction = fragmentManager.beginTransaction();
                        Bundle stockBundle = new Bundle();
                        stockBundle.putString("DOW", dowIndexAvg);
                        stockBundle.putString("NASDAQ", nasdaqIndexAvg);
                        stockBundle.putString("SP", spIndexAvg);
                        stockBundle.putString("NYSE", nyseIndexAvg);
                        marketDataFragment.setArguments(stockBundle);
                        marketDataTransaction.replace(R.id.marketdata_fragmentcontainer, marketDataFragment).addToBackStack(null).commit();
                    }
                }
            }
        };

        Thread marketThread = new Thread(runnable);
        marketThread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Home");
    }

    private void homeNavigationMenu() {

        mMainNavigationTool.setUsePercentValues(true);
        mMainNavigationTool.setDescription("");
        mMainNavigationTool.setExtraOffsets(5, 10, 5, 5);

        mMainNavigationTool.setDragDecelerationFrictionCoef(0.95f);

        mMainNavigationTool.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        mMainNavigationTool.setCenterText(generateCenterSpannableText());

        mMainNavigationTool.setDrawHoleEnabled(true);
        mMainNavigationTool.setHoleColor(Color.WHITE);

        mMainNavigationTool.setTransparentCircleColor(Color.WHITE);
        mMainNavigationTool.setTransparentCircleAlpha(110);

        mMainNavigationTool.setHoleRadius(58f);
        mMainNavigationTool.setTransparentCircleRadius(61f);

        mMainNavigationTool.setDrawCenterText(true);

        mMainNavigationTool.setRotationAngle(0);
        // enable rotation of the chart by touch
        mMainNavigationTool.setRotationEnabled(true);
        mMainNavigationTool.setHighlightPerTapEnabled(true);

        mMainNavigationTool.setOnChartValueSelectedListener(this);

        setNavMenu();

        mMainNavigationTool.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mMainNavigationTool.getLegend().setEnabled(false);
    }

    private void setNavMenu() {


        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < 4; i++) {
            yVals1.add(new Entry(1, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Portfolio");
        xVals.add("News");
        xVals.add("Market");
        xVals.add("Notifications");

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setDrawValues(false);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf"));
        mMainNavigationTool.setData(data);

        // undo all highlights
        mMainNavigationTool.highlightValues(null);

        mMainNavigationTool.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Bubble \n Stocks");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 6, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 6, s.length() - 6, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 6, s.length() - 6, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 6, s.length() - 6, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 6, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 6, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        switch (e.getXIndex()) {
            case 0:
                Toast.makeText(MainActivity.this, "Portfolio", Toast.LENGTH_SHORT).show();
                mMainNavigationTool.animateXY(3400, 3400, Easing.EasingOption.EaseOutCirc, Easing.EasingOption.EaseOutCirc);

                break;
            case 1:
                Toast.makeText(MainActivity.this, "News", Toast.LENGTH_SHORT).show();
                mMainNavigationTool.animateXY(3400, 3400, Easing.EasingOption.EaseOutCubic, Easing.EasingOption.EaseOutCubic);
                break;
            case 2:
                getMarketData();
                mMainNavigationTool.animateXY(3400, 3400, Easing.EasingOption.EaseOutQuad, Easing.EasingOption.EaseOutQuad);
                break;
            case 3:
                Toast.makeText(MainActivity.this, "Notifications", Toast.LENGTH_SHORT).show();
                mMainNavigationTool.animateXY(3400, 3400, Easing.EasingOption.EaseOutElastic, Easing.EasingOption.EaseOutElastic);
                break;
        }
    }

    @Override
    public void onNothingSelected() {

    }

    private void initializeDatabase() {
        ContentValues nasdaqValues = new ContentValues();
        nasdaqValues.put(StockDBHelper.COLUMN_STOCK_SYMBOL, "IXIC");
        nasdaqValues.put(StockDBHelper.COLUMN_STOCK_PRICE, "0");
        nasdaqValues.put(StockDBHelper.COLUMN_STOCK_TRACKED, 0);
        nasdaqValues.put(StockDBHelper.COLUMN_STOCK_OPENPRICE, "0");

        Uri uri1 = getContentResolver().insert(StockContentProvider.CONTENT_URI, nasdaqValues);
        Log.i("CONTENTPROVIDER", "Inserted nasdaq into uri: " + uri1.toString());

        ContentValues spyValues = new ContentValues();
        spyValues.put(StockDBHelper.COLUMN_STOCK_SYMBOL, "SPY");
        spyValues.put(StockDBHelper.COLUMN_STOCK_PRICE, "0");
        spyValues.put(StockDBHelper.COLUMN_STOCK_TRACKED, 0);
        spyValues.put(StockDBHelper.COLUMN_STOCK_OPENPRICE, "0");

        Uri uri2 = getContentResolver().insert(StockContentProvider.CONTENT_URI, spyValues);
        Log.i("CONTENTPROVIDER", "Inserted spy into uri: " + uri2.toString());

    }
    public class StockContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public StockContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            //do stuff on UI thread
            Log.d(MainActivity.class.getName(), "CHANGE OBSERVED AT URI: " + uri);
            Cursor cursor = getContentResolver().query(StockContentProvider.CONTENT_URI, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String price = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_PRICE));
                Log.i("CONTENTOBSERVER", "New Price is: "+price);
                cursor.moveToNext();
            } cursor.close();


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver);
    }
}

