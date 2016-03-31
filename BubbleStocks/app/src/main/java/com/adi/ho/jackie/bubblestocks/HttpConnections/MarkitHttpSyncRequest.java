package com.adi.ho.jackie.bubblestocks.HttpConnections;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JHADI on 3/31/16.
 */
public class MarkitHttpSyncRequest {

    private static final String MARKIT_API_CALL = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=";
    private static final String MARKIT_END_CALL = "&callback=myFunction";
    OkHttpClient client = new OkHttpClient();

    public String run(String symbol) throws IOException {
        Request request = new Request.Builder()
                .url(MARKIT_API_CALL+symbol+MARKIT_END_CALL)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
