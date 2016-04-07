package com.adi.ho.jackie.bubblestocks.customviews;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adi.ho.jackie.bubblestocks.database.StockContentProvider;
import com.adi.ho.jackie.bubblestocks.database.StockDBHelper;
import com.adi.ho.jackie.bubblestocks.fragments.StockDetailFragment;
import com.adi.ho.jackie.bubblestocks.httpconnections.IntradayMarketDataRequest;
import com.adi.ho.jackie.bubblestocks.activities.MainActivity;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.stockportfolio.DBStock;
import com.adi.ho.jackie.bubblestocks.stockportfolio.HistoricalStockQuoteWrapper;
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
    private ArrayList<Integer> colors;

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
        addColorsToArray();
        //Strings get set before it finishes inflating
        mSymbol.setTextSize(15f);
        Random randColor = new Random();
        setBackground(dynamicGenerateBubbleShape(colors.get(randColor.nextInt(colors.size()))));

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

    //Update price with appropriate format
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
            mPrice.setText(price + "\n -" + change);
        }
    }


    //same method as in activity
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
    private static final int MAX_CLICK_DURATION = 180;

    //Motion events, touch listener
    @Override
    public boolean onTouch(View v, MotionEvent event) {
         switch (event.getAction()) {

             //on user press down
            case MotionEvent.ACTION_DOWN:

                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            // on user lift finger up
            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION){
                    Thread stockSearchThread = new Thread(searchStockRunnable);
                    stockSearchThread.start();
                } break;

            // on drag motion
            case MotionEvent.ACTION_MOVE:

                    v.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
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
    private GradientDrawable dynamicGenerateBubbleShape( int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setGradientRadius(150f);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        drawable.setDither(true);
        drawable.setStroke((int) 2, Color.parseColor("#EEEEEE"));
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r.getDisplayMetrics());
        drawable.setSize((int)px, (int)px);

        drawable.setAlpha(200);
        return drawable;
    }

    private void addColorsToArray(){
        colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
    }

    public void updatePrice(){
        new UpdatePriceAsyncTask().execute(symbol);

    }

    //On update animation
    private void bubbleShake(){

        ObjectAnimator bounceUpAnimation = ViewPropertyObjectAnimator.animate(this).yBy(-20f).setDuration(300).get();
        ObjectAnimator bounceDownAnimation = ViewPropertyObjectAnimator.animate(this).yBy(20f).setDuration(300).get();
        ObjectAnimator bounceUpAnimation2 = ViewPropertyObjectAnimator.animate(this).yBy(-20f).setDuration(300).get();
        ObjectAnimator bounceDownAnimation2 = ViewPropertyObjectAnimator.animate(this).yBy(20f).setDuration(300).get();
        List<Animator> animatorList = new ArrayList<>();
        animatorList.add(bounceUpAnimation);
        animatorList.add(bounceDownAnimation);
        animatorList.add(bounceUpAnimation2);
        animatorList.add(bounceDownAnimation2);
        AnimatorSet bubbleShakeSet = new AnimatorSet();
        bubbleShakeSet.playSequentially(animatorList);
        bubbleShakeSet.setInterpolator(new LinearInterpolator());
        bubbleShakeSet.start();

    }

    private class UpdatePriceAsyncTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {

            String[] columns = new String[]{StockDBHelper.COLUMN_STOCK_SYMBOL, StockDBHelper.COLUMN_STOCK_PRICE};
            String[] updatedPrice = new String[2];
            String stockSymbol = params[0];


            Cursor cursor = context.getContentResolver().query(StockContentProvider.CONTENT_URI,columns ,
                    null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                if (cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_SYMBOL)).equalsIgnoreCase(stockSymbol)){
                    updatedPrice[0] = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_PRICE));
                    updatedPrice[1] = cursor.getString(cursor.getColumnIndex(StockDBHelper.COLUMN_STOCK_OPENPRICE));
                    break;
                }
                cursor.moveToNext();
            }

            cursor.close();
            return updatedPrice;
        }

        @Override
        protected void onPostExecute(String[] s) {
            setmPrice(s[0], s[1]);
            bubbleShake();
        }
    }
}
