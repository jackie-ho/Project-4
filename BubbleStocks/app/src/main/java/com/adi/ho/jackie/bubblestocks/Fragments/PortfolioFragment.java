package com.adi.ho.jackie.bubblestocks.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adi.ho.jackie.bubblestocks.Database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.Database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.Portfolio;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.PortfolioStock;
import com.adi.ho.jackie.bubblestocks.customviews.PortfolioBubble;


public class PortfolioFragment extends Fragment {

    private RecyclerView mStockPortfolioRecycler;
    private FrameLayout mFrameContainer;
    private Portfolio mPortfolio;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolio,container,false);
        mFrameContainer = (FrameLayout)view.findViewById(R.id.portfolio_container);

        mPortfolio = Portfolio.getInstance();
//        mStockPortfolioRecycler = (RecyclerView)view.findViewById(R.id.stock_portfolio_recyclerview);
//        mStockPortfolioRecycler.setHasFixedSize(true);
//
//        LinearLayoutManager portfolioLayoutManager = new LinearLayoutManager(getContext());
//        mStockPortfolioRecycler.setLayoutManager(portfolioLayoutManager);

        //Find out the tracked stocks
        if (mPortfolio.getPortfolioSize() == 0 ){
            new CreateTrackedStocksAsyncTask().execute();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

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

        private void generateBubbles(PortfolioStock trackedStock){
            PortfolioBubble bubble = new PortfolioBubble(getContext());
            bubble.setmSymbol(trackedStock.getmSymbol());
            bubble.setmPrice(trackedStock.getmPrice());
            mFrameContainer.addView(bubble);

        }
    }
}
