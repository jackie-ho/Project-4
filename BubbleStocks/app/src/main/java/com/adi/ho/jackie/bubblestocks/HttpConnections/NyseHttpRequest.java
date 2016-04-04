package com.adi.ho.jackie.bubblestocks.httpconnections;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JHADI on 3/26/16.
 */
public class NyseHttpRequest {
    private static final String DOWINDEX_URL = "https://www.quandl.com/api/v3/datasets/YAHOO/INDEX_NYA.json?start_date=";
    private static final String API_KEY = "HhbKySgiDH1WfqPy4xtz";
    OkHttpClient client = new OkHttpClient();

    public String run(String date) throws IOException {
        Request request = new Request.Builder()
                .url(DOWINDEX_URL+date+"&api_key="+API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
