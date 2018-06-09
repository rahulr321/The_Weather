package com.orbot.theweather.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.TimeZone;

/**
 * Created by Rahul on 16/04/2016.
 */
public class TimeZoneHelper   implements Serializable {
    private String timeZoneId, timeZoneName,status ;
    private int dstOffset, rawOffset;

    public TimeZoneHelper() {
        TimeZone timeZone = TimeZone.getDefault();
        this.dstOffset = timeZone.getDSTSavings();
        this.rawOffset = timeZone.getRawOffset();
        this.status = "Default";
        this.timeZoneId = timeZone.getID();
        this.timeZoneName = timeZone.getDisplayName();
    }

    public TimeZoneHelper(int dstOffset, int rawOffset, String status, String timeZoneId, String timeZoneName) {
        this.dstOffset = dstOffset;
        this.rawOffset = rawOffset;
        this.status = status;
        this.timeZoneId = timeZoneId;
        this.timeZoneName = timeZoneName;
    }

    public TimeZoneHelper(String parsingData) {

        try {
            JSONObject timeZoneObject = new JSONObject(parsingData);
            //resolving data

            this.dstOffset = timeZoneObject.getInt("dstOffset");
            this.rawOffset = timeZoneObject.getInt("rawOffset");
            this.status = timeZoneObject.optString("status");
            this.timeZoneId = timeZoneObject.optString("timeZoneId");
            this.timeZoneName = timeZoneObject.optString("timeZoneName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public int getDstOffset() {
        return dstOffset;
    }

    public void setDstOffset(int dstOffset) {
        this.dstOffset = dstOffset;
    }

    public int getRawOffset() {
        return rawOffset;
    }

    public void setRawOffset(int rawOffset) {
        this.rawOffset = rawOffset;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    @Override
    public String toString() {
        return "timeZoneId='" + timeZoneId + '\'' +
                ", timeZoneName='" + timeZoneName + '\'' +
                ", status='" + status + '\'' +
                ", dstOffset='" + dstOffset + '\'' +
                ", rawOffset='" + rawOffset + '\'';
    }
}
