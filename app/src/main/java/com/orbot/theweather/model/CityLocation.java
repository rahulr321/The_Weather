package com.orbot.theweather.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Rahul on 28/10/2015.
 */
public class CityLocation implements Serializable {
    private int CityID;
    private String Name;
    private String Longitude;
    private String Latitude;
    private String Country;


    private String IMGURL;
    private long sunset;
    private long sunrise;
    private Locale locale;
    private String timeZone;
    private TimeZoneHelper timeZoneHelper;

    public CityLocation() {
        this.CityID = 0;
        this.Name = "";
        this.Latitude = "";
        this.Longitude = "";
        this.Country = "";
        this.IMGURL = "";
        this.sunset = 0;
        this.sunrise = 0;
        this.locale = Locale.getDefault();
        this.timeZone = "";
        this.timeZoneHelper=new TimeZoneHelper();

    }

    public CityLocation(int CityID, String Name, String Longitude, String Latitude, String Country, String IMGURL) {
        this.setCityID(CityID);
        this.setName(Name);
        this.setLatitude(Latitude);
        this.setLongitude(Longitude);
        this.setCountry(Country);
        this.setIMGURL(IMGURL);

        this.locale = new Locale(Locale.getDefault().getLanguage(), Country);


    }

    public String getIMGURL() {
        return IMGURL;
    }

    public void setIMGURL(String IMGURL) {
        this.IMGURL = IMGURL;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public int getCityID() {
        return CityID;
    }

    public void setCityID(int cityID) {
        CityID = cityID;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        this.locale = new Locale(Locale.getDefault().getLanguage(), Country);
        Country = country;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZoneHelper getTimeZoneHelper() {
        return timeZoneHelper;
    }

    public void setTimeZoneHelper(TimeZoneHelper timeZoneHelper) {
        this.timeZoneHelper = timeZoneHelper;
    }

    public Calendar getLocationTime()
    {
        Calendar calendar = new GregorianCalendar();
        TimeZone currentTimeZone = TimeZone.getTimeZone(getTimeZone());
        calendar.setTimeZone(currentTimeZone);
        return calendar;

/*
        long timeCPH = calendar.getTimeInMillis();
        System.out.println("timeCPH  = " + timeCPH);
        System.out.println("hour     = " + calendar.get(Calendar.HOUR_OF_DAY));


        long timeLA = calendar.getTimeInMillis();
        System.out.println("timeLA   = " + timeLA);
        System.out.println("hour     = " + calendar.get(Calendar.HOUR_OF_DAY));*/
    }

    @Override
    public String toString() {
        return "CityLocation{" +
                "CityID=" + CityID +
                ", Name='" + Name + '\'' +
                ", Longitude='" + Longitude + '\'' +
                ", Latitude='" + Latitude + '\'' +
                ", Country='" + Country + '\'' +
                '}';
    }
}

