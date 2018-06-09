package com.orbot.theweather.Client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Rahul on 15/04/2016.
 */
public class TimeZoneHTTPClient {
    private static String BASE_URL = "https://maps.googleapis.com/maps/api/timezone/json?location=";
    private static String TIMESTAMP_TAG = "&timestamp=";
    private static String KEY_TAG = "&key=";
    private static String KEY = "AIzaSyDhYvTUBqWLgHwqEspJzqDWvkhql0_-Ff8";


    public String getLocationTimeZone(String LAT, String LON, int TIMESTAMP) {

        URL url;
        InputStream is = null;
        String urlString = BASE_URL + LAT + "," + LON + TIMESTAMP_TAG + TIMESTAMP + KEY_TAG + KEY;

        try {
            url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}
