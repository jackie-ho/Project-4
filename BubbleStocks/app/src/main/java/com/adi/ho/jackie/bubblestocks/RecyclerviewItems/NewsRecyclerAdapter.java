package com.adi.ho.jackie.bubblestocks.recyclerviewitems;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adi.ho.jackie.bubblestocks.activities.ArticleActivity;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.Item;

import java.util.List;

/**
 * Created by JHADI on 3/29/16.
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder> {

    private List<Item> topStories;
    private List<com.adi.ho.jackie.bubblestocks.companyspecificrssfeed.Item> companyStories;
    private Context context;
    private Typeface tf;

    public NewsRecyclerAdapter(Context context, List<Item> newsStories){
        topStories = newsStories;
        this.context = context;
        tf = Typeface.createFromAsset(context.getAssets(), "trade-gothic.ttf");
    }

    public NewsRecyclerAdapter(List<com.adi.ho.jackie.bubblestocks.companyspecificrssfeed.Item> newsStories, Context context){
        this.context = context;
        companyStories = newsStories;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_newsstory_layout, null);
        NewsViewHolder nvh = new NewsViewHolder(view);
        return nvh;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        if (topStories != null && topStories.size() > 0 && topStories.get(position) instanceof Item) {
            holder.mTitleText.setText(topStories.get(position).getTitle());
            holder.mLinkText.setText(topStories.get(position).getLink());
            holder.mTitleText.setTypeface(tf);
        } else {
            holder.mLinkText.setText(companyStories.get(position).getLink());
            holder.mTitleText.setText(companyStories.get(position).getTitle());
            holder.mTitleText.setTypeface(tf);

        }
    }

    @Override
    public int getItemCount() {
        if (topStories != null && topStories.size() > 0){
            return topStories.size();
        }
        else {
            return companyStories.size();
        }
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTitleText;
        public TextView mLinkText;
        public NewsViewHolder(View itemView) {
            super(itemView);
            mTitleText = (TextView)itemView.findViewById(R.id.news_title);
            mLinkText = (TextView)itemView.findViewById(R.id.news_story_link_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ArticleActivity.class);
            intent.putExtra("LINK", mLinkText.getText().toString());
            context.startActivity(intent);
            }
    }
}
