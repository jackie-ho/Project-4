package com.adi.ho.jackie.bubblestocks.recyclerviewitems;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.stockportfolio.DBStock;

import java.util.List;

/**
 * Created by JHADI on 3/23/16.
 */
public class StockRecyclerAdapter extends RecyclerView.Adapter<StockViewHolder> {

    List<DBStock> stockPortfolio;
    Context context;

    public StockRecyclerAdapter(Context context, List<DBStock> stockPortfolio){
        this.context = context;
        this.stockPortfolio = stockPortfolio;

    }
    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item_layout,parent,false);
        StockViewHolder svh = new StockViewHolder(view);
        return svh;
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        holder.mSymbol.setText(stockPortfolio.get(position).getSymbol());
        holder.mPercentChange.setText(stockPortfolio.get(position).getPercentChange());
        holder.mPriceChange.setText(stockPortfolio.get(position).getChange());
        holder.mPrice.setText(stockPortfolio.get(position).getDayClose());
    }

    @Override
    public int getItemCount() {
        return stockPortfolio.size();
    }
    public void swap(List<DBStock> newPortfolio){
        stockPortfolio.clear();
        stockPortfolio.addAll(newPortfolio);
        notifyDataSetChanged();
    }
}
