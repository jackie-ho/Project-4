package com.adi.ho.jackie.bubblestocks.customviews;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adi.ho.jackie.bubblestocks.R;

/**
 * Created by JHADI on 4/3/16.
 */
public class PortfolioBubble extends LinearLayout {
    private TextView mSymbol;
    private TextView mPrice;

    //Compound view bubble
    Context context;
    public PortfolioBubble(Context context) {
        super(context);
        this.context = context;
        inflateViews();
        setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setPadding(10,10,10,10);

    }

    public PortfolioBubble(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateViews();

    }

    public PortfolioBubble(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflateViews();
    }

    private void inflateViews(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bubble_portfolio_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSymbol = (TextView)this.findViewById(R.id.stock_symboltext);
        mPrice = (TextView)this.findViewById(R.id.stock_percentagechange);
    }

    public void setmSymbol(String symbol){
        mSymbol.setText(symbol);
    }
    public void setmPrice(String price){
        mPrice.setText(price);
    }
}
