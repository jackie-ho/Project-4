package com.adi.ho.jackie.bubblestocks.httpconnections;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JHADI on 4/4/16.
 */
public class SearchSuggestionHttpRequest {
    private static final String yahooCall = "http://d.yimg.com/aq/autoc?query=";
    private static final String yahooTags = "&region=US&lang=en-US";
    OkHttpClient client = new OkHttpClient();
    private ArrayList<String> listSymbols = new ArrayList<>();

    public String[] run(String query) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(yahooCall + query + yahooTags)
                .build();

        Response response = client.newCall(request).execute();

        String data = response.body().string();
        JSONObject initialSearch = new JSONObject(data);
        JSONArray resultArray = initialSearch.getJSONArray("Result");
        for (int i = 0 ; i < resultArray.length() ; i++){
            JSONObject suggestion = resultArray.getJSONObject(i);
            listSymbols.add(suggestion.getString("symbol"));
        }
        String[] resultList = new String[listSymbols.size()];
        for (int j = 0; j < listSymbols.size(); j++){
            resultList[j] = listSymbols.get(j);
        }

        return resultList;
    }

    private class SearchAsynctask extends AsyncTask<String, Void, String[]>{
        @Override
        protected String[] doInBackground(String... params) {
            Request request = new Request.Builder()
                    .url(yahooCall + params[0] + yahooTags)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                String data = response.body().string();
                JSONObject initialSearch = new JSONObject(data);
                JSONArray resultArray = initialSearch.getJSONArray("Result");
                for (int i = 0 ; i < resultArray.length() ; i++){
                    JSONObject suggestion = resultArray.getJSONObject(i);
                    listSymbols.add(suggestion.getString("symbol"));
                }
                String[] resultList = new String[listSymbols.size()];
                for (int j = 0; j < listSymbols.size(); j++){
                    resultList[j] = listSymbols.get(j);
                }

                return resultList;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new String[0];
        }
    }
}
