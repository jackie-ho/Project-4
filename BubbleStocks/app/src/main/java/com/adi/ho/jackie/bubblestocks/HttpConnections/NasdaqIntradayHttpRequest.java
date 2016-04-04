package com.adi.ho.jackie.bubblestocks.httpconnections;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JHADI on 3/29/16.
 */
public class NasdaqIntradayHttpRequest {

   // public static final  String nasdaqRequest = "http://www.google.com/finance/getprices?i=60&p=1d&f=d,o,h,l,c,v&df=cpct&q=IXIC"
    private static final String nasdaqRequest = "http://www.google.com/finance/getprices?i=60&p=1d&f=d,c&df=cpct&q=IXIC";
    OkHttpClient client = new OkHttpClient();

    public String run() throws IOException {
        Request request = new Request.Builder()
                .url(nasdaqRequest)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
