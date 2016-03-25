package com.adi.ho.jackie.bubblestocks.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.RecyclerviewItems.StockRecyclerAdapter;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Created by JHADI on 3/22/16.
 */
public class StockFragment extends Fragment implements SearchView.OnQueryTextListener {

    SelectStock selectStockListener;
    StockRecyclerAdapter mAdapter;
    List<DBStock> myPortfolioStockList;
    private MaterialSearchView mStockSearchView;

    public static interface SelectStock{
        public void selectedStock(Stock searchedStock);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            selectStockListener = (SelectStock)activity;
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_fragment, container, false);
        RecyclerView stockRecycler = (RecyclerView)view.findViewById(R.id.stock_recyclerviewlist);
        stockRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        stockRecycler.setLayoutManager(linearLayoutManager);
        myPortfolioStockList = new ArrayList<>();

        mAdapter = new StockRecyclerAdapter(getContext(), myPortfolioStockList);
        stockRecycler.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
       // mStockSearchView = (SearchView) MenuItemCompat.getActionView(item);

        mStockSearchView.setMenuItem(item);

       // mStockSearchView.setIconified(true); //prevents soft keyboard from showing everytime the fragment is changed.
       // mStockSearchView.setOnQueryTextListener(this);
        mStockSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final String stockSymbol = query;
                Runnable searchStockRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Stock stockSearch = null;
                        try {
                            stockSearch = YahooFinance.get(stockSymbol);
                            selectStockListener.selectedStock(stockSearch);
                            Log.i("STOCKSEARCH", "Searched for "+stockSymbol);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Invalid Stock Symbol", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                Thread stockSearchThread = new Thread(searchStockRunnable);
                stockSearchThread.start();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        final String stockSymbol = query;
        Runnable searchStockRunnable = new Runnable() {
            @Override
            public void run() {
                Stock stockSearch = null;
                try {
                    stockSearch = YahooFinance.get(stockSymbol);
                    selectStockListener.selectedStock(stockSearch);
                    Log.i("STOCKSEARCH", "Searched for "+stockSymbol);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Invalid Stock Symbol", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Thread stockSearchThread = new Thread(searchStockRunnable);
        stockSearchThread.start();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText.length();
        return false;
    }
}
