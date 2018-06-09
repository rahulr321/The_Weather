package com.orbot.theweather.model;

import java.io.Serializable;

/**
 * Created by Rahul on 04/08/2015.
 */
public class CardViewDisplay implements Serializable{
    private Weather weather;
    private CurrentWeather currentWeather;
    private int cardColor;

    public CardViewDisplay(Weather weather ) {
        this.weather = weather;
        this.currentWeather = null;
    }

    public CardViewDisplay(Weather weather, CurrentWeather currentWeather) {
        this.weather = weather;
        this.currentWeather = currentWeather;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public int getCardColor() {
        return cardColor;
    }

    public void setCardColor(int cardColor) {
        this.cardColor = cardColor;
    }
}