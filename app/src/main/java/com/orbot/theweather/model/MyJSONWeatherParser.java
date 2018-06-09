package com.orbot.theweather.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.orbot.theweather.model.ForecastDailyDetails.Rain;
import static com.orbot.theweather.model.ForecastDailyDetails.Snow;
import static com.orbot.theweather.model.ForecastDailyDetails.WeatherCloud;
import static com.orbot.theweather.model.ForecastDailyDetails.Weathr;
import static com.orbot.theweather.model.ForecastDailyDetails.Wind;

public class MyJSONWeatherParser {
    private Calendar c = Calendar.getInstance();

    public static CurrentWeather getWeatherCurrent(String data) throws JSONException {
        CurrentWeather weather = new CurrentWeather();

        if (data != null) {
            //  create out JSONObject from the data
            JSONObject jObj = new JSONObject(data);

            //  start extracting the info
            CityLocation loc = new CityLocation();

            JSONObject coordObj = getObject("coord", jObj);
            loc.setLatitude(getString("lat", coordObj));
            loc.setLongitude(getString("lon", coordObj));

            JSONObject sysObj = getObject("sys", jObj);
            loc.setCountry(getString("country", sysObj));
            loc.setSunrise(getInt("sunrise", sysObj));
            loc.setSunset(getInt("sunset", sysObj));
            loc.setName(getString("name", jObj));
            loc.setCityID(getInt("id", jObj));

            // We get weather info (This is an array)
            JSONArray jArr = jObj.getJSONArray("weather");

            // We use only the first value
            JSONObject JSONWeather = jArr.getJSONObject(0);
            weather.setWeatherID(getInt("id", JSONWeather));
            weather.setDescription(getString("description", JSONWeather));
            weather.setMainWeather(getString("main", JSONWeather));
            weather.setIconID(getString("icon", JSONWeather));

            JSONObject mainObj = getObject("main", jObj);
            weather.setHumidity(getInt("humidity", mainObj));
            weather.setPressure(getInt("pressure", mainObj));
            weather.setTempMax(getFloat("temp_max", mainObj));
            weather.setTempMin(getFloat("temp_min", mainObj));
            weather.setTemp(getFloat("temp", mainObj));

            // Wind
            JSONObject wObj = getObject("wind", jObj);
            weather.setWindSpeed(getFloat("speed", wObj));
            weather.setWindDirection(getFloat("deg", wObj));

            // Clouds
            JSONObject cObj = getObject("clouds", jObj);
            weather.setCloudLevel(getFloat("all", cObj));

            weather.setCityID(loc.getCityID());
            weather.setCityLocation(loc);
            // We download the icon to show
        }
        return weather;
    }

    public static CurrentWeather[] getCurrentWeatherCities(String data) throws JSONException {
        CurrentWeather[] weather = null;// = new CurrentWeather();
        CurrentWeather tempWeather;

        if (data != null) {
            //  create out JSONObject from the data
            JSONObject jObj = new JSONObject(data);
            int totalCities = getInt("cnt", jObj);
            JSONArray jsonListArray = jObj.getJSONArray("list");
            weather = new CurrentWeather[totalCities];

            for (int x = 0; x < totalCities; x++) {
                JSONObject jsonObjectDetail = jsonListArray.getJSONObject(x);
                tempWeather = new CurrentWeather();
                //  start extracting the info
                CityLocation loc = new CityLocation();

                JSONObject coordObj = getObject("coord", jsonObjectDetail);
                loc.setLatitude(getString("lat", coordObj));
                loc.setLongitude(getString("lon", coordObj));

                JSONObject sysObj = getObject("sys", jsonObjectDetail);
                loc.setCountry(getString("country", sysObj));
                loc.setSunrise(getInt("sunrise", sysObj));
                loc.setSunset(getInt("sunset", sysObj));
                loc.setName(getString("name", jsonObjectDetail));
                loc.setCityID(getInt("id", jsonObjectDetail));

                // We get weather info (This is an array)
                JSONArray jArr = jsonObjectDetail.getJSONArray("weather");

                // We use only the first value
                JSONObject JSONWeather = jArr.getJSONObject(0);
                tempWeather.setWeatherID(getInt("id", JSONWeather));
                tempWeather.setDescription(getString("description", JSONWeather));
                tempWeather.setMainWeather(getString("main", JSONWeather));
                tempWeather.setIconID(getString("icon", JSONWeather));

                JSONObject mainObj = getObject("main", jsonObjectDetail);
                tempWeather.setHumidity(getInt("humidity", mainObj));
                tempWeather.setPressure(getInt("pressure", mainObj));
                tempWeather.setTempMax(getFloat("temp_max", mainObj));
                tempWeather.setTempMin(getFloat("temp_min", mainObj));
                tempWeather.setTemp(getFloat("temp", mainObj));

                // Wind
                JSONObject wObj = getObject("wind", jsonObjectDetail);
                tempWeather.setWindSpeed(getFloat("speed", wObj));
                tempWeather.setWindDirection(getFloat("deg", wObj));

                // Clouds
                JSONObject cObj = getObject("clouds", jsonObjectDetail);
                tempWeather.setCloudLevel(getFloat("all", cObj));

                tempWeather.setCityID(loc.getCityID());
                tempWeather.setCityLocation(loc);


                weather[x] = tempWeather;
            }
        }
        return weather;
    }

