package com.adi.ho.jackie.bubblestocks.Fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adi.ho.jackie.bubblestocks.Database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.Database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.HistoricalStockQuoteWrapper;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.IntradayStockData;
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
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    private YAxis mPriceAxis;
    private ImageView mArrowIcon;
    public TextView mPriceText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_detailfragment, container, false);

        //References
        mPriceText = (TextView)view.findViewById(R.id.stock_detail_currentpricetext);
        mArrowIcon = (ImageView)view.findViewById(R.id.stock_detail_arrowicon);
        mStockTicker = (TextView)view.findViewById(R.id.stock_detail_ticker);
        mCompanyNameText = (TextView)view.findViewById(R.id.stock_detail_companyname);
        mChart = (CandleStickChart) view.findViewById(R.id.stock_detail_3mcandlestick);
        mFiveMinuteChart = (CandleStickChart) view.findViewById(R.id.stock_detail_5mincandlestick);
        mDailyChart = (LineChart) view.findViewById(R.id.stock_detail_1dlinechart);
        mCombinedThreeMChart = (CombinedChart)view.findViewById(R.id.stock_detail_3mcombinedchart);
        mDiviYield = (TextView)view.findViewById(R.id.stock_detail_diviyieldtext);
        mEPS = (TextView)view.findViewById(R.id.stock_detail_epstext);
        mMarketCap = (TextView)view.findViewById(R.id.stock_Detail_stockmarketcaptext);
        mAvgVol = (TextView)view.findViewById(R.id.stock_detail_averagevolumetext);
        mVol = (TextView)view.findViewById(R.id.stock_detail_volumetext);
        mPE = (TextView)view.findViewById(R.id.stock_detail_petext);
        mRevenue = (TextView)view.findViewById(R.id.stock_detail_stockrevenuetext);
        mTarget = (TextView)view.findViewById(R.id.stock_detail_stockoneyeartarget);
        mYearHigh = (TextView)view.findViewById(R.id.stock_detail_52wkhightext);
        mYearLow = (TextView)view.findViewById(R.id.stock_detail_52wklowtext);

        oneDayGraphButton = (Button) view.findViewById(R.id.oneday_databutton);
        threeMonthGraphButton = (Button) view.findViewById(R.id.threemonth_databutton);
        historicalStockQuoteWrappers = new ArrayList<>();
        mXAxisDays = new ArrayList<>();
        intradayStockDataLinkedList = new LinkedList<>();
        mTempHistQuoteList = new ArrayList<>();

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

        setUpSixMChart();

      return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        oneDayGraphButton.setOnClickListener(oneDayListener);
        threeMonthGraphButton.setOnClickListener(threeMonthListener);
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
                mCompanyNameText.setText("("+stockData.getSymbol().toUpperCase()+") "+companyName+" - ");

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

    private void setUpSixMChart(){
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);//TODO:Change the max

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

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
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.rgb(122, 242, 84));
        set1.setIncreasingPaintStyle(Paint.Style.STROKE);
        set1.setNeutralColor(Color.BLUE);


        CandleData candleData = new CandleData(mXAxisDays, set1);
        mChart.setData(candleData);
    }

    private void setUpCombinedChart(){
        mCombinedThreeMChart.setDescription("");
        mCombinedThreeMChart.setBackgroundColor(Color.WHITE);
        mCombinedThreeMChart.setDrawGridBackground(false);
        mCombinedThreeMChart.setDrawBarShadow(false);

        // order where the charts appear
        mCombinedThreeMChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.SCATTER
        });

        YAxis rightAxis = mCombinedThreeMChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(5, true);
        rightAxis.setSpaceTop(85f); // sets space top to push volume bars below candles

