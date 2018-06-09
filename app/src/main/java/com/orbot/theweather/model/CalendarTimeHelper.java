package com.orbot.theweather.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Rahul on 04/05/2016.
 */
public class CalendarTimeHelper implements Serializable {
    private int sec, min, hour, date, month, year;

    public CalendarTimeHelper() {
        Calendar calendar= Calendar.getInstance();
        this.sec = calendar.get(Calendar.SECOND);
        this.min = calendar.get(Calendar.MINUTE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.date = calendar.get(Calendar.DATE);
        this.month = calendar.get(Calendar.MONTH);
        this.year = calendar.get(Calendar.YEAR);
    }

    public CalendarTimeHelper(Calendar calendar) {
        this.sec = calendar.get(Calendar.SECOND);
        this.min = calendar.get(Calendar.MINUTE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.date = calendar.get(Calendar.DATE);
        this.month = calendar.get(Calendar.MONTH);
        this.year = calendar.get(Calendar.YEAR);
    }

    public CalendarTimeHelper(int min, int hour, int date, int month, int year) {
        this.sec = 0;
        this.min = min;
        this.hour = hour;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public CalendarTimeHelper(int sec, int min, int hour, int date, int month, int year) {
        this.sec = sec;
        this.min = min;
        this.hour = hour;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
