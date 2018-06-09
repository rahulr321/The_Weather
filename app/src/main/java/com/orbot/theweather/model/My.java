package com.orbot.theweather.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rahul on 14/11/2015.
 */
public class My {
    Calendar calendar=Calendar.getInstance();
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getFlickrImageURL(String farmID, String serverID, String ID, String secret) {
        return "https://farm" + farmID + ".staticflickr.com/" + serverID + "/" + ID + "_" + secret + ".jpg";
    }

    public static String getWeatherResourceName(int code) {
        return "wi_owm_" + code;
    }
    public static String getWeatherResourceName(int code,Date time) {
        if(time.getHours()>6&& time.getHours()<18)
        return "wi_owm_" + code; //return neutral
        else
            return "wi_owm_night_" + code;
    }

    public static String getTempToCelsius(double temp){
        return Math.round(temp- 273.15)+"";
    }

    public static String getTempToFahrenheit (double temp){
        return Math.round(temp * 9/5 - 459.67)+"";
    }
}
