package com.adi.ho.jackie.bubblestocks.HttpConnections;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JHADI on 3/21/16.
 */
public class DowHttpRequests {
    private static final String DOWINDEX_URL = "https://www.quandl.com/api/v3/datasets/BCB/UDJIAD1.json?start_date=";
    OkHttpClient client = new OkHttpClient();

    public String run(String date) throws IOException {
        Request request = new Request.Builder()
                .url(DOWINDEX_URL+date)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
