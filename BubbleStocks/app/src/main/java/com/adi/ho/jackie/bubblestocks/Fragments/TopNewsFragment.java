package com.adi.ho.jackie.bubblestocks.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adi.ho.jackie.bubblestocks.HttpConnections.YahooTopStoriesRequest;
import com.adi.ho.jackie.bubblestocks.R;
import com.adi.ho.jackie.bubblestocks.RecyclerviewItems.DividerItemDecoration;
import com.adi.ho.jackie.bubblestocks.RecyclerviewItems.NewsRecyclerAdapter;
import com.adi.ho.jackie.bubblestocks.RecyclerviewItems.VerticalSpaceItemDecoration;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.Body;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.Channel;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.InitialResult;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.Item;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.Query;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.Results;
import com.adi.ho.jackie.bubblestocks.yahoorssfeed.Rss;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopNewsFragment extends Fragment {

    InitialResult topStories;
    List<Item> mTopNewsItems;
    private RecyclerView mRecyclerView;
    private NewsRecyclerAdapter mAdapter;

    public TopNewsFragment() {
        // Required empty public constructor
        mTopNewsItems = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_top_news, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.top_news_stories_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager newsStoriesLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(newsStoriesLayoutManager);


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CallTopStoriesAsyncTask().execute();

    }

    private class CallTopStoriesAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            String data = "";
            try {
                data = new YahooTopStoriesRequest().run();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
        @Override
        protected void onPostExecute(String s) {
            Gson gson = new Gson();
            topStories = gson.fromJson(s, InitialResult.class);
            //Find top news items titles
            mTopNewsItems = topStories.getQuery().getResults().getBody().getRss().getChannel().getItem();
            mAdapter = new NewsRecyclerAdapter(getContext(), mTopNewsItems);
            mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(40));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
