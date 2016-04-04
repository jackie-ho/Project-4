package com.adi.ho.jackie.bubblestocks.httpconnections;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JHADI on 3/26/16.
 */
public class IntradayMarketDataRequest {
    private static final String YAHOO_API_CALL = "https://chartapi.finance.yahoo.com/instrument/1.0/";
    private static final String API_END_CALL = "/chartdata;type=quote;range=1d/json";
    OkHttpClient okHttpClient = new OkHttpClient();
    public String run(String symbol) throws IOException{
        Request request = new Request.Builder()
                .url(YAHOO_API_CALL+symbol+API_END_CALL)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

}