    public static Weather getWeather(String data) throws JSONException {
        Weather weather = new Weather();

        if (data != null) {
            //  create out JSONObject from the data
            JSONObject jObj = new JSONObject(data);

            //  start extracting the info
            CityLocation loc = new CityLocation();

            JSONObject coordObj = getObject("coord", jObj);
            loc.setLatitude(getString("lat", coordObj));
            loc.setLongitude(getString("lon", coordObj));

            JSONObject sysObj = getObject("sys", jObj);
            loc.setCountry(getString("country", sysObj));
            loc.setSunrise(getInt("sunrise", sysObj));
            loc.setSunset(getInt("sunset", sysObj));
            loc.setName(getString("name", jObj));
            loc.setCityID(getInt("id", jObj));

            // We get weather info (This is an array)
            JSONArray jArr = jObj.getJSONArray("weather");

            // We use only the first value
            JSONObject JSONWeather = jArr.getJSONObject(0);
            weather.setWeatherID(getInt("id", JSONWeather));
            weather.setDescription(getString("description", JSONWeather));
            weather.setMainWeather(getString("main", JSONWeather));
            weather.setIconID(getString("icon", JSONWeather));

            JSONObject mainObj = getObject("main", jObj);
            weather.setHumidity(getInt("humidity", mainObj));
            weather.setPressure(getInt("pressure", mainObj));
            weather.setTempMax(getFloat("temp_max", mainObj));
            weather.setTempMin(getFloat("temp_min", mainObj));
            weather.setTemp(getFloat("temp", mainObj));

            // Wind
            JSONObject wObj = getObject("wind", jObj);
            weather.setWindSpeed(getFloat("speed", wObj));
            weather.setWindDirection(getFloat("deg", wObj));

            // Clouds
            JSONObject cObj = getObject("clouds", jObj);
            weather.setCloudLevel(getFloat("all", cObj));

            weather.setCityID(loc.getCityID());
            weather.setCityLocation(loc);
            // We download the icon to show
        }
        return weather;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
    /*




     */

    public static ForecastDailyDetails[] getDailyWeather(String data) throws JSONException {
        ForecastDailyDetails[] forecastDaily = null;
        if (data != null) {
            //  create out JSONObject from the data
            JSONObject jObj = new JSONObject(data);
            JSONArray jsonListArray = jObj.getJSONArray("list");
            int totalWeatherData = jsonListArray.length();
            forecastDaily = new ForecastDailyDetails[totalWeatherData];

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

            ForecastDailyDetails.WeatherMain weatherMain;
            ForecastDailyDetails.Weathr weathr;
            WeatherCloud weatherCloud;
            ForecastDailyDetails.Wind wind;
            ForecastDailyDetails.Snow snow = null;
            ForecastDailyDetails.Rain rain = null;
            int DT;
            Date date = null;

            for (int x = 0; x < totalWeatherData; x++) {
                JSONObject jsonObjectDetail = jsonListArray.getJSONObject(x);

                DT = getInt("dt", jsonObjectDetail);

                // We get weather info (This is an array)
                JSONArray jArr = jsonObjectDetail.getJSONArray("weather");
                // We use only the first value
                JSONObject JSONWeather = jArr.getJSONObject(0);
                weathr = new Weathr();
                weathr.setWeatherID(getInt("id", JSONWeather));
                weathr.setDescription(getString("description", JSONWeather));
                weathr.setMainWeather(getString("main", JSONWeather));
                weathr.setIconID(getString("icon", JSONWeather));

                //Main
                JSONObject jsonWeatherMain = getObject("main", jsonObjectDetail);
                weatherMain = new ForecastDailyDetails.WeatherMain();
                weatherMain.setHumidity(getInt("humidity", jsonWeatherMain));
                weatherMain.setPressure(getInt("pressure", jsonWeatherMain));
                weatherMain.setPressureSeaLevel(getInt("sea_level", jsonWeatherMain));
                weatherMain.setPressureGroundLevel(getInt("grnd_level", jsonWeatherMain));
                weatherMain.setTemp(getFloat("temp", jsonWeatherMain));
                weatherMain.setTempMax(getFloat("temp_max", jsonWeatherMain));
                weatherMain.setTempMin(getFloat("temp_min", jsonWeatherMain));

                // Wind
                wind = new Wind();
                try {
                    if(!getString("wind",jsonObjectDetail).equalsIgnoreCase("null")){
                        if (jsonObjectDetail.has("wind")) {
                            JSONObject wObj = getObject("wind", jsonObjectDetail);
                            wind.setWindSpeed(getFloat("speed", wObj));
                            wind.setWindDirection(getFloat("deg", wObj));
                        }
                    }else{
                        throw new JSONException("ErrorLogg");
                    }
                }catch (JSONException e){
                    Log.d("ErrorLog","here");
                    e.printStackTrace();
                }

                // Clouds
                weatherCloud = new WeatherCloud();
                JSONObject cObj = getObject("clouds", jsonObjectDetail);
                weatherCloud.setCloudLevel(getFloat("all", cObj));
                try {
                    //forecasted time
                    date = format.parse(getString("dt_txt", jsonObjectDetail));
                    //Log.d("dt_date",date.toString());
                    //Snow
                    snow = new Snow();
                    if (jsonObjectDetail.has("snow")) {
                        JSONObject sObj = getObject("snow", jsonObjectDetail);
                        if (sObj.has("3h"))
                            snow.setSnow(getFloat("3h", sObj));
                    }
                    //rain)
                    rain = new Rain();

                    if (jsonObjectDetail.has("rain")) {
                        JSONObject rObj = getObject("rain", jsonObjectDetail);
                        if (rObj.has("3h"))
                            rain.setRain(getFloat("3h", rObj));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
                forecastDaily[x] = new ForecastDailyDetails(DT, weatherMain, weathr, wind, weatherCloud, rain, snow, date);
            }
        }
        return forecastDaily;
    }

    public static DailyForecast[] get7DailyWeather(String data) throws JSONException {
        DailyForecast[] dailyForecast = new DailyForecast[0];
        if (data != null) {
            //  create out JSONObject from the data
            JSONObject jObj = new JSONObject(data);
            JSONArray jsonListArray = jObj.getJSONArray("list");
            int totalWeatherData = jsonListArray.length();

            dailyForecast = new DailyForecast[totalWeatherData];
            DailyForecast.WeatherMain weatherMain;
            DailyForecast.Weathr weathr;
            DailyForecast.WeatherCloud weatherCloud;
            DailyForecast.Wind wind;
            DailyForecast.Snow snow;
            DailyForecast.Rain rain;
            DailyForecast.Temp temp;
            JSONObject jsonObjectDetail;
            int DT;
            Date date;

            for (int x = 0; x < totalWeatherData; x++) {
                jsonObjectDetail = jsonListArray.getJSONObject(x);

                weatherMain = new DailyForecast.WeatherMain();
                weatherCloud = new DailyForecast.WeatherCloud();
                weathr = new DailyForecast.Weathr();
                wind = new DailyForecast.Wind();
                snow = new DailyForecast.Snow();
                rain = new DailyForecast.Rain();
                temp = new DailyForecast.Temp();

                DT = getInt("dt", jsonObjectDetail);

                //Temp
                JSONObject jsonObjectTemp = getObject("temp", jsonObjectDetail);
                temp.setDay(getFloat("day", jsonObjectTemp));
                temp.setMin(getFloat("min", jsonObjectTemp));
                temp.setMax(getFloat("max", jsonObjectTemp));
                temp.setNight(getFloat("night", jsonObjectTemp));
                temp.setEve(getFloat("eve", jsonObjectTemp));
                temp.setMorn(getFloat("morn", jsonObjectTemp));

                //Main
                weatherMain.setPressure(getInt("pressure", jsonObjectDetail));
                weatherMain.setHumidity(getInt("humidity", jsonObjectDetail));

                //Weather
                JSONArray jsonArrayWeather = jsonObjectDetail.getJSONArray("weather");
                // We use only the first value
                JSONObject JSONWeather = jsonArrayWeather.getJSONObject(0);
                weathr.setWeatherID(getInt("id", JSONWeather));
                weathr.setMainWeather(getString("main", JSONWeather));
                weathr.setDescription(getString("description", JSONWeather));
                weathr.setIconID(getString("icon", JSONWeather));


                // Wind
                wind.setWindSpeed(getFloat("speed", jsonObjectDetail));
                wind.setWindDirection(getFloat("deg", jsonObjectDetail));

                // Clouds
                weatherCloud.setCloudLevel(getFloat("clouds", jsonObjectDetail));
                try {
                    //Snow
                    snow.setSnow(getFloat("snow", jsonObjectDetail));

                    //rain)
                    rain.setRain(getFloat("rain", jsonObjectDetail));
                } catch (Exception e) {
                }

                //forecasted time
                date = new Date(DT * 1000L);
                dailyForecast[x] = new DailyForecast(DT, temp, weatherMain, weathr, wind, weatherCloud, rain, snow, date);
            }
        }
        return dailyForecast;
    }
}
