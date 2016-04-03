package com.adi.ho.jackie.bubblestocks.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adi.ho.jackie.bubblestocks.Fragments.StockDetailFragment;
import com.adi.ho.jackie.bubblestocks.HttpConnections.IntradayMarketDataRequest;
import com.adi.ho.jackie.bubblestocks.MainActivity;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.HistoricalStockQuoteWrapper;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.Portfolio;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

/**
 * Created by JHADI on 4/3/16.
 */
public class PortfolioBubble extends LinearLayout implements  View.OnTouchListener {
    private TextView mSymbol;
    private TextView mPrice;
    private String symbol;
    private String price;
    private int height;
    private int width;
    private FragmentManager fragmentManager;


    //Compound view bubble
    Context context;

    public PortfolioBubble(Context context, FragmentManager fragmentManager) {
        super(context);
        this.fragmentManager = fragmentManager;
        this.context = context;
        inflateViews();
        setOnTouchListener(this);
    }

    public PortfolioBubble(Context context, AttributeSet attrs, FragmentManager fragmentManager) {
        super(context, attrs);
        this.fragmentManager = fragmentManager;
        this.context = context;
        inflateViews();
        setOnTouchListener(this);

    }

    public PortfolioBubble(Context context, AttributeSet attrs, int defStyleAttr, FragmentManager fragmentManager) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.fragmentManager = fragmentManager;
        inflateViews();
        setOnTouchListener(this);


    }

    private void inflateViews() {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate(getContext(), R.layout.bubble_portfolio_view, this);

        //Get width and length of screen
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);


        width = metrics.widthPixels;
        height = metrics.heightPixels;

        int leftMargin = (int) (Math.random() * (width / 2)) + 50;
        int topMargin = (int) (Math.random() * (height / 2)) + 150;

        mSymbol = (TextView) this.findViewById(R.id.stock_symboltext);
        mPrice = (TextView) this.findViewById(R.id.stock_percentagechange);

        //Strings get set before it finishes inflating
        mSymbol.setText("hihi");
        mPrice.setText("baby");
        mSymbol.setTextSize(15f);
        setBackgroundResource(R.drawable.bubble1);

        setOrientation(LinearLayout.VERTICAL);
        setAlpha(0.8f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(leftMargin, topMargin, 0, 0);
        setLayoutParams(params);

        setPadding(10, 10, 10, 10);


//        inflater.inflate(R.layout.bubble_portfolio_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    public void setmSymbol(String symbol) {
        this.symbol = symbol;
        mSymbol.setText(symbol);
    }

    public void setmPrice(String price, String openPrice) {
        this.price = price;
        float currentPrice = Float.parseFloat(price);
        float startPrice = Float.parseFloat(openPrice);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        GradientDrawable drawable = (GradientDrawable) this.getBackground();
        String change = String.valueOf(df.format(Math.abs(currentPrice - startPrice)));
        if (currentPrice > startPrice) {
            drawable.setStroke(2, Color.GREEN);
            mPrice.setText(price + "\n+" + change);
        } else if (currentPrice == startPrice) {
            drawable.setStroke(2, Color.BLACK);
            mPrice.setText(price + "\n -");
        } else {
            drawable.setStroke(2, Color.RED);
            mPrice.setText(price + "\n-" + change);
        }
    }


    public void selectedStock(Stock searchedStock) {
        //Get historical data for the searched stock.
        ArrayList<HistoricalStockQuoteWrapper> historicalQuoteList = new ArrayList<>();
        DBStock parcelingStock = new DBStock();
        Calendar lastSixMonths = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        String intradayData = "";
        lastSixMonths.add(Calendar.MONTH, -6);
        try {
            parcelingStock = MainActivity.setStockInfo(searchedStock);
            //get intraday data
            intradayData = new IntradayMarketDataRequest().run(searchedStock.getSymbol());
            //historical data includes open,close, high, and low prices as well as volume
            for (HistoricalQuote historicalQuote : searchedStock.getHistory(lastSixMonths, yesterday, Interval.DAILY)) {
                historicalQuoteList.add(0, new HistoricalStockQuoteWrapper(historicalQuote));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (parcelingStock != null && parcelingStock.getSymbol() != null && historicalQuoteList.size() > 0) {
                StockDetailFragment stockDetailFragment = new StockDetailFragment();
                FragmentTransaction stockTransaction = fragmentManager.beginTransaction();

                Bundle stockBundle = new Bundle();
                stockBundle.putParcelable("EXSTOCK", parcelingStock);
                stockBundle.putParcelableArrayList("HISTORICALQUOTE", historicalQuoteList);
                stockBundle.putString("INTRADAY", intradayData);
                stockDetailFragment.setArguments(stockBundle);
                stockTransaction.replace(R.id.stock_fragmentcontainer, stockDetailFragment).addToBackStack(null).commit();
            }
        }
    }

    float dX, dY;
    long startClickTime;
    private static final int MAX_CLICK_DURATION = 200;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
         switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION){
                    Thread stockSearchThread = new Thread(searchStockRunnable);
                    stockSearchThread.start();
                } break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(dX) >= width-25 || Math.abs(dX) <= 25) {
                    v.animate()
                            .y(event.getRawY() + dY)
                            .setDuration(0).start();
                } else if (Math.abs(dY) >= height-25 || Math.abs(dY) <= 25){
                    v.animate()
                            .x(event.getRawX() + dX)
                            .setDuration(0).start();
                } else {

                    v.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
                }
                break;

            default:
                return false;
        }
        return true;
    }
    Runnable searchStockRunnable = new Runnable() {
        @Override
        public void run() {
            Stock stockSearch = null;
            try {
                stockSearch = YahooFinance.get(symbol);
                if (stockSearch != null) {
                    selectedStock(stockSearch);
                    Log.i("STOCKSEARCH", "Searched for " + symbol);
                } else {
                    Log.e(PortfolioBubble.class.getName(), "Error getting symbol");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
