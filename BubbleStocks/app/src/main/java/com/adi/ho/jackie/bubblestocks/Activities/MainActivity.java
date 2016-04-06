package com.adi.ho.jackie.bubblestocks.activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.fragments.MarketDataFragment;
import com.adi.ho.jackie.bubblestocks.fragments.PortfolioFragment;
import com.adi.ho.jackie.bubblestocks.fragments.StockDetailFragment;
import com.adi.ho.jackie.bubblestocks.fragments.StockFragment;
import com.adi.ho.jackie.bubblestocks.fragments.TopNewsFragment;
import com.adi.ho.jackie.bubblestocks.httpconnections.DowHttpRequests;
import com.adi.ho.jackie.bubblestocks.httpconnections.IntradayMarketDataRequest;
import com.adi.ho.jackie.bubblestocks.httpconnections.NasdaqHttpRequest;
import com.adi.ho.jackie.bubblestocks.httpconnections.NyseHttpRequest;
import com.adi.ho.jackie.bubblestocks.httpconnections.SearchSuggestionHttpRequest;
import com.adi.ho.jackie.bubblestocks.httpconnections.SpyHttpRequests;
import com.adi.ho.jackie.bubblestocks.network.NetworkUtil;
import com.adi.ho.jackie.bubblestocks.stockportfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.stockportfolio.HistoricalStockQuoteWrapper;
import com.adi.ho.jackie.bubblestocks.stockportfolio.PortfolioStock;
import com.adi.ho.jackie.bubblestocks.customviews.BubbleImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class MainActivity extends AppCompatActivity implements StockFragment.SelectStock, OnChartValueSelectedListener, StockDetailFragment.OnTrackedListener {
    private static final String yahooCall = "http://d.yimg.com/aq/autoc?query=";
    private static final String yahooTags = "&region=US&lang=en-US";
    public static final String AUTHORITY = "com.adi.ho.jackie.bubblestocks.database.StockContentProvider";
    // Account type
    public static final String ACCOUNT_TYPE = "investor.com";
    // Account
    public static final String ACCOUNT = "default_account";
    private MaterialSearchView mMaterialSearchView;
    private static final String INITIAL_START = "Initialized";
    private static final String SYNC_STARTED = "SYNC_START";

    private ConnectionBroadcastReceiver mBroadcastReceiver;
    Account mAccount;
    ContentResolver mResolver;
    Toolbar toolbar;

    private PieChart mMainNavigationTool;
    private List<String> fragmentTags;
    private boolean initialSync = true;
    private List<ImageView> bubbleList;
    private List<Integer> bubbleDrawableList;
    private RelativeLayout mParentLayout;
    private ArrayList<Integer> colors;
    private PortfolioFragment portfolioFragment;
    private boolean syncActivated;
    private OkHttpClient client;
    private ArrayList<String> listSymbols;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResolver = getContentResolver();
        mAccount = createSyncAccount(this);
        //Check if database is initialized
        Cursor cursor = mResolver.query(StockContentProvider.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() == 0) {
            initializeDatabase();
        }
        cursor.close();

        //Sync stock and market prices
        autoSyncStocks();
        fragmentTags = Arrays.asList(getResources().getStringArray(R.array.fragment_stack_tag));
        bubbleList = new ArrayList<>();
        bubbleDrawableList = new ArrayList<>();
        listSymbols = new ArrayList<>();
        client = new OkHttpClient();
        mBroadcastReceiver = new ConnectionBroadcastReceiver();
        //Toolbar search reference
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mMaterialSearchView = (MaterialSearchView) findViewById(R.id.material_searchview);
        mMainNavigationTool = (PieChart) findViewById(R.id.home_navigationmenu);
        mParentLayout = (RelativeLayout) findViewById(R.id.main_activity_rel_layout);


        setSupportActionBar(toolbar);


        //Search for stocks
        mMaterialSearchView.setHint("Enter stock symbol.");
        mMaterialSearchView.setOnQueryTextListener(msvListener);



        //Create nav menu
        homeNavigationMenu();

        //Register broadcast receiver;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(mBroadcastReceiver, filter);
    }

    MaterialSearchView.OnQueryTextListener msvListener = new MaterialSearchView.OnQueryTextListener() {
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
                        if (stockSearch != null) {
                            selectedStock(stockSearch);
                            Log.i("STOCKSEARCH", "Searched for " + stockSymbol);
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid Stock Symbol", Toast.LENGTH_SHORT).show();
                        }
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
            if (newText.length() > 0) {
                new SearchAsynctask().execute(newText);
            }
            return true;
        }
    };

    //Create default account
    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {

        } else {

        }
        return newAccount;
    }

    private void autoSyncStocks() {
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        initialSync = settings.getBoolean(INITIAL_START, true);
        syncActivated = settings.getBoolean(SYNC_STARTED, false);

        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        SimpleDateFormat tradingTimeRange = new SimpleDateFormat("EEE, dd MMM HH:mm");
        String dateAndTime = tradingTimeRange.format(currentTime.getTime());

        //Activate or deactive syncadapter based off current time in NY
        if (dateAndTime.contains("Sat") || dateAndTime.contains("Sun")) {

            ContentResolver.setIsSyncable(mAccount, AUTHORITY, 0);
            ContentResolver.cancelSync(null, null);
            Log.d("SYNCADAPTER", "Sync canceled");
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(SYNC_STARTED, false);
            editor.commit();

        } else try {
            //Check for times between 9 am - 4 pm
            if (checkIfTradingTimeRange(0)) {
                if (initialSync) {
                    ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
                    ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
                    long seconds = 60;
                    ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, seconds);
                    Log.i(MainActivity.class.getName(), "Sync started, size 1");
                    //Start sync service once
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(INITIAL_START, false);
                    editor.putBoolean(SYNC_STARTED, true);
                    editor.commit();
                } else if (!syncActivated){
                    ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
                    ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
                    long seconds = 60;
                    ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, seconds);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(SYNC_STARTED, true);
                    editor.commit();
                }

            } else {
                ContentResolver.setIsSyncable(mAccount, AUTHORITY, 0);
                ContentResolver.cancelSync(null, null);
                Log.d("SYNCADAPTER", "Sync canceled");
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SYNC_STARTED, false);
                editor.commit();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mMaterialSearchView.setMenuItem(searchItem);
        mMaterialSearchView.setSubmitOnClick(true);
        return true;
    }

    //Retrieve intraday and historical data
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

    // ============================Convert Stock object to Parcelable objects for fragments==========================
    public static DBStock setStockInfo(Stock stock) {
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
                        int count = fragmentManager.getBackStackEntryCount();
                        marketDataTransaction.replace(R.id.marketdata_fragmentcontainer, marketDataFragment).addToBackStack(fragmentTags.get(count)).commit();
                    }
                }
            }
        };

        Thread marketThread = new Thread(runnable);
        marketThread.start();

    }

    // ========================================Animation Part =======================================

    @Override
    protected void onResume() {
        super.onResume();
        animateBubbling();

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

       colors = new ArrayList<Integer>();

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

        ArrayList<Integer> navWheelColors = new ArrayList<>();
        navWheelColors.add(ContextCompat.getColor(this,R.color.navWheelColor1));
        navWheelColors.add(ContextCompat.getColor(this,R.color.navWheelColor2));
        navWheelColors.add(ContextCompat.getColor(this,R.color.navWheelColor3));
        navWheelColors.add(ContextCompat.getColor(this,R.color.navWheelColor4));
        dataSet.setColors(navWheelColors);

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
        s.setSpan(new RelativeSizeSpan(2.1f), 0, 6, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 6, s.length() - 6, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0,  6, 0);
        s.setSpan(new RelativeSizeSpan(1.9f), 9, s.length() - 6, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 6, s.length()-5, 0);
        s.setSpan(new ForegroundColorSpan(Color.RED), s.length() - 6, s.length(), 0);
        return s;
    }


    //====================================Nav Wheel==========================================================
    FragmentManager fragmentManager = getSupportFragmentManager();
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        switch (e.getXIndex()) {
            case 0:
                //Toast.makeText(MainActivity.this, "Portfolio", Toast.LENGTH_SHORT).show();
                //mMainNavigationTool.animateXY(3400, 3400, Easing.EasingOption.EaseOutCirc, Easing.EasingOption.EaseOutCirc);
                portfolioFragment = new PortfolioFragment();
                FragmentTransaction portfolioTransaction = fragmentManager.beginTransaction();
                portfolioTransaction.replace(R.id.stock_fragmentcontainer, portfolioFragment).addToBackStack("PORTFOLIO").commit();
                break;
            case 1:
              //  mMainNavigationTool.animateXY(3400, 3400, Easing.EasingOption.EaseOutCubic, Easing.EasingOption.EaseOutCubic);
                TopNewsFragment topNewsFragment = new TopNewsFragment();

                FragmentTransaction newsFragmentTransaction = fragmentManager.beginTransaction();
                newsFragmentTransaction.replace(R.id.topnews_fragmentcontainer, topNewsFragment).addToBackStack(null).commit();
                break;
            case 2:
                getMarketData();
                break;
            case 3:
                Toast.makeText(MainActivity.this, "Currently disabled.", Toast.LENGTH_SHORT).show();
                //mMainNavigationTool.animateXY(3400, 3400, Easing.EasingOption.EaseOutElastic, Easing.EasingOption.EaseOutElastic);
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

    //
    public static boolean checkIfTradingTimeRange(int number) throws ParseException {
        String string1 = "9";
        String minutes = "30";
        SimpleDateFormat startDateTime;
        Calendar calendar1;
        String startTime;
        calendar1 = Calendar.getInstance();
        calendar1.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        switch (number) {
            case 0:
                startDateTime = new SimpleDateFormat("HH");
                startTime = startDateTime.format(startDateTime.parse(string1));
                calendar1.set(Calendar.HOUR_OF_DAY, 9);
                calendar1.set(Calendar.MINUTE, 0);
                break;
            case 1:
                startDateTime = new SimpleDateFormat("HH:mm");
                startTime = startDateTime.format(startDateTime.parse(string1 + ":" + minutes));
                calendar1.set(Calendar.HOUR_OF_DAY, 9);
                calendar1.set(Calendar.MINUTE, 31);
                break;
        }

        String string2 = "16";
        SimpleDateFormat endDateTime = new SimpleDateFormat("HH");
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String endTime = endDateTime.format(endDateTime.parse(string2));

        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        calendar2.set(Calendar.HOUR_OF_DAY, 16);
        calendar2.set(Calendar.MINUTE, 0);

        Date nowTime = currentTime.getTime();
        if (nowTime.after(calendar1.getTime()) && nowTime.before(calendar2.getTime())) {
            return true;
        } else {
            return false;
        }
    }


    //Generate random sized bubbles and color
    private GradientDrawable dynamicGenerateBubbleShape(int diameter, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setGradientRadius(150f);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        drawable.setDither(true);
        drawable.setStroke((int) 2, Color.parseColor("#EEEEEE"));
        drawable.setSize(diameter, diameter);
        drawable.setAlpha(200);
        return drawable;
    }

    private void setupBubbleAnimation(){

        int duration = (int) (Math.random() * 30000) + 13000;
        int diameter = (int)(Math.random()*70)+60;
        Random randColor = new Random();
        final BubbleImageView image = new BubbleImageView(this);
//        image.setImageDrawable(dynamicGenerateBubbleShape(diameter, colors.get(randomColor)));
        image.setBackground(dynamicGenerateBubbleShape(diameter, colors.get(randColor.nextInt(colors.size()))));
        // Adds the view to the layout
        mParentLayout.addView(image);
        //TODO: change to width and height of screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        float scaley = (float) (Math.random() * height + 150) * 1f;
        float scalex = (float) (Math.random() * width + 20);

        image.setOnClickListener(bubblePop);
        image.animate().setInterpolator(new AccelerateDecelerateInterpolator())
                .alpha(1)
                .setDuration(duration)
                .x(scalex)
                .y(scaley)
        .setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                image.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                image.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }
    View.OnClickListener bubblePop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setVisibility(View.GONE);
            if (v.getAnimation() != null){
                v.getAnimation().cancel();
            }
        }
    };


    //Animation bubble
    private void animateBubbling(){
        Handler animationHandler = new Handler();
        for (int i = 0; i < 50; i++) {
            Runnable animationRunnable = new Runnable() {
                @Override
                public void run() {
                    setupBubbleAnimation();
                }
            };
            animationHandler.post(animationRunnable);
         //   setupBubbleAnimation();
        }
    }

    @Override
    public void onStockTracked(PortfolioStock trackedStock) {
        //portfolioFragment = (PortfolioFragment)
        //fragmentManager.findFragmentByTag("PORTFOLIO");

        //Add bubble to portfolio if portfolio fragment is opened.
        if (portfolioFragment != null){
            portfolioFragment.addBubbleToPortfolio(trackedStock);
        }
    }
    private class SearchAsynctask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listSymbols.clear();
        }

        @Override
        protected String[] doInBackground(String... params) {
            Request request = new Request.Builder()
                    .url(yahooCall + params[0] + yahooTags)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                String data = response.body().string();
                JSONObject initialSearch = new JSONObject(data);
                JSONObject resultSet = initialSearch.getJSONObject("ResultSet");
                JSONArray resultArray = resultSet.getJSONArray("Result");
                for (int i = 0 ; i < resultArray.length() ; i++){
                    JSONObject suggestion = resultArray.getJSONObject(i);
                    String sugg = "("+suggestion.getString("symbol")+") ";
                    listSymbols.add(suggestion.getString("symbol"));
                }

                //Keep list 5 items or under
                if (listSymbols.size() <=5){
                    String[] resultList = new String[listSymbols.size()];
                    for (int j = 0; j < listSymbols.size(); j++){
                        resultList[j] = listSymbols.get(j);
                    }
                    return resultList;
                } else {
                    String[] resultList = new String[5];
                    for (int j = 0; j < 5; j++){
                        resultList[j] = listSymbols.get(j);
                    }
                    return resultList;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            //the suggestion filter isn't good.
            if (strings.length>0){
                mMaterialSearchView.setSuggestions(strings);

            }

        }
    }

    //broadcast detect network availability
    public class ConnectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = NetworkUtil.getConnectivityStatusString(context);
            if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    mMaterialSearchView.setOnQueryTextListener(null);
                    mMainNavigationTool.setOnChartValueSelectedListener(null);
                    Toast.makeText(MainActivity.this, "Connection lost.", Toast.LENGTH_SHORT).show();


                } else {
                    mMaterialSearchView.setOnQueryTextListener(msvListener);
                    mMainNavigationTool.setOnChartValueSelectedListener(MainActivity.this);
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}

