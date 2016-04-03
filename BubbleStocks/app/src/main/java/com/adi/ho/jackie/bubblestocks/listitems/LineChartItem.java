package com.adi.ho.jackie.bubblestocks.listitems;

/**
 * Created by JHADI on 3/25/16.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.customviews.MarketCustomMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


public class LineChartItem extends ChartItem implements OnChartValueSelectedListener {

    private Typeface mTf;
    private LineChart mLineChart;
    private Context context;

    public LineChartItem(ChartData<?> cd, Context c) {
        super(cd);

        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public int getItemType() {
        return TYPE_LINECHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder = null;


        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_linechart, null);
            context = convertView.getContext();
            holder.chart = (LineChart) convertView.findViewById(R.id.linechart);
            mLineChart = holder.chart;
            holder.marketText = (TextView)convertView.findViewById(R.id.market_text);
            mLineChart.setOnChartValueSelectedListener(this);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        // holder.chart.setValueTypeface(mTf);
        holder.chart.setDescription("");
        holder.chart.setDrawGridBackground(false);

        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);


        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);

        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setTypeface(mTf);
//        rightAxis.setLabelCount(5, false);
//        rightAxis.setDrawGridLines(false);

        // set data
        holder.chart.setData((LineData) mChartData);

        // holder.chart.invalidate();
        holder.chart.animateX(750);

        //Set title
        switch (position){
            case 0 :
                holder.marketText.setText("Dow Jones Industrial Average");
                break;
            case 1:
                holder.marketText.setText("Nasdaq");
                break;
            case 2:
                holder.marketText.setText("NYSE");
                break;
            case 3:
                holder.marketText.setText("S&P 500");
                break;
        }



        return convertView;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        mLineChart.setMarkerView(new MarketCustomMarkerView(context, R.layout.market_markerview_layout));
    }

    @Override
    public void onNothingSelected() {

    }

    private static class ViewHolder {
        LineChart chart;
        TextView marketText;
    }
}

