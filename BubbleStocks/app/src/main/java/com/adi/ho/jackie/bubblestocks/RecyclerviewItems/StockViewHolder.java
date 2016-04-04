package com.adi.ho.jackie.bubblestocks.recyclerviewitems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.adi.ho.jackie.bubblestocks.R;

/**
 * Created by JHADI on 3/23/16.
 */
public class StockViewHolder extends RecyclerView.ViewHolder {
    public TextView mSymbol;
    public TextView mPrice;
    public TextView mPriceChange;
    public TextView mPercentChange;

    public StockViewHolder(View itemView) {
        super(itemView);
        mPercentChange = (TextView)itemView.findViewById(R.id.stock_list_percentchange);
        mPrice = (TextView)itemView.findViewById(R.id.stock_list_currentprice);
        mPriceChange = (TextView)itemView.findViewById(R.id.stock_list_change);
        mSymbol = (TextView)itemView.findViewById(R.id.stock_list_symbol);

    }
}
