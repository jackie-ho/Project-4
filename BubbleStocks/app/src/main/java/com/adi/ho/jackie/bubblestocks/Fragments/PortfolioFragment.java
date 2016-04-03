package com.adi.ho.jackie.bubblestocks.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.adi.ho.jackie.bubblestocks.Database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.Database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.HttpConnections.IntradayMarketDataRequest;
import com.adi.ho.jackie.bubblestocks.MainActivity;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.HistoricalStockQuoteWrapper;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.Portfolio;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.PortfolioStock;
import com.adi.ho.jackie.bubblestocks.customviews.PortfolioBubble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;


public class PortfolioFragment extends Fragment {

    private RecyclerView mStockPortfolioRecycler;
    private RelativeLayout mFrameContainer;
    private Portfolio mPortfolio;
    private int width;
    private int height;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolio,container,false);
        mFrameContainer = (RelativeLayout)view.findViewById(R.id.portfolio_container);
        mFrameContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mPortfolio = Portfolio.getInstance();
//        mStockPortfolioRecycler = (RecyclerView)view.findViewById(R.id.stock_portfolio_recyclerview);
//        mStockPortfolioRecycler.setHasFixedSize(true);
//
//        LinearLayoutManager portfolioLayoutManager = new LinearLayoutManager(getContext());
//        mStockPortfolioRecycler.setLayoutManager(portfolioLayoutManager);

        //Find out the tracked stocks
        if (mPortfolio.getPortfolioSize() == 0 ){
            new CreateTrackedStocksAsyncTask().execute();
        } else {
            for (PortfolioStock portfolioStock : mPortfolio.getMyStockPortfolio()){
                generateBubbles(portfolioStock);
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Set bounds for dragging
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

    }

    private class CreateTrackedStocksAsyncTask extends AsyncTask<Void,Void,Cursor>{

        @Override
        protected Cursor doInBackground(Void... params) {
            //get tracked stocks
            return getContext().getContentResolver().query(StockContentProvider.CONTENT_URI, StockDBHelper.ALL_COLUMNS,
                    StockDBHelper.COLUMN_ID + " = ?", new String[]{"1"},null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            String symbol = "";
            String price = "";
            String openPrice = "";
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    symbol = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_SYMBOL));
                    price = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_PRICE));
                    openPrice = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_OPENPRICE));

                    mPortfolio.initialAddToPortfolio(new PortfolioStock(symbol, price, openPrice));
                    cursor.moveToNext();
                }
                for (PortfolioStock trackedStock : mPortfolio.getMyStockPortfolio()){

                    //generate bubbles here
                    generateBubbles(trackedStock);
                }

            }

        }


    }
    private void generateBubbles(PortfolioStock trackedStock){
        PortfolioBubble bubble = new PortfolioBubble(getContext(),getActivity().getSupportFragmentManager());
        bubble.setmSymbol(trackedStock.getmSymbol());
        bubble.setmPrice(trackedStock.getmPrice(), trackedStock.getmOpenPrice());
        // bubble.setOnTouchListener(dragBubbleListener);
        mFrameContainer.addView(bubble);

    }

}
