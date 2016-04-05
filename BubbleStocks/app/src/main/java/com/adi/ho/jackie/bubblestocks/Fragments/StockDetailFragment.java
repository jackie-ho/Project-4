package com.adi.ho.jackie.bubblestocks.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adi.ho.jackie.bubblestocks.customviews.LineCustomMarkerView;
import com.adi.ho.jackie.bubblestocks.database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.httpconnections.CompanySpecificNewsRequest;
import com.adi.ho.jackie.bubblestocks.activities.MainActivity;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.recyclerviewitems.DividerItemDecoration;
import com.adi.ho.jackie.bubblestocks.recyclerviewitems.NewsRecyclerAdapter;
import com.adi.ho.jackie.bubblestocks.recyclerviewitems.VerticalSpaceItemDecoration;
import com.adi.ho.jackie.bubblestocks.stockportfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.stockportfolio.HistoricalStockQuoteWrapper;
import com.adi.ho.jackie.bubblestocks.stockportfolio.IntradayStockData;
import com.adi.ho.jackie.bubblestocks.stockportfolio.PortfolioStock;
import com.adi.ho.jackie.bubblestocks.companyspecificrssfeed.CompanyResult;
import com.adi.ho.jackie.bubblestocks.customviews.CandleCustomMarkerView;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by JHADI on 3/23/16.
 */
public class StockDetailFragment extends Fragment {
    private CandleStickChart mChart;
    private CandleStickChart mFiveMinuteChart;
    private LineChart mDailyChart;
    private CombinedChart mCombinedThreeMChart;
    ArrayList<HistoricalStockQuoteWrapper> historicalStockQuoteWrappers;
    ArrayList<String> mXAxisDays;
    DBStock stockData;
    CandleDataSet set1;
    private String intradayJsonData;
    private String companyName;
    private LinkedList<IntradayStockData> intradayStockDataLinkedList;
    private ArrayList<HistoricalStockQuoteWrapper> mTempHistQuoteList;
    private Button oneDayGraphButton;
    private Button threeMonthGraphButton;
    private boolean dailyChartFlag = false;

    private TextView mYearHigh;
    private TextView mYearLow;
    private TextView mMarketCap;
    private TextView mEPS;
    private TextView mVol;
    private TextView mAvgVol;
    private TextView mPE;
    private TextView mDiviYield;
    private TextView mRevenue;
    private TextView mTarget;
    private TextView mStockTicker;
    private TextView mCompanyNameText;
    private TextView mTimeStampText;
    private YAxis mPriceAxis;
    private ImageView mArrowIcon;
    public TextView mPriceText;
    private boolean mTrackedFlag;
    private int stockId;
    private FloatingActionButton fab;
    private ContentObserver mObserver;
    private boolean isStockInDB = false;
    private RecyclerView mRecycler;
    private NewsRecyclerAdapter mAdapter;
    private List<com.adi.ho.jackie.bubblestocks.companyspecificrssfeed.Item> mArticleTitleList;
    public OnTrackedListener mTrackedListener;
    private Button mSixMonthDataButton;

    public static interface OnTrackedListener {
        public void onStockTracked(PortfolioStock newTrackedStock);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mTrackedListener = (OnTrackedListener)activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" needs to implement OnTrackedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_detailfragment, container, false);

