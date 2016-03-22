package com.adi.ho.jackie.bubblestocks.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;

import java.util.LinkedList;

/**
 * Created by JHADI on 3/22/16.
 */
public class StockFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_fragment, container, false);
        CandleStickChart stockCharts = (CandleStickChart) view.findViewById(R.id.stock_fragment_candlestickchart);
        stockCharts.setAutoScaleMinMaxEnabled(true);
        Bundle stockInfo = getArguments();
        if (stockInfo != null){
            DBStock stockData = stockInfo.getParcelable("EXSTOCK");

        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinkedList<CandleEntry> dailyCandleData = new LinkedList<>();
        //new CandleEntry()
        //CandleDataSet candleDataSet = new CandleDataSet()
    }
}
