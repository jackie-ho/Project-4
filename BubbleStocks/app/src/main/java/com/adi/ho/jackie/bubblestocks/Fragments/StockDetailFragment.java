package com.adi.ho.jackie.bubblestocks.Fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private YAxis mPriceAxis;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_detailfragment, container, false);

        //References
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

        if (intradayJsonData.length() > 0 && intradayJsonData.contains("finance_charts_json_callback")) {
            intradayJsonData = intradayJsonData.substring(30);
            int jsonDataLength = intradayJsonData.length();
            intradayJsonData = intradayJsonData.substring(0, jsonDataLength - 2);

            try {
                JSONObject intradayInitialObject = new JSONObject(intradayJsonData);
                JSONObject metaIntradayObject = intradayInitialObject.getJSONObject("meta");
                companyName = metaIntradayObject.optString("Company-Name", "");
                JSONArray intradayPricesArray = intradayInitialObject.getJSONArray("series");
                for (int i = 0; i < intradayPricesArray.length(); i++) {
                    intradayStockDataLinkedList.add(new IntradayStockData(intradayPricesArray.getJSONObject(i)));
                }
                Log.v("INTRADAY", "size of list: " + intradayStockDataLinkedList.size());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            fillCandleCharts();

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

        data.setData(getPastThreeMonthsVolume());
        data.setData(getPastThreeMonthsPrices());
        mCombinedThreeMChart.setData(data);
        mCombinedThreeMChart.invalidate();
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
                    ll1.setLabel("Previous close: $" + intradayStockDataLinkedList.get(0).getOpenPrice());

                    YAxis leftAxis = mDailyChart.getAxisLeft();
                    leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                    leftAxis.addLimitLine(ll1);
                    leftAxis.setDrawLimitLinesBehindData(true);

                    LineData intradayLinedata = new LineData(timeStampList, intradayDataSet);
                    mDailyChart.setData(intradayLinedata);
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

        if (historicalStockQuoteWrappers.size() > 0) {
            int counter = historicalStockQuoteWrappers.size();
            int index = 0;
            while (entries.size() < 59) {
                //Don't add to index 0 or bars will disappear upon pinch zoom
                entries.add(new BarEntry(historicalStockQuoteWrappers.get(counter - 1).getDayVolume(), index));
                counter -= 1;
                index++;
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
        int counter = historicalStockQuoteWrappers.size()-1;
        while (dailyCandleData.size() < 59) {
            float low = Float.parseFloat(historicalStockQuoteWrappers.get(counter).getLowPrice());
            float high = Float.parseFloat(historicalStockQuoteWrappers.get(counter).getHighPrice());
            float open = Float.parseFloat(historicalStockQuoteWrappers.get(counter).getOpenPrice());
            float close = Float.parseFloat(historicalStockQuoteWrappers.get(counter).getClosePrice());

            dailyCandleData.add(new CandleEntry(index, low, high, open, close));
            index++;
            counter--;
        }
        mPriceAxis = mCombinedThreeMChart.getAxisLeft();
        float minPrice = (Float.parseFloat(Collections.min(historicalStockQuoteWrappers).getClosePrice()));
        if (minPrice <= 20){
            mPriceAxis.setAxisMinValue(0f);

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
    }
}
