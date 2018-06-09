package com.orbot.theweather.model;

import java.util.Date;

/**
 * Created by Rahul on 06/01/2016.
 */
public class ForecastDailyDetails {

    private int DT;
    private WeatherMain weatherMain;
    private Weathr weathr;
    private Wind wind;
    private WeatherCloud weatherCloud;
    private Rain rain;
    private Snow snow;
    private Date forecastedTime;

    public ForecastDailyDetails() {
        this.DT = 0;
        this.weatherMain = null;
        this.weathr = null;
        this.wind = null;
        this.weatherCloud = null;
        this.rain = null;
        this.snow = null;
        this.forecastedTime=null;
    }

    public ForecastDailyDetails(int DT, WeatherMain weatherMain, Weathr weathr, Wind wind, WeatherCloud weatherCloud, Rain rain, Snow snow, Date forecastedTime) {
        this.DT = DT;
        this.weatherMain = weatherMain;
        this.weathr = weathr;
        this.wind = wind;
        this.weatherCloud = weatherCloud;
        this.rain = rain;
        this.snow = snow;
        this.forecastedTime=forecastedTime;
    }

    public int getDT() {
        return DT;
    }

    public void setDT(int DT) {
        this.DT = DT;
    }

    public WeatherMain getWeatherMain() {
        return weatherMain;
    }

    public void setWeatherMain(WeatherMain weatherMain) {
        this.weatherMain = weatherMain;
    }

    public Weathr getWeathr() {
        return weathr;
    }

    public void setWeathr(Weathr weathr) {
        this.weathr = weathr;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public WeatherCloud getWeatherCloud() {
        return weatherCloud;
    }

    public void setWeatherCloud(WeatherCloud weatherCloud) {
        this.weatherCloud = weatherCloud;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public void setSnow(Snow snow) {
        this.snow = snow;
    }

    public Date getForecastedTime() {
        return forecastedTime;
    }

    public void setForecastedTime(Date forecastedTime) {
        this.forecastedTime = forecastedTime;
    }

    @Override
    public String toString() {
        return "ForecastDailyDetails{" +
                "DT=" + DT +
                ", weatherMain=" + weatherMain.toString() +
                ", weathr=" + weathr.toString() +
                ", wind=" + wind.toString() +
                ", weatherCloud=" + weatherCloud.toString() +
                ", rain=" + rain.getRain() +
                ", snow=" + snow.getSnow() +
                ", forecastedTime=" + forecastedTime.toString() +
                '}';
    }



    /*
        CLASSES
     */

    public static class  WeatherMain {
        private double temp, tempMin, tempMax, pressure, pressureSeaLevel, pressureGroundLevel,
                humidity;

        public WeatherMain() {
        }

        public WeatherMain(double temp, double tempMin, double tempMax, double pressure, double pressureSeaLevel, double pressureGroundLevel, double humidity) {
            this.temp = temp;
            this.tempMin = tempMin;
            this.tempMax = tempMax;
            this.pressure = pressure;
            this.pressureSeaLevel = pressureSeaLevel;
            this.pressureGroundLevel = pressureGroundLevel;
            this.humidity = humidity;
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

        public double getHumidity() {
            return humidity;
        }

        public void setHumidity(double humidity) {
            this.humidity = humidity;
        }

        @Override
        public String toString() {
            return "WeatherMain{" +
                    "temp=" + temp +
                    ", tempMin=" + tempMin +
                    ", tempMax=" + tempMax +
                    ", pressure=" + pressure +
                    ", pressureSeaLevel=" + pressureSeaLevel +
                    ", pressureGroundLevel=" + pressureGroundLevel +
                    ", humidity=" + humidity +
                    '}';
        }
    }

    public static class Weathr {
        private int weatherID;
        private String mainWeather, description, iconID;

        public Weathr() {
        }

        public Weathr(int weatherID, String mainWeather, String description, String iconID) {
            this.weatherID = weatherID;
            this.mainWeather = mainWeather;
            this.description = description;
            this.iconID = iconID;
        }

        public int getWeatherID() {
            return weatherID;
        }

        public void setWeatherID(int weatherID) {
            this.weatherID = weatherID;
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

        @Override
        public String toString() {
            return "Weathr{" +
                    "weatherID=" + weatherID +
                    ", mainWeather='" + mainWeather + '\'' +
                    ", description='" + description + '\'' +
                    ", iconID='" + iconID + '\'' +
                    '}';
        }
    }

    public static class Wind {
        private double windSpeed, windDirection;

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

    }

    public static class WeatherCloud {
        private double cloudLevel;

        public double getCloudLevel() {
            return cloudLevel;
        }

        public void setCloudLevel(double cloudLevel) {
            this.cloudLevel = cloudLevel;
        }

        @Override
        public String toString() {
            return "WeatherCloud{" +
                    "cloudLevel=" + cloudLevel +
                    '}';
        }
    }

    public static class Rain {
        public double getRain() {
            return rain;
        }

        public void setRain(double rain) {
            this.rain = rain;
        }

        double rain;
    }

    public static class Snow {
        private double snow;

        public double getSnow() {
            return snow;
        }

        public void setSnow(double snow) {
            this.snow = snow;
        }
    }
}