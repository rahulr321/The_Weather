package com.orbot.theweather.model;

import java.util.Date;

/**
 * Created by Rahul on 08/01/2016.
 **/
public class DailyForecast {
    private int DTs;
    private Temp temp;
    private WeatherMain weatherMain;
    private Weathr weathr;
    private Wind wind;
    private WeatherCloud weatherCloud;
    private Rain rain;
    private Snow snow;
    private Date forecastTime;

    public DailyForecast() {
        this.DTs = 0;
        this.temp = null;
        this.weatherMain = null;
        this.weathr = null;
        this.wind = null;
        this.weatherCloud = null;
        this.rain = null;
        this.snow = null;
        this.forecastTime =null;
    }

    public DailyForecast(int DTs, Temp temp, WeatherMain weatherMain, Weathr weathr, Wind wind, WeatherCloud weatherCloud, Rain rain, Snow snow,Date forecastTime) {
        this.DTs = DTs;
        this.temp = temp;
        this.weatherMain = weatherMain;
        this.weathr = weathr;
        this.wind = wind;
        this.weatherCloud = weatherCloud;
        this.rain = rain;
        this.snow = snow;
        this.forecastTime = forecastTime;
    }

    public Date getForecastTime() {
        return forecastTime;
    }

    public void setForecastTime(Date forecastTime) {
        this.forecastTime = forecastTime;
    }

    public int getDTs() {
        return DTs;
    }

    public void setDTs(int DTs) {
        this.DTs = DTs;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
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

    public static class Temp{
        private double day,min,max,night,eve,morn;
        public Temp() {
            this.day = 0;
            this.min = 0;
            this.max = 0;
            this.night = 0;
            this.eve = 0;
            this.morn = 0;
        }
        public Temp(double day, double min, double max, double night, double eve, double morn) {
            this.day = day;
            this.min = min;
            this.max = max;
            this.night = night;
            this.eve = eve;
            this.morn = morn;
        }

        public double getDay() {
            return day;
        }

        public void setDay(double day) {
            this.day = day;
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getNight() {
            return night;
        }

        public void setNight(double night) {
            this.night = night;
        }

        public double getEve() {
            return eve;
        }

        public void setEve(double eve) {
            this.eve = eve;
        }

        public double getMorn() {
            return morn;
        }

        public void setMorn(double morn) {
            this.morn = morn;
        }
    }

    public static class WeatherMain {
        private double  pressure,humidity;

        public WeatherMain() {
        }

        public WeatherMain(double pressure,double humidity) {
            this.pressure = pressure;
            this.humidity = humidity;
        }

        public double getHumidity() {
            return humidity;
        }

        public void setHumidity(double humidity) {
            this.humidity = humidity;
        }


        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

        @Override
        public String toString() {
            return "WeatherMain{" +
                    "pressure=" + pressure +
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

    }

    public static class Wind {
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

        private double windSpeed, windDirection;
    }

    public static class WeatherCloud {
        public double getCloudLevel() {
            return cloudLevel;
        }

        public void setCloudLevel(double cloudLevel) {
            this.cloudLevel = cloudLevel;
        }

        private double cloudLevel;
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

    @Override
    public String toString() {
        return "DailyForecast{" +
                "DTs=" + DTs +
                ", temp=" + temp +
                ", weatherMain=" + weatherMain +
                ", weathr=" + weathr +
                ", wind=" + wind +
                ", weatherCloud=" + weatherCloud +
                ", rain=" + rain +
                ", snow=" + snow +
                ", forecastTime=" + forecastTime +
                '}';
    }
}