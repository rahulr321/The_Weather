package com.orbot.theweather.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rahul on 06/11/2015.
 */
public class JSONLocationParser
{
    public static CityLocation getLocation(String data) throws JSONException
    {
        CityLocation loc = new CityLocation();

        if(data!=null)
        {
            //  create out JSONObject from the data
            JSONObject jObj = new JSONObject(data);

            loc.setCityID(getInt("id", jObj));
            loc.setName(getString("name", jObj));

            JSONObject sysObj = getObject("sys", jObj);
            loc.setCountry(getString("country", sysObj));
            loc.setSunrise(getInt("sunrise", sysObj));
            loc.setSunset(getInt("sunset", sysObj));;

            JSONObject coordObj = getObject("coord", jObj);
            loc.setLongitude(getString("lon", coordObj));
            loc.setLatitude(getString("lat", coordObj));
        }
        return loc;
    }



    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}
