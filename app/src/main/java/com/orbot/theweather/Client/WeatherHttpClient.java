package com.orbot.theweather.Client;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherHttpClient {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String BASE_CityID_URL = "http://api.openweathermap.org/data/2.5/weather?id=";
    private static String BASE_Geo_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static String BASE_Forecast_URL = "http://api.openweathermap.org/data/2.5/forecast?id=";
    private static String BASE_Daily_Forecast_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?id=";
    private static String BASE_Current_MultiCity_URL = "http://api.openweathermap.org/data/2.5/group?id=";

    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private static String API_Part = "&appid=";
    private static String CNT_Part = "&cnt=6";
    private static String lat, lon;

    //enter API herefrom OpenWeatherMap
    private final String weatherAPIKey = "";

    public String getWeatherData(String location) throws JSONException {

        HttpURLConnection con = null;
        InputStream is = null;
        String tempUrl = BASE_URL + location + API_Part + weatherAPIKey;

        try {
            con = (HttpURLConnection) (new URL(tempUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            //will throw exception so handle it on finish - not connected to internet.

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
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    public String getWeatherDataForCoordinate(double latitude, double longitude) throws Exception {
        HttpURLConnection con = null;
        InputStream is = null;
        String tempUrl = BASE_Geo_URL + "lat" + "=" + latitude + "&" + "lon" + "=" + longitude + API_Part + weatherAPIKey;
        //Log.d("CordURL",tempUrl);/*Math.round(latitude*/
        try {
            con = (HttpURLConnection) (new URL(tempUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            //will throw exception so handle it on finish - not connected to internet.

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
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    public String getWeatherDataCityID(String CityID) {
        HttpURLConnection con = null;
        InputStream is = null;
        String tempUrl = BASE_CityID_URL + CityID + API_Part + weatherAPIKey;

        try {
            con = (HttpURLConnection) (new URL(tempUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            //will throw exception so handle it on finish - not connected to internet.

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
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    public String getWeatherDataCityIDs(String [] cityIDs) {
        HttpURLConnection con = null;
        InputStream is = null;
        String s="";
        char comma=',';
        for (int x=0;x<cityIDs.length;x++){
            if(x!=cityIDs.length-1){
                s+=cityIDs[x]+comma;
            }else
            {
                s+=cityIDs;
            }
        }
        String tempUrl = BASE_Current_MultiCity_URL + s + API_Part + weatherAPIKey;

        try {
            con = (HttpURLConnection) (new URL(tempUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            //will throw exception so handle it on finish - not connected to internet.

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
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    public String getDailyWeatherForecast(String CityID) {
        HttpURLConnection con = null;
        InputStream is = null;
        String tempUrl = BASE_Daily_Forecast_URL + CityID + CNT_Part+API_Part + weatherAPIKey;

        try {
            con = (HttpURLConnection) (new URL(tempUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            //will throw exception so handle it on finish - not connected to internet.

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
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    public String getWeatherForecast(String CityID) {
        HttpURLConnection con = null;
        InputStream is = null;
        String tempUrl = BASE_Forecast_URL + CityID + API_Part + weatherAPIKey;

        try {
            con = (HttpURLConnection) (new URL(tempUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            //will throw exception so handle it on finish - not connected to internet.

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
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    public byte[] getImage(String code) {
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            con = (HttpURLConnection) (new URL(IMG_URL + code)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }
}
