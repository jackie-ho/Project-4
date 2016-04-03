package com.adi.ho.jackie.bubblestocks.customviews;

import android.content.Context;
import android.widget.TextView;

import com.adi.ho.jackie.bubblestocks.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by JHADI on 4/1/16.
 */
public class MarketCustomMarkerView extends MarkerView {


    private TextView mPrice;
    public MarketCustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        mPrice = (TextView)findViewById(R.id.market_markerprice);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        mPrice.setText(String.valueOf(e.getVal()));
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