//        YAxis leftAxis = mCombinedThreeMChart.getAxisLeft();
//        leftAxis.setDrawGridLines(false);
        //leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mCombinedThreeMChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        CombinedData data = new CombinedData(getPastThreeMonths());

        //Temporarily store historical data for easier transition to graph data
        int counter = historicalStockQuoteWrappers.size()-1;
        while (mTempHistQuoteList.size() <59){
            mTempHistQuoteList.add(0, historicalStockQuoteWrappers.get(counter));
            counter--;
        }
        data.setData(getPastThreeMonthsVolume());
        data.setData(getPastThreeMonthsPrices());
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
                mFiveMinuteChart.setVisibility(View.GONE);
                mCombinedThreeMChart.setVisibility(View.GONE);

                if (dailyChartFlag == false) {
                  setUpDailyChart();
                } else {
                }
            }
        }
    };


    View.OnClickListener threeMonthListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: animate graph removal and addition with property animation, push it off screen instead of making it come visible
            if (mCombinedThreeMChart.getVisibility() == View.GONE){
                mCombinedThreeMChart.setVisibility(View.VISIBLE);
                mDailyChart.setVisibility(View.GONE);
                mFiveMinuteChart.setVisibility(View.GONE);
                mChart.setVisibility(View.GONE);
                setUpCombinedChart();

            }
            Toast.makeText(getContext(), "Pressed 3M", Toast.LENGTH_SHORT).show();
        }
    };

    private ArrayList<String> getPastThreeMonths(){
        ArrayList<String> threeMonthList = new ArrayList<>();
        if (historicalStockQuoteWrappers.size() > 0){
            int counter = historicalStockQuoteWrappers.size();
            while (threeMonthList.size() <59){
                threeMonthList.add(0,historicalStockQuoteWrappers.get(counter-1).getDayOfQuote());
                counter -=1;
            }
            threeMonthList.add(stockData.getDay());

        }
        return threeMonthList;
    }

    private BarData getPastThreeMonthsVolume(){
        BarData volumeData = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        if (mTempHistQuoteList.size() > 0) {
            for (int j = 0; j<mTempHistQuoteList.size() ; j++){
                entries.add(new BarEntry(mTempHistQuoteList.get(j).getDayVolume(),j));
            }
            entries.add(new BarEntry(Float.parseFloat(stockData.getAvgVolBarEntries()), 60));
        }
        BarDataSet set = new BarDataSet(entries, "Volume");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);

        set.setDrawValues(false);

        volumeData.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        return volumeData;
    }

    private CandleData getPastThreeMonthsPrices(){
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
        if (minPrice <= 20){
            mPriceAxis.setAxisMinValue(0f);
            volumeAxis.setAxisMinValue(0f);
        } else if (minPrice <= 100 && minPrice > 20){
            mPriceAxis.setAxisMinValue(minPrice-10);
        } else if (minPrice <= 300 && minPrice >100){
            mPriceAxis.setAxisMinValue(minPrice-40);
        } else {
            mPriceAxis.setAxisMinValue(minPrice-80);
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
        threeMonthCandleSet.setShadowWidth(0.7f);
        threeMonthCandleSet.setDecreasingColor(Color.RED);
        threeMonthCandleSet.setDecreasingPaintStyle(Paint.Style.FILL);
        threeMonthCandleSet.setIncreasingColor(Color.rgb(122, 242, 84));
        threeMonthCandleSet.setIncreasingPaintStyle(Paint.Style.STROKE);
        threeMonthCandleSet.setNeutralColor(Color.BLUE);
        threeMonthCandleSet.setDrawValues(false);


        CandleData candleData = new CandleData(getPastThreeMonths(), threeMonthCandleSet);
        return candleData;
    }

    //TODO: Concenate strings with placeholders from strings.xml
    private void setStockData(){
        mYearLow.setText("52 Week Low: $"+ stockData.getYearLow());
        mYearHigh.setText("52 Week High: $"+stockData.getYearHigh());
        mTarget.setText("One Year Target Price: $"+ stockData.getOneYearPriceEstimate());
        mAvgVol.setText("Average Volume: "+ stockData.getAvgVol());
        mEPS.setText("EPS: "+stockData.getEps());
            mDiviYield.setText("Dividend Yield: " + stockData.getDiviYield() + "%");
        mRevenue.setText("Revenue: $"+stockData.getRevenue());
        if (!stockData.getPe().equals(0)) {
            mPE.setText("PE: " + stockData.getPe());
        } else {
            mPE.setText("PE: -");
        }
        mMarketCap.setText("Market Cap: $"+stockData.getMarketCap());
        if (Double.parseDouble(stockData.getDayClose()) > Double.parseDouble(stockData.getDayOpen()) ){
            mStockTicker.setText("$"+ stockData.getChange() +" +"
                    + stockData.getPercentChange()+"%" );
            mPriceText.setText("$"+stockData.getDayClose());
            mArrowIcon.setImageResource(R.drawable.arrow_up3);
        } else if (Double.parseDouble(stockData.getDayClose()) < Double.parseDouble(stockData.getDayOpen()) ){
            mStockTicker.setText("$"+stockData.getDayClose()+" -$"+ stockData.getChange() +" -"
                    + stockData.getPercentChange()+"%" );
            mPriceText.setText("$"+stockData.getDayClose());
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
    private void setUpDailyChart(){
        mDailyChart.setDescription("");
        // enable touch gestures
        mDailyChart.setTouchEnabled(true);
        // disable scaling and dragging
        mDailyChart.setDragEnabled(false);
        mDailyChart.setScaleEnabled(true);
        mDailyChart.setPinchZoom(true);

        //add entries to the graph
        ArrayList<Entry> dailyEntries = new ArrayList<>();
        ArrayList<String> timeStampList = new ArrayList<>();
        for (int j = 0; j < intradayStockDataLinkedList.size(); j++) {
            dailyEntries.add(new Entry(Float.parseFloat(intradayStockDataLinkedList.get(j).getClosePrice()), j));
            timeStampList.add(intradayStockDataLinkedList.get(j).getTimestamp());
        }

        //set graph properties
        LineDataSet intradayDataSet = new LineDataSet(dailyEntries, "Daily");
        intradayDataSet.setLineWidth(2.5f);
        intradayDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        intradayDataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        intradayDataSet.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        intradayDataSet.setDrawValues(false);

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
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.setDrawLimitLinesBehindData(true);

        XAxis bottomAxis = mDailyChart.getXAxis();
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LineData intradayLinedata = new LineData(timeStampList, intradayDataSet);
        mDailyChart.setData(intradayLinedata);
        mDailyChart.invalidate();
        mDailyChart.animateX(1400);
    }

    //Separate since intraday data is called from onStart()
    private void setVolume(){
        double totalVolume = 0;
        String todaysVolume="";
        if (intradayStockDataLinkedList.size()> 0) {
            for (IntradayStockData intradayStockData : intradayStockDataLinkedList) {
                totalVolume += Long.parseLong(intradayStockData.getVolume());
            }
            if (totalVolume >=1000000 && totalVolume < 1000000000) {
                todaysVolume = String.format("%.2fM", totalVolume / 1000000);
            } else if (totalVolume >= 1000000000){
                todaysVolume = String.format("%.2fB", totalVolume/ 1000000000);
            } else {
                todaysVolume = String.valueOf(totalVolume);
            }

            mVol.setText("Volume: " + todaysVolume);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Cursor cursor = getContext().getContentResolver().query(StockContentProvider.CONTENT_URI, null, StockDBHelper.COLUMN_STOCK_SYMBOL +
        " = ?",new String[]{stockData.getSymbol().toUpperCase()}, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0 && cursor.getInt(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_TRACKED)) == 0){
            getContext().getContentResolver().delete(StockContentProvider.CONTENT_URI, StockDBHelper.COLUMN_STOCK_SYMBOL + " = ? ",
                    new String[]{stockData.getSymbol().toUpperCase()});
            Log.i("STOCKDETAIL", "Deleted stock symbol: "+stockData.getSymbol());
        }
    }


}
