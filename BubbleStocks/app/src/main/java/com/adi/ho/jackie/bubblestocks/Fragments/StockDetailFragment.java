package com.adi.ho.jackie.bubblestocks.Fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.HistoricalStockQuoteWrapper;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JHADI on 3/23/16.
 */
public class StockDetailFragment extends Fragment {
    CandleStickChart mChart;
    ArrayList<HistoricalStockQuoteWrapper> historicalStockQuoteWrappers;
    ArrayList<String> mXAxisDays;
    DBStock stockData;
    CandleDataSet set1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_detailfragment, container, false);
        mChart = (CandleStickChart) view.findViewById(R.id.stock_detail_candlestick);
        historicalStockQuoteWrappers = new ArrayList<>();
        mXAxisDays = new ArrayList<>();
        mChart.setAutoScaleMinMaxEnabled(true);
        Bundle stockInfo = getArguments();
        if (stockInfo != null) {
            stockData = stockInfo.getParcelable("EXSTOCK");

            historicalStockQuoteWrappers = stockInfo.getParcelableArrayList("HISTORICALQUOTE");

        }
        for (int i = 0; i < historicalStockQuoteWrappers.size(); i++) {
            mXAxisDays.add(String.valueOf(i));
        }
        mChart.setBackgroundColor(Color.WHITE);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

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
//        rightAxis.setStartAtZero(false);

        // setting data
//        mSeekBarX.setProgress(40);
//        mSeekBarY.setProgress(100);
//
//        mChart.getLegend().setEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        if (!stockData.getDayOpen().equals(String.valueOf(dailyCandleData.get(dailyCandleData.size() - 1).getOpen()))) {
            dailyCandleData.add(new CandleEntry(dailyCandleData.size(), Float.parseFloat(stockData.getDayLow()), Float.parseFloat(stockData.getDayHigh()),
                    Float.parseFloat(stockData.getDayOpen()), Float.parseFloat(stockData.getDayClose())));
            set1 = new CandleDataSet(dailyCandleData, "Price");
            int addOne = Integer.parseInt(mXAxisDays.get(dailyCandleData.size() - 2));
            mXAxisDays.add(String.valueOf(addOne));
        }

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

}
