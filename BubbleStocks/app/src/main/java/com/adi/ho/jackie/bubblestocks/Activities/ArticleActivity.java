package com.adi.ho.jackie.bubblestocks.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.adi.ho.jackie.bubblestocks.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ArticleActivity extends AppCompatActivity {

    private String mWebLink;
    private WebView mWebView;
    Document mWebPage;
    ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar)findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);

        mWebView = (WebView)findViewById(R.id.article_webview);
        mProgressBar = (ProgressBar)findViewById(R.id.article_activity_progressbar);

        //Article link from fragment list
        mWebLink = getIntent().getStringExtra("LINK");
        new ConnectToWebAsyncTask().execute(mWebLink);
    }

    private class ConnectToWebAsyncTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            try {
                //Scrape webpage for body
                mWebPage = Jsoup.connect(params[0].trim()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            final Element ele = mWebPage.body(); //mWebpage.body.select("p").text()
            final String body = "<style>img{display: inline; height: auto; max-width: 100%;}</style>"+ele.select("p").html();
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadData(body,"text/html",null);
                }
            });


        }
    }
}