        //References
        fab = (FloatingActionButton) view.findViewById(R.id.stock_detail_addtoportfolio);
        mPriceText = (TextView) view.findViewById(R.id.stock_detail_currentpricetext);
        mArrowIcon = (ImageView) view.findViewById(R.id.stock_detail_arrowicon);
        mStockTicker = (TextView) view.findViewById(R.id.stock_detail_ticker);
        mCompanyNameText = (TextView) view.findViewById(R.id.stock_detail_companyname);
        mChart = (CandleStickChart) view.findViewById(R.id.stock_detail_3mcandlestick);
        mDailyChart = (LineChart) view.findViewById(R.id.stock_detail_1dlinechart);
        mCombinedThreeMChart = (CombinedChart) view.findViewById(R.id.stock_detail_3mcombinedchart);
        mDiviYield = (TextView) view.findViewById(R.id.stock_detail_diviyieldtext);
        mEPS = (TextView) view.findViewById(R.id.stock_detail_epstext);
        mMarketCap = (TextView) view.findViewById(R.id.stock_Detail_stockmarketcaptext);
        mAvgVol = (TextView) view.findViewById(R.id.stock_detail_averagevolumetext);
        mVol = (TextView) view.findViewById(R.id.stock_detail_volumetext);
        mPE = (TextView) view.findViewById(R.id.stock_detail_petext);
        mRevenue = (TextView) view.findViewById(R.id.stock_detail_stockrevenuetext);
        mTarget = (TextView) view.findViewById(R.id.stock_detail_stockoneyeartarget);
        mYearHigh = (TextView) view.findViewById(R.id.stock_detail_52wkhightext);
        mYearLow = (TextView) view.findViewById(R.id.stock_detail_52wklowtext);
        mTimeStampText = (TextView)view.findViewById(R.id.time_ticker);
        mRecycler = (RecyclerView)view.findViewById(R.id.stock_detail_articlelist);

        mSixMonthDataButton = (Button)view.findViewById(R.id.sixmonth_databutton);
        oneDayGraphButton = (Button) view.findViewById(R.id.oneday_databutton);
        threeMonthGraphButton = (Button) view.findViewById(R.id.threemonth_databutton);
        historicalStockQuoteWrappers = new ArrayList<>();
        mXAxisDays = new ArrayList<>();
        intradayStockDataLinkedList = new LinkedList<>();
        mTempHistQuoteList = new ArrayList<>();
        mArticleTitleList = new ArrayList<>();

        mChart.setAutoScaleMinMaxEnabled(true);
        Bundle stockInfo = getArguments();
        if (stockInfo != null) {
            stockData = stockInfo.getParcelable("EXSTOCK");
            intradayJsonData = stockInfo.getString("INTRADAY");
            historicalStockQuoteWrappers = stockInfo.getParcelableArrayList("HISTORICALQUOTE");
            setStockData();

        }
        for (int i = 0; i < historicalStockQuoteWrappers.size(); i++) {
            mXAxisDays.add(String.valueOf(i));
        }

        //Register content observer
        mObserver = new StockContentObserver(new Handler());

        setUpSixMChart();
        checkIfTracked();

        return view;
    }

