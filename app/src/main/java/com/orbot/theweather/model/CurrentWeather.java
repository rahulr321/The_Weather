package com.orbot.theweather.model;

/**
 * Created by Rahul on 29/10/2015.
 */
public class CurrentWeather extends Weather {
    private int itemID, sunrise, sunset;

    public CurrentWeather() {
        super();
        this.itemID = 0;
        this.sunrise = 0;
        this.sunset = 0;
    }

    public CurrentWeather(int DT, int cityID, int weatherID, double temp, double tempMin, double tempMax, double pressure, double pressureSeaLevel,
                          double pressureGroundLevel, double cloudLevel, double windSpeed, double windDirection,
                          double humidity, String mainWeather, String description, String iconID, String forecastedTime, int itemID, int sunrise, int sunset) {
        super(DT, cityID, weatherID, temp, tempMin, tempMax, pressure, pressureSeaLevel, pressureGroundLevel, cloudLevel,
                windSpeed, windDirection, humidity, mainWeather, description, iconID, forecastedTime);
        this.itemID = itemID;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public int getSunrise() {
        return sunrise;
    }

    public void setSunrise(int sunrise) {
        this.sunrise = sunrise;
    }

    public int getSunset() {
        return sunset;
    }

    public void setSunset(int sunset) {
        this.sunset = sunset;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
}
