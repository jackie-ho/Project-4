package com.adi.ho.jackie.bubblestocks.customviews;

import android.content.Context;
import android.widget.TextView;

import com.adi.ho.jackie.bubblestocks.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by JHADI on 4/1/16.
 */
public class CandleCustomMarkerView extends MarkerView {

    private TextView mDate;
    private TextView mHigh;
    private TextView mLow;
    private TextView mOpen;
    private TextView mClose;

    public CandleCustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        mDate = (TextView)findViewById(R.id.marker_date);
        mHigh = (TextView)findViewById(R.id.marker_highprice);
        mLow = (TextView)findViewById(R.id.marker_lowprice);
        mOpen = (TextView)findViewById(R.id.marker_openprice);
        mClose = (TextView)findViewById(R.id.marker_closeprice);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        CandleEntry candleEntry = (CandleEntry)e;
        String highPrice = "High: " + candleEntry.getHigh();
        String lowPrice = "Low: " + candleEntry.getLow();
        String openPrice = "Open: " + candleEntry.getOpen();
        String closePrice = "Close: " + candleEntry.getClose();
        mHigh.setText(highPrice);
        mLow.setText(lowPrice);
        mOpen.setText(openPrice);
        mClose.setText(closePrice);


    }

    @Override
    public int getXOffset(float xpos) {
        return 0;
    }

    @Override
    public int getYOffset(float ypos) {
        return 0;
    }
}