    //Check if the stock is already "tracked"
    private void checkIfTracked() {
        //To get id and to correctly display add to portfolio button
        Cursor cursor = getContext().getContentResolver().query(StockContentProvider.CONTENT_URI, StockDBHelper.ALL_COLUMNS,
                StockDBHelper.COLUMN_STOCK_SYMBOL + " = ? ", new String[]{stockData.getSymbol().toUpperCase()}, null, null);
        cursor.moveToFirst();
        String symbol = "";
        while (!cursor.isAfterLast()) {
            symbol = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_SYMBOL));
            if (symbol.equalsIgnoreCase(stockData.getSymbol())) {
                isStockInDB = true;
                break;
            }
            cursor.moveToNext();
        }

        if (isStockInDB) {
            mTrackedFlag = true;
            //     int counter = cursor.getCount();
            //get latest entry, hopefully there's only one.
            //   cursor.moveToPosition(counter-1);
            stockId = cursor.getInt(cursor.getColumnIndex(StockDBHelper.COLUMN_ID));
            fab.setVisibility(View.GONE);
            Log.v(StockDetailFragment.class.getName(), "Tracked already: " + stockData.getSymbol());

        } else {
            mTrackedFlag = false;
            ContentValues tempInsert = new ContentValues();
            tempInsert.put(StockDBHelper.COLUMN_STOCK_SYMBOL, stockData.getSymbol().toUpperCase());
            tempInsert.put(StockDBHelper.COLUMN_STOCK_PRICE, stockData.getDayClose());
            tempInsert.put(StockDBHelper.COLUMN_STOCK_OPENPRICE, stockData.getDayOpen());
            tempInsert.put(StockDBHelper.COLUMN_STOCK_TRACKED, 0);
            Uri uri = getContext().getContentResolver().insert(StockContentProvider.CONTENT_URI, tempInsert);
            stockId = Integer.parseInt(uri.getLastPathSegment());
            Log.i(StockDetailFragment.class.getName(), "Inserted into database: " + stockData.getSymbol() + " with id : " + stockId);

        }
        cursor.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        oneDayGraphButton.setOnClickListener(oneDayListener);
        threeMonthGraphButton.setOnClickListener(threeMonthListener);
        mSixMonthDataButton.setOnClickListener(sixMonthListener);
        fab.setOnClickListener(clickListener);
        if (stockData != null) {
            new RetrieveCompanyArticlesAsyncTask().execute(stockData.getSymbol());
        }
    }

    //==============================Async Task recall articles=======================================

    private class RetrieveCompanyArticlesAsyncTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... params) {
            String data = "";
            //Do it
            try {
                data =  new CompanySpecificNewsRequest().run(params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            Gson gson = new Gson();
            CompanyResult finalResult = gson.fromJson(s, CompanyResult.class);
            mArticleTitleList = finalResult.getQuery().getResults().getItem();
            mRecycler.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            mRecycler.setLayoutManager(llm);
            mAdapter = new NewsRecyclerAdapter(mArticleTitleList, getContext());
            mRecycler.addItemDecoration(new VerticalSpaceItemDecoration(40));
            mRecycler.addItemDecoration(new DividerItemDecoration(getContext()));
            mRecycler.setNestedScrollingEnabled(false);
            mRecycler.setAdapter(mAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //Get intraday data
        if (intradayJsonData.length() > 0 && intradayJsonData.contains("finance_charts_json_callback")) {
            intradayJsonData = intradayJsonData.substring(30);
            int jsonDataLength = intradayJsonData.length();
            intradayJsonData = intradayJsonData.substring(0, jsonDataLength - 2);

            try {
                JSONObject intradayInitialObject = new JSONObject(intradayJsonData);
                JSONObject metaIntradayObject = intradayInitialObject.getJSONObject("meta");
                companyName = metaIntradayObject.optString("Company-Name", "");
                mCompanyNameText.setText("(" + stockData.getSymbol().toUpperCase() + ") " + companyName + " - ");

                JSONArray intradayPricesArray = intradayInitialObject.getJSONArray("series");
                for (int i = 0; i < intradayPricesArray.length(); i++) {
                    intradayStockDataLinkedList.add(new IntradayStockData(intradayPricesArray.getJSONObject(i)));
                }
                Log.v("INTRADAY", "size of list: " + intradayStockDataLinkedList.size());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            setUpDailyChart();
            setVolume();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Register content observer
        getContext().getContentResolver().registerContentObserver(StockContentProvider.CONTENT_URI, true, mObserver);
        try {
            if (MainActivity.checkIfTradingTimeRange(1)) {
                Log.i(StockDetailFragment.class.getName(), "Manual sync requested");
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                getContext().getContentResolver().requestSync(MainActivity.createSyncAccount(getContext()), MainActivity.AUTHORITY, bundle);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void setUpSixMChart() {
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
     //   mChart.setMaxVisibleValueCount(60);//TODO:Change the max

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setMarkerView(new CandleCustomMarkerView(getContext(), R.layout.candlemarker_layout));
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        mChart.invalidate();
//
    }

    private void fillCandleCharts() {
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        List<CandleEntry> dailyCandleData = new LinkedList<>();
        int index = 0;
        for (HistoricalStockQuoteWrapper hist : historicalStockQuoteWrappers) {
            float low = Float.parseFloat(hist.getLowPrice());
            float high = Float.parseFloat(hist.getHighPrice());
            float open = Float.parseFloat(hist.getOpenPrice());
            float close = Float.parseFloat(hist.getClosePrice());

            dailyCandleData.add(new CandleEntry(index, low, high, open, close));
            index += 1;
        }

        dailyCandleData.add(new CandleEntry(dailyCandleData.size(), Float.parseFloat(stockData.getDayLow()), Float.parseFloat(stockData.getDayHigh()),
                Float.parseFloat(stockData.getDayOpen()), Float.parseFloat(stockData.getDayClose())));
        set1 = new CandleDataSet(dailyCandleData, "Price");
        int addOne = Integer.parseInt(mXAxisDays.get(dailyCandleData.size() - 2));
        mXAxisDays.add(String.valueOf(addOne));


        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowColorSameAsCandle(true);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(ContextCompat.getColor(getContext(), R.color.colorCandleDecreasing)); //
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(ContextCompat.getColor(getContext(), R.color.colorCandleIncreasing));
        set1.setIncreasingPaintStyle(Paint.Style.STROKE);
        set1.setNeutralColor(Color.BLUE);
        set1.setDrawValues(false);



        CandleData candleData = new CandleData(getPastSixMonths(), set1);
        mChart.setData(candleData);
        mChart.animateX(1400);
        mChart.invalidate();
    }

    private void setUpCombinedChart() {
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mCombinedThreeMChart.setDescription("");
        mCombinedThreeMChart.setBackgroundColor(Color.WHITE);
        mCombinedThreeMChart.setDrawGridBackground(false);
        mCombinedThreeMChart.setDrawBarShadow(false);

        // order where the charts appear
        mCombinedThreeMChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.SCATTER
        });

        mCombinedThreeMChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                h.getRange();
                e.getVal();
                e.getData();
                if (e instanceof CandleEntry){
                    mCombinedThreeMChart.setMarkerView(new CandleCustomMarkerView(getContext(),R.layout.candlemarker_layout));
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        YAxis rightAxis = mCombinedThreeMChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(4, true);
        rightAxis.setSpaceTop(85f); // sets space top to push volume bars below candles

        YAxis leftAxis = mCombinedThreeMChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
//        leftAxis.setDrawGridLines(false);
        //leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mCombinedThreeMChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(3);



        CombinedData data = new CombinedData(getPastThreeMonths());

        //Temporarily store historical data for easier transition to graph data
        int counter = historicalStockQuoteWrappers.size() - 1;
        while (mTempHistQuoteList.size() < 59) {
            mTempHistQuoteList.add(0, historicalStockQuoteWrappers.get(counter));
            counter--;
        }
        data.setData(getPastThreeMonthsPrices());
        data.setData(getPastThreeMonthsVolume());

        mCombinedThreeMChart.setData(data);
        mCombinedThreeMChart.invalidate();
        mCombinedThreeMChart.animateXY(1400, 1400);
    }

    View.OnClickListener oneDayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "Button clicked", Toast.LENGTH_SHORT).show();
            if (mDailyChart.getVisibility() == View.GONE) {
                mDailyChart.setVisibility(View.VISIBLE);
                mChart.setVisibility(View.GONE);
                mCombinedThreeMChart.setVisibility(View.GONE);

                if (dailyChartFlag == false) {
                    setUpDailyChart();
                } else {
                }
            }
        }
    };

    View.OnClickListener sixMonthListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mChart.getVisibility() == View.GONE){
                mChart.setVisibility(View.VISIBLE);
                mDailyChart.setVisibility(View.GONE);
                mCombinedThreeMChart.setVisibility(View.GONE);
                fillCandleCharts();
            }
        }
    };

    View.OnClickListener threeMonthListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: animate graph removal and addition with property animation, push it off screen instead of making it come visible
            if (mCombinedThreeMChart.getVisibility() == View.GONE) {
                mCombinedThreeMChart.setVisibility(View.VISIBLE);
                mDailyChart.setVisibility(View.GONE);
                mChart.setVisibility(View.GONE);
                setUpCombinedChart();

            }
            Toast.makeText(getContext(), "Pressed 3M", Toast.LENGTH_SHORT).show();
        }
    };

    private ArrayList<String> getPastThreeMonths() {
        ArrayList<String> threeMonthList = new ArrayList<>();
        if (historicalStockQuoteWrappers.size() > 0) {
            int counter = historicalStockQuoteWrappers.size();
            while (threeMonthList.size() < 59) {
                threeMonthList.add(0, historicalStockQuoteWrappers.get(counter - 1).getDayOfQuote());
                counter -= 1;
            }
            threeMonthList.add(stockData.getDay());

        }
        return threeMonthList;
    }

    private ArrayList<String> getPastSixMonths(){
        ArrayList<String> sixMonthList = new ArrayList<>();
        if (historicalStockQuoteWrappers != null && historicalStockQuoteWrappers.size() > 0){
            int counter = historicalStockQuoteWrappers.size();
            while (sixMonthList.size() < historicalStockQuoteWrappers.size()){
                sixMonthList.add(0, historicalStockQuoteWrappers.get(counter - 1).getDayOfQuote());
                counter -=1;
            }
            sixMonthList.add(stockData.getDay());
        }
        return sixMonthList;

    }

    private BarData getPastThreeMonthsVolume() {
        BarData volumeData = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        if (mTempHistQuoteList.size() > 0) {
            for (int j = 0; j < mTempHistQuoteList.size(); j++) {
                entries.add(new BarEntry(mTempHistQuoteList.get(j).getDayVolume(), j));
            }
            entries.add(new BarEntry(Float.parseFloat(stockData.getAvgVolBarEntries()), 60));
        }
        BarDataSet set = new BarDataSet(entries, "Volume");
        set.setColor(Color.rgb(255, 228, 181));
        set.setDrawValues(false);


        volumeData.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        volumeData.setHighlightEnabled(false);
        return volumeData;
    }

    private CandleData getPastThreeMonthsPrices() {
        List<CandleEntry> dailyCandleData = new LinkedList<>();
        int index = 0;
        while (dailyCandleData.size() < 59) {
            float low = Float.parseFloat(mTempHistQuoteList.get(index).getLowPrice());
            float high = Float.parseFloat(mTempHistQuoteList.get(index).getHighPrice());
            float open = Float.parseFloat(mTempHistQuoteList.get(index).getOpenPrice());
            float close = Float.parseFloat(mTempHistQuoteList.get(index).getClosePrice());

            dailyCandleData.add(new CandleEntry(index, low, high, open, close));
            index++;
        }

        //Set minimum values for price and volume axis
        mPriceAxis = mCombinedThreeMChart.getAxisLeft();
        YAxis volumeAxis = mCombinedThreeMChart.getAxisRight();
        float minPrice = (Float.parseFloat(Collections.min(historicalStockQuoteWrappers).getClosePrice()));
        if (minPrice <= 20) {
            mPriceAxis.setAxisMinValue(0f);
            volumeAxis.setAxisMinValue(0f);
        } else if (minPrice <= 100 && minPrice > 20) {
            mPriceAxis.setAxisMinValue(minPrice - 10);
        } else if (minPrice <= 300 && minPrice > 100) {
            mPriceAxis.setAxisMinValue(minPrice - 40);
        } else {
            mPriceAxis.setAxisMinValue(minPrice - 80);
        }

        //Add last session trading data
        dailyCandleData.add(new CandleEntry(dailyCandleData.size(), Float.parseFloat(stockData.getDayLow()), Float.parseFloat(stockData.getDayHigh()),
                Float.parseFloat(stockData.getDayOpen()), Float.parseFloat(stockData.getDayClose())));
        CandleDataSet threeMonthCandleSet = new CandleDataSet(dailyCandleData, "Price");
        int addOne = Integer.parseInt(mXAxisDays.get(dailyCandleData.size() - 2));
        //mXAxisDays.add(String.valueOf(addOne));


        threeMonthCandleSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set1.setColor(Color.rgb(80, 80, 80));
        threeMonthCandleSet.setShadowColor(Color.DKGRAY);
        threeMonthCandleSet.setShadowColorSameAsCandle(true);
        threeMonthCandleSet.setShadowWidth(0.7f);
        threeMonthCandleSet.setDecreasingColor(ContextCompat.getColor(getContext(), R.color.colorCandleDecreasing));
        threeMonthCandleSet.setDecreasingPaintStyle(Paint.Style.FILL);
        threeMonthCandleSet.setIncreasingColor(ContextCompat.getColor(getContext(), R.color.colorCandleIncreasing));
        threeMonthCandleSet.setIncreasingPaintStyle(Paint.Style.STROKE);
        threeMonthCandleSet.setNeutralColor(Color.BLUE);
        threeMonthCandleSet.setDrawValues(false);


        CandleData candleData = new CandleData(getPastThreeMonths(), threeMonthCandleSet);
        candleData.setHighlightEnabled(true);
        return candleData;
    }

    //TODO: Concenate strings with placeholders from strings.xml
    private void setStockData() {
        mYearLow.setText("52 Week Low: $" + stockData.getYearLow());
        mYearHigh.setText("52 Week High: $" + stockData.getYearHigh());
        mTarget.setText("1Y Target: $" + stockData.getOneYearPriceEstimate());
        mAvgVol.setText("Avg Volume: " + stockData.getAvgVol());
        mEPS.setText("EPS: " + stockData.getEps());
        mDiviYield.setText("Dividend Yield: " + stockData.getDiviYield() + "%");
        mRevenue.setText("Revenue: $" + stockData.getRevenue());
        if (!stockData.getPe().equals(0)) {
            mPE.setText("PE: " + stockData.getPe());
        } else {
            mPE.setText("PE: -");
        }
        mMarketCap.setText("Market Cap: $" + stockData.getMarketCap());
        if (Double.parseDouble(stockData.getDayClose()) > Double.parseDouble(stockData.getDayOpen())) {
            mStockTicker.setText("$" + stockData.getChange() + "    +"
                    + stockData.getPercentChange() + "%");
            mPriceText.setText("$" + stockData.getDayClose());
            mArrowIcon.setImageResource(R.drawable.arrow_up3);
        } else if (Double.parseDouble(stockData.getDayClose()) < Double.parseDouble(stockData.getDayOpen())) {
            mStockTicker.setText("$" + stockData.getChange() + "    "
                    + stockData.getPercentChange() + "%");
            mPriceText.setText("$" + stockData.getDayClose());
            mArrowIcon.setImageResource(R.drawable.arrow_down3);
        } else {

        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.stockdetail_menu, menu);
    }

    //Daily intraday data
    private void setUpDailyChart() {
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mDailyChart.setDescription("");
        // enable touch gestures
        mDailyChart.setTouchEnabled(true);
        // disable scaling and dragging
        mDailyChart.setDragEnabled(true);
        mDailyChart.setScaleEnabled(true);
        mDailyChart.setPinchZoom(true);
        mDailyChart.setMarkerView(new LineCustomMarkerView(getContext(), R.layout.linemarker_layout));

        //add entries to the graph
        ArrayList<Entry> dailyEntries = new ArrayList<>();
        ArrayList<String> timeStampList = new ArrayList<>();
        for (int j = 0; j < intradayStockDataLinkedList.size(); j++) {
            dailyEntries.add(new Entry(Float.parseFloat(intradayStockDataLinkedList.get(j).getClosePrice()), j));
            timeStampList.add(intradayStockDataLinkedList.get(j).getTimestamp());
        }

        //set graph properties
        LineDataSet intradayDataSet = new LineDataSet(dailyEntries, "");
        intradayDataSet.setLineWidth(2.5f);
        intradayDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        intradayDataSet.setColor(ContextCompat.getColor(getContext(),R.color.colorBlueDailyLine));
        intradayDataSet.setCircleColor(ContextCompat.getColor(getContext(),R.color.colorBlueDailyLine));
        intradayDataSet.setDrawValues(false);
        //Add gradient to chart
        Drawable lineChartFill = ContextCompat.getDrawable(getContext(), R.drawable.blue_fade);
        intradayDataSet.setFillDrawable(lineChartFill);
        intradayDataSet.setDrawFilled(true);

        //Add open price line
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(Float.parseFloat(intradayStockDataLinkedList.get(0).getOpenPrice()), "");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);
        ll1.setLabel("Open Price: $" + intradayStockDataLinkedList.get(0).getOpenPrice());

        YAxis leftAxis = mDailyChart.getAxisLeft();
       leftAxis.setEnabled(false);
       // leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        //eftAxis.addLimitLine(ll1);
        //leftAxis.setDrawLimitLinesBehindData(true);

        YAxis rightAxis = mDailyChart.getAxisRight();
        rightAxis.removeAllLimitLines();
        rightAxis.addLimitLine(ll1);
        rightAxis.setDrawLimitLinesBehindData(false);


        XAxis bottomAxis = mDailyChart.getXAxis();
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LineData intradayLinedata = new LineData(timeStampList, intradayDataSet);
        mDailyChart.setData(intradayLinedata);
        mDailyChart.invalidate();
        mDailyChart.animateX(1400);
    }

    //Separate since intraday data is called from onStart()
    private void setVolume() {
        double totalVolume = 0;
        String todaysVolume = "";
        if (intradayStockDataLinkedList.size() > 0) {
            for (IntradayStockData intradayStockData : intradayStockDataLinkedList) {
                totalVolume += Long.parseLong(intradayStockData.getVolume());
            }
            if (totalVolume >= 1000000 && totalVolume < 1000000000) {
                todaysVolume = String.format("%.2fM", totalVolume / 1000000);
            } else if (totalVolume >= 1000000000) {
                todaysVolume = String.format("%.2fB", totalVolume / 1000000000);
            } else {
                todaysVolume = String.valueOf(totalVolume);
            }

            mVol.setText("Volume: " + todaysVolume);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //deregister content observer
        getContext().getContentResolver().unregisterContentObserver(mObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Remove stock if it isn't tracked
        Cursor cursor = getContext().getContentResolver().query(StockContentProvider.CONTENT_URI, null, StockDBHelper.COLUMN_STOCK_SYMBOL +
                " = ?", new String[]{stockData.getSymbol().toUpperCase()}, null);
        int counter = cursor.getCount();
        cursor.moveToPosition(counter - 1);
        if (cursor.getCount() > 0 && cursor.getInt(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_TRACKED)) == 0) {
            getContext().getContentResolver().delete(StockContentProvider.CONTENT_URI, StockDBHelper.COLUMN_ID + " = ? ",
                    new String[]{String.valueOf(stockId)});
            Log.i("STOCKDETAIL", "Deleted stock symbol: " + stockData.getSymbol());
        }
        cursor.close();

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse(StockContentProvider.CONTENT_URI + "/" + String.valueOf(stockId));
            ContentValues portfolioStock = new ContentValues();
            portfolioStock.put(StockDBHelper.COLUMN_STOCK_TRACKED, 1);
            getContext().getContentResolver().update(uri, portfolioStock,
                    StockDBHelper.COLUMN_ID + " = ? ", new String[]{String.valueOf(stockId)});
            PortfolioStock trackedStock = new PortfolioStock(stockData.getSymbol().toUpperCase(), stockData.getDayClose(), stockData.getDayOpen());
            mTrackedListener.onStockTracked(trackedStock);
            Log.i(StockDetailFragment.class.getName(), "Added to tracked stocks: " + stockData.getSymbol().toUpperCase());
            Toast.makeText(getContext(), "Tracked.", Toast.LENGTH_SHORT).show();
            v.setVisibility(View.GONE);
        }
    };

    //Observes for price changes during trading session
    public class StockContentObserver extends ContentObserver {

        public StockContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            //do stuff on UI thread

            Log.d("MARKET DATA", "CHANGE OBSERVED AT URI: " + uri);
            Uri stockUri = Uri.parse(StockContentProvider.CONTENT_URI + "/" + String.valueOf(stockId));
            Cursor cursor = getContext().getContentResolver().query(stockUri, null, StockDBHelper.COLUMN_ID
                    + " = ? ", new String[]{String.valueOf(stockId)}, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String newPrice = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_PRICE));
                String openPrice = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_OPENPRICE));
                //          String openPrice = stockData.getPr
                String volume = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_VOLUME));
                //Check whether new price is above old price
                NumberFormat decimalFormat = NumberFormat.getPercentInstance();
                decimalFormat.setMinimumFractionDigits(2);
                try {
                    if (MainActivity.checkIfTradingTimeRange(1))
                        if (Double.parseDouble(newPrice) > Double.parseDouble(openPrice)) {

                            String instantPriceChange = String.valueOf(Float.parseFloat(newPrice) - Float.parseFloat(openPrice));
                            // String instantPercentageChange = String.valueOf((Float.parseFloat(newPrice) - Float.parseFloat(openPrice))/ Float.parseFloat(stockData.getDayOpen()) * 100);
                            Double instantPercentageChange = (Double.parseDouble(newPrice) - Double.parseDouble(openPrice)) / Double.parseDouble(openPrice);
                            mPriceText.setText(NumberFormat.getCurrencyInstance().format(Double.parseDouble(newPrice)));
                            mStockTicker.setText(DecimalFormat.getCurrencyInstance().format(Double.parseDouble(instantPriceChange))
                                    + "    +" + decimalFormat.format(instantPercentageChange));
                            mArrowIcon.setImageResource(R.drawable.arrow_up3);
                        } else if (Double.parseDouble(newPrice) < Double.parseDouble(openPrice)) {
                            mPriceText.setText("$" + newPrice);
                            String priceChange = String.valueOf(Math.abs(Float.parseFloat(newPrice) - Float.parseFloat(openPrice)));
                            // percentageChange = String.valueOf((Float.parseFloat(newPrice) - Float.parseFloat(openPrice)) / Float.parseFloat(stockData.getDayOpen()) * 100);
                            double percentageChange = (Double.parseDouble(newPrice) - Double.parseDouble(openPrice)) / Double.parseDouble(openPrice);
                            mStockTicker.setText(DecimalFormat.getCurrencyInstance().format(Double.parseDouble(priceChange))
                                    + "    " + decimalFormat.format(percentageChange));
                            mArrowIcon.setImageResource(R.drawable.arrow_down3);
                        } else {
                            mPriceText.setText(newPrice);
                            mStockTicker.setText("N/C");
                        }
                    mVol.setText("Volume: " + volume);
                    mTimeStampText.setText(getCurrentTime());
                    updateLineChart(newPrice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

    }

    private void updateLineChart(String newPriceEntry) {
        LineData data = mDailyChart.getData();
        String newTimeEntry = IntradayStockData.getCurrentTime();
        if (data != null) {

            //Retrieve dataset
            ILineDataSet set = data.getDataSetByIndex(0);

            // add a new x-value first
            data.addXValue(newTimeEntry);

            // choose a random dataSet
            int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());

            data.addEntry(new Entry(Float.parseFloat(newPriceEntry), set.getEntryCount()), randomDataSetIndex);

            // Refreshes data
            mDailyChart.notifyDataSetChanged();


//            // this automatically refreshes the chart (calls invalidate())
            mDailyChart.moveViewTo(data.getXValCount(), 50f, YAxis.AxisDependency.RIGHT);
        }
    }

    //For timestamp
    private String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        return "Timestamp: " + sdf.format(calendar.getTime());
    }
}


