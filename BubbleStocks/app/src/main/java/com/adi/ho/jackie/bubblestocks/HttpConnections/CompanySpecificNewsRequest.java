package com.adi.ho.jackie.bubblestocks.httpconnections;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JHADI on 4/2/16.
 */
public class CompanySpecificNewsRequest {

    public static final String YAHOO_APICALL="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20rss%20where%20url%20in%20('https%3A%2F%2Ffeeds.finance.yahoo.com%2Frss%2F2.0%2Fheadline%3Fs%3D";
    public static final String YAHOO_ENDCALL= "%26region%3DUS%26lang%3Den-US')&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    OkHttpClient client = new OkHttpClient();

    public String run(String symbol) throws IOException {
        Request request = new Request.Builder()
                .url(YAHOO_APICALL+symbol+YAHOO_ENDCALL)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
