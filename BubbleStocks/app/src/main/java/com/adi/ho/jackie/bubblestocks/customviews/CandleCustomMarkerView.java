package com.adi.ho.jackie.bubblestocks.customviews;

import android.content.Context;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by JHADI on 4/1/16.
 */
public class CandleCustomMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CandleCustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {



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
