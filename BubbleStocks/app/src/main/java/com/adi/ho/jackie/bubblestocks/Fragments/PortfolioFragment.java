package com.adi.ho.jackie.bubblestocks.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adi.ho.jackie.bubblestocks.R;


public class PortfolioFragment extends Fragment {

    private RecyclerView mStockPortfolioRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolio,container,false);
        mStockPortfolioRecycler = (RecyclerView)view.findViewById(R.id.stock_portfolio_recyclerview);
        mStockPortfolioRecycler.setHasFixedSize(true);

        LinearLayoutManager portfolioLayoutManager = new LinearLayoutManager(getContext());
        mStockPortfolioRecycler.setLayoutManager(portfolioLayoutManager);

        return view;
    }
}
