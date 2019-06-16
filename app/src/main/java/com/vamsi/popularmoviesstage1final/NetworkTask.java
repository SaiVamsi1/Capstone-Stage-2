package com.vamsi.popularmoviesstage1final;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


class NetworkTask {

    public static URL buildURL(String[] query) throws MalformedURLException
    {
        Uri builtUri = Uri.parse(Constants.MOVIEDB_BASE_URL).buildUpon().appendPath(query[0]).appendQueryParameter(Constants.APIKEY,Constants.API_KEY).build();
        return new URL(builtUri.toString());
    }

    public static String getResponsefromurl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(Constants.GET);
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        String response = buffer.toString();
        urlConnection.disconnect();
        return response;
    }

}
