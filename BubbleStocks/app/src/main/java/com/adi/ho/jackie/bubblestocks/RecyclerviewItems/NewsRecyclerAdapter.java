package com.adi.ho.jackie.bubblestocks.RecyclerviewItems;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.Item;

import java.util.List;

/**
 * Created by JHADI on 3/29/16.
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder> {

    private List<Item> topStories;
    private Context context;

    public NewsRecyclerAdapter(Context context, List<Item> newsStories){
        topStories = newsStories;
        this.context = context;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_newsstory_layout, null);
        NewsViewHolder nvh = new NewsViewHolder(view);
        return nvh;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        holder.mTitleText.setText(topStories.get(position).getTitle());
        holder.mLinkText.setText(topStories.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return topStories.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTitleText;
        public TextView mLinkText;
        public NewsViewHolder(View itemView) {
            super(itemView);
            mTitleText = (TextView)itemView.findViewById(R.id.news_title);
            mLinkText = (TextView)itemView.findViewById(R.id.news_story_link_textview);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
