package com.adi.ho.jackie.bubblestocks.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.MarketData;
import com.adi.ho.jackie.bubblestocks.listitems.ChartItem;
import com.adi.ho.jackie.bubblestocks.listitems.LineChartItem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class MarketDataFragment extends Fragment {

    private ArrayList<MarketData> marketDataArrayList;
    private ArrayList<Float> marketPriceArrayList;
    private  ArrayList<ChartItem> mChartListItem;
    private ListView mMarketDataListView;
    private LineChart mSPYChart;
    private String mDowIndexAvgJson;
    private String dowName;
    private ArrayList<String> mThreeMonthsDates;
    private LinkedList<Double> mDowMarketPriceArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market_data, container, false);
        mMarketDataListView = (ListView)view.findViewById(R.id.marketListView);
        mChartListItem = new ArrayList<>();
        Utils.init(getContext());

        //Get Arguments
        marketDataArrayList = new ArrayList<>();
        marketPriceArrayList = new ArrayList<>();
        mDowMarketPriceArray = new LinkedList<>();
        mThreeMonthsDates = new ArrayList<>();
        Bundle marketDataBundleArguments = getArguments();
        marketDataArrayList = marketDataBundleArguments.getParcelableArrayList("MARKETDATA");
        mDowIndexAvgJson = marketDataBundleArguments.getString("DOW");
        if (! marketDataArrayList.isEmpty() && mDowIndexAvgJson != null){
            for (int i = 0 ; i <marketDataArrayList.size();i++){
                marketPriceArrayList.add(0,Float.parseFloat(marketDataArrayList.get(i).getPrice()));
                Log.i("MARKETDATA", "Price of SPy: "+marketDataArrayList.get(i).getPrice());
            }
            try {
                JSONObject dowJsonObject = new JSONObject(mDowIndexAvgJson);
                JSONObject dowDatasetJsonObject = dowJsonObject.getJSONObject("dataset");
                dowName = dowDatasetJsonObject.getString("name");
                JSONArray dowDataJson = dowDatasetJsonObject.getJSONArray("data");
                for (int i = 0; i < 100;i++){
                    JSONArray dowDataWithDataAndPrice = (JSONArray) dowDataJson.get(i);
                    mDowMarketPriceArray.add((Double)dowDataWithDataAndPrice.get(1));
                    mThreeMonthsDates.add(0,(String)dowDataWithDataAndPrice.get(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("MARKETDATA", "Size of price array list: "+ marketPriceArrayList.size());
            Log.i("MARKETDATA", "Size of date array list: "+ mThreeMonthsDates.size());
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Float> floatPricesDow = new ArrayList<>();
        //Convert ArrayList ofDouble to Float
        for (Double price: mDowMarketPriceArray) {
            floatPricesDow.add(0,price.floatValue());
        }
        floatPricesDow.size();
        mThreeMonthsDates.size();

        mChartListItem.add(new LineChartItem(generateDataLine(marketPriceArrayList), getActivity()));
        mChartListItem.add(new LineChartItem(generateDataLine(floatPricesDow),getActivity()));
        mMarketDataListView.setAdapter(new ChartDataAdapter(getActivity(),mChartListItem));

    }
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 2; // we have 3 different item-types
        }
    }

    private LineData generateDataLine(ArrayList<Float> marketPriceArrayList) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();
        for (int i = 0; i < marketPriceArrayList.size(); i++) {
            e1.add(new Entry(marketPriceArrayList.get(i),i));
        }

        LineDataSet d1 = new LineDataSet(e1, "SPY");
        d1.setLineWidth(2.5f);
    //    d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

//        ArrayList<Entry> e2 = new ArrayList<Entry>();
//
//        for (int i = 0; i < 12; i++) {
//            e2.add(new Entry(e1.get(i).getVal() - 30, i));
//        }
//
//        LineDataSet d2 = new LineDataSet(e2, "New DataSet " + cnt + ", (2)");
//        d2.setLineWidth(2.5f);
//        d2.setCircleRadius(4.5f);
//        d2.setHighLightColor(Color.rgb(244, 117, 117));
//        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        //sets.add(d2);


        LineData cd = new LineData(getMonths(marketPriceArrayList), sets);
        return cd;
    }

    private ArrayList<String> getMonths(ArrayList<Float> xValuesSize) {
        ArrayList<String> months = new ArrayList<>();
        if (xValuesSize.size() == marketPriceArrayList.size()){
            for (int j = 0 ; j<marketPriceArrayList.size();j++){
                months.add(String.valueOf(j));
            }
        } else if (xValuesSize.size() == mDowMarketPriceArray.size()){
            return mThreeMonthsDates;
        }
        return months;
    }
}
