package com.adi.ho.jackie.bubblestocks.httpconnections;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JHADI on 3/29/16.
 */
public class YahooTopStoriesRequest {

    public static final String TOPSTORIES_LINK = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%27https%3A%2F%2Ffinance.yahoo.com%2Frss%2Ftopfinstories%3Fbypass%3Dtrue%27&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    OkHttpClient client = new OkHttpClient();

    public String run() throws IOException {
        Request request = new Request.Builder()
                .url(TOPSTORIES_LINK)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
