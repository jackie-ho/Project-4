package com.adi.ho.jackie.bubblestocks.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adi.ho.jackie.bubblestocks.R;
import com.github.mikephil.charting.charts.LineChart;


public class MarketDataFragment extends Fragment {

    LineChart dowLineChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market_data,container,false);
        dowLineChart = (LineChart)view.findViewById(R.id.dow_linechart);
        return view;
    }
}
