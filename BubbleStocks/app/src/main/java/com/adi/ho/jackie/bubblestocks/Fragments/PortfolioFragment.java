package com.adi.ho.jackie.bubblestocks.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.adi.ho.jackie.bubblestocks.database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.Portfolio;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.PortfolioStock;
import com.adi.ho.jackie.bubblestocks.customviews.PortfolioBubble;


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

        //Prevent clicking through fragment
        mFrameContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //Portfolio instance
        mPortfolio = Portfolio.getInstance();

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
                    //make bubbles
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

    public void addBubbleToPortfolio(PortfolioStock newStock){
        mPortfolio.initialAddToPortfolio(newStock);
        PortfolioBubble newBubble = new PortfolioBubble(getContext(), getActivity().getSupportFragmentManager());
        newBubble.setmSymbol(newStock.getmSymbol());
        newBubble.setmPrice(newStock.getmPrice(), newStock.getmOpenPrice());
        mFrameContainer.addView(newBubble);

    }

}
