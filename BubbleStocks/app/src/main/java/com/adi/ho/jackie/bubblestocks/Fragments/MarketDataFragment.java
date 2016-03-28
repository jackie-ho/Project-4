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
    private ArrayList<ChartItem> mChartListItem;
    private ListView mMarketDataListView;
    private LineChart mSPYChart;
    private String mDowIndexAvgJson;
    private String mNyseIndexAvgJson;
    private String mNasdaqIndexAvgJson;
    private String mSPIndexAvgJson;
    private String dowName;
    private ArrayList<String> mThreeMonthsDates;
    private LinkedList<Double> mDowMarketPriceArray;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market_data, container, false);
        mMarketDataListView = (ListView) view.findViewById(R.id.marketListView);
        mChartListItem = new ArrayList<>();
        Utils.init(getContext());

        //Get Arguments
        marketDataArrayList = new ArrayList<>();
        marketPriceArrayList = new ArrayList<>();
        mDowMarketPriceArray = new LinkedList<>();
        mThreeMonthsDates = new ArrayList<>();
        Bundle marketDataBundleArguments = getArguments();
        //marketDataArrayList = marketDataBundleArguments.getParcelableArrayList("MARKETDATA");
        mDowIndexAvgJson = marketDataBundleArguments.getString("DOW");
        mSPIndexAvgJson = marketDataBundleArguments.getString("SP");
        mNasdaqIndexAvgJson = marketDataBundleArguments.getString("NASDAQ");
        mNyseIndexAvgJson = marketDataBundleArguments.getString("NYSE");
        if (mSPIndexAvgJson!= null && mDowIndexAvgJson != null) {

            try {
                mChartListItem.add(new LineChartItem(generateDataLine(convertJsonToArrayList(mDowIndexAvgJson)), getActivity()));
                mChartListItem.add(new LineChartItem(generateDataLine(convertJsonToArrayList(mSPIndexAvgJson)), getActivity()));
                mChartListItem.add(new LineChartItem(generateDataLine(convertJsonToArrayList(mNasdaqIndexAvgJson)), getActivity()));
                mChartListItem.add(new LineChartItem(generateDataLine(convertJsonToArrayList(mNyseIndexAvgJson)), getActivity()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMarketDataListView.setAdapter(new ChartDataAdapter(getActivity(), mChartListItem));
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
            return 4; // we have 3 different item-types
        }
    }

    private LineData generateDataLine(ArrayList<Float> marketPriceArrayList) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();
        for (int i = 0; i < marketPriceArrayList.size(); i++) {
            e1.add(new Entry(marketPriceArrayList.get(i), i));
        }

        LineDataSet d1 = new LineDataSet(e1, "SPY");
        d1.setLineWidth(2.5f);
        //    d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);

        LineData cd = new LineData(getMonths(), sets);
        return cd;
    }

    private ArrayList<String> getMonths() {
       return mThreeMonthsDates;
    }

    private ArrayList<Float> convertJsonToArrayList(String json) throws JSONException {
        LinkedList<Double> priceArray = new LinkedList<>();
        ArrayList<Float> floatPriceArray = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONObject datasetJsonObject = jsonObject.getJSONObject("dataset");
        String indexName = datasetJsonObject.getString("name").toLowerCase();
        if (indexName.contains("dow")) {
            JSONArray dowDataJson = datasetJsonObject.getJSONArray("data");
            for (int i = 0; i < 100; i++) {
                JSONArray dowDataWithDataAndPrice = (JSONArray) dowDataJson.get(i);
                priceArray.add((Double) dowDataWithDataAndPrice.get(1));
                mThreeMonthsDates.add(0, (String) dowDataWithDataAndPrice.get(0));
            }
            for (Double price : priceArray) {
                floatPriceArray.add(0,price.floatValue());
            }
            return floatPriceArray;
        } else if (indexName.contains("nyse")) {
            JSONArray nyseDataJson = datasetJsonObject.getJSONArray("data");
            for (int i = 0; i < 100; i++) {
                JSONArray nyseDataWithDataAndPrice = (JSONArray) nyseDataJson.get(i);
                priceArray.add((Double) nyseDataWithDataAndPrice.get(1));
            }
            for (Double price : priceArray) {
                floatPriceArray.add(0,price.floatValue());
            }
            return floatPriceArray;
        } else if (indexName.contains("s\u0026p")) {
            JSONArray spDataJson = datasetJsonObject.getJSONArray("data");
            for (int i = 0; i < 100; i++) {
                JSONArray spDataWithDataAndPrice = (JSONArray) spDataJson.get(i);
                priceArray.add((Double) spDataWithDataAndPrice.get(1));
            }
            for (Double price : priceArray) {
                floatPriceArray.add(0,price.floatValue());
            }
            return floatPriceArray;
        } else if (indexName.contains("nasdaq")) {
            JSONArray nasdaqDataJson = datasetJsonObject.getJSONArray("data");
            for (int i = 0; i < 100; i++) {
                JSONArray nasdaqDataWithDataAndPrice = (JSONArray) nasdaqDataJson.get(i);
                priceArray.add((Double) nasdaqDataWithDataAndPrice.get(1));

            }
            for (Double price : priceArray) {
                floatPriceArray.add(0,price.floatValue());
            }
            return floatPriceArray;
        }
        return null;
    }
}