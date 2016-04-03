package com.adi.ho.jackie.bubblestocks.listitems;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.data.ChartData;

/**
 * Created by JHADI on 3/25/16.
 */
public abstract class ChartItem {

    protected static final int TYPE_LINECHART = 1;

    protected ChartData<?> mChartData;

    public ChartItem(ChartData<?> cd) {
        this.mChartData = cd;
    }

    public abstract int getItemType();

    public abstract View getView(int position, View convertView, Context c);
}
