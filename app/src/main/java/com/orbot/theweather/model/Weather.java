package com.orbot.theweather.model;

import java.io.Serializable;

/**
 * Created by Rahul on 28/10/2015.
 */
public class Weather implements Serializable {
    private int cityID, weatherID, DT;
    private double temp, tempMin, tempMax, pressure, pressureSeaLevel, pressureGroundLevel,
            cloudLevel, windSpeed, windDirection, humidity;
    private String mainWeather, description, iconID, foretastedTime;

    public CityLocation getCityLocation() {
        return cityLocation;
    }

    public void setCityLocation(CityLocation cityLocation) {
        this.cityLocation = cityLocation;
    }

    private CityLocation cityLocation;

    public Weather() {
        this.DT = 0;
        this.cityID = 0;
        this.weatherID = 0;
        this.temp = 0;
        this.tempMin = 0;
        this.tempMax = 0;
        this.pressure = 0;
        this.pressureSeaLevel = 0;
        this.pressureGroundLevel = 0;
        this.cloudLevel = 0;
        this.windSpeed = 0;
        this.windDirection = 0;
        this.humidity = 0;
        this.mainWeather = "";
        this.description = "";
        this.iconID = "";
        foretastedTime = "";
    }

    public Weather(int DT, int cityID, int weatherID, double temp, double tempMin, double tempMax,
                   double pressure, double pressureSeaLevel, double pressureGroundLevel, double cloudLevel, double windSpeed,
                   double windDirection, double humidity, String mainWeather, String description, String iconID, String forecastedTime) {
        this.DT = DT;
        this.cityID = cityID;
        this.weatherID = weatherID;
        this.temp = temp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.pressure = pressure;
        this.pressureSeaLevel = pressureSeaLevel;
        this.pressureGroundLevel = pressureGroundLevel;
        this.cloudLevel = cloudLevel;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.humidity = humidity;
        this.mainWeather = mainWeather;
        this.description = description;
        this.iconID = iconID;
        this.foretastedTime = forecastedTime;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public int getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(int weatherID) {
        this.weatherID = weatherID;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getPressureSeaLevel() {
        return pressureSeaLevel;
    }

    public void setPressureSeaLevel(double pressureSeaLevel) {
        this.pressureSeaLevel = pressureSeaLevel;
    }

    public double getPressureGroundLevel() {
        return pressureGroundLevel;
    }

    public void setPressureGroundLevel(double pressureGroundLevel) {
        this.pressureGroundLevel = pressureGroundLevel;
    }

    public double getCloudLevel() {
        return cloudLevel;
    }

    public void setCloudLevel(double cloudLevel) {
        this.cloudLevel = cloudLevel;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getMainWeather() {
        return mainWeather;
    }

    public void setMainWeather(String mainWeather) {
        this.mainWeather = mainWeather;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconID() {
        return iconID;
    }

    public void setIconID(String iconID) {
        this.iconID = iconID;
    }

    public int getDT() {
        return DT;
    }

    public void setDT(int DT) {
        this.DT = DT;
    }

    public String getForetastedTime() {
        return foretastedTime;
    }

    public void setForetastedTime(String foretastedTime) {
        this.foretastedTime = foretastedTime;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "cityID=" + cityID +
                ", weatherID=" + weatherID +
                ", DT=" + DT +
                ", temp=" + temp +
                ", tempMin=" + tempMin +
                ", tempMax=" + tempMax +
                ", pressure=" + pressure +
                ", pressureSeaLevel=" + pressureSeaLevel +
                ", pressureGroundLevel=" + pressureGroundLevel +
                ", cloudLevel=" + cloudLevel +
                ", windSpeed=" + windSpeed +
                ", windDirection=" + windDirection +
                ", humidity=" + humidity +
                ", mainWeather='" + mainWeather + '\'' +
                ", description='" + description + '\'' +
                ", iconID='" + iconID + '\'' +
                ", foretastedTime='" + foretastedTime + '\'' +
                ", cityLocation=" + cityLocation +
                '}';
    }
}
