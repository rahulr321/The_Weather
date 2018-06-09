package com.orbot.theweather.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.orbot.theweather.model.CityLocation;
import com.orbot.theweather.model.CurrentWeather;
import com.orbot.theweather.model.Weather;

public class TheWeatherDatabase extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static String DB_Name = "TheWeatherDatabase.db";

    // Table Names
    private static final String TB_Location = "Location";
    private static final String TB_Location_FTS = "Location_FTS";
    private static final String TB_CurrentDetails = "CurrentDetails";
    private static final String TB_WeatherDetails = "WeatherDetails";

    // Common column names
    private static final String KEY_CityID = "CityID";
    private static final String KEY_ForecastedTime = "ForecastedTime";

    private static final String KEY_Temp = "Temp";
    private static final String KEY_TempMin = "TempMin";
    private static final String KEY_TempMax = "TempMax";

    private static final String KEY_WeatherID = "WeatherID";
    private static final String KEY_MainWeather = "MainWeather";
    private static final String KEY_Description = "Description";
    private static final String KEY_IconID = "IconID";

    private static final String KEY_WindSpeed = "WindSpeed";
    private static final String KEY_WindDirection = "WindDirection";

    private static final String KEY_Humidity = "Humidity";
    /************************************************************************/


    //CityLocation Column
    private static final String KEY_Name = "Name";
    private static final String KEY_Country = "Country";
    private static final String KEY_Latitude = "Latitude";
    private static final String KEY_Longitude = "Longitude";
    private static final String KEY_IMGURL = "IMGURL";
    /************************************************************************/


    //WeatherDetails Column
    private static final String KEY_DT = "DT";
    private static final String KEY_Pressure = "Pressure";
    private static final String KEY_SeaLevelPressure = "SeaLevelPressure";
    private static final String KEY_GroundLevelPressure = "GroundLevelPressure";
    private static final String KEY_Cloudiness = "Cloudiness";
    /************************************************************************/

    //WeatherDetails Column
    private static final String KEY_ItemID = "ItemID";
    private static final String KEY_Sunset = "Sunset";
    private static final String KEY_Sunrise = "Sunrise";
    /************************************************************************/

    /**************************************************************************************/
    // Table Create Statements
    // To do table create statement
    private static final String CreateLocation_TB =
            "CREATE TABLE " + TB_Location +
                    "(" +
                    KEY_CityID + " INTEGER PRIMARY KEY NOT NULL," +
                    KEY_Name + " TEXT NOT NULL," +
                    KEY_Country + " TEXT NOT NULL," +
                    KEY_Latitude + " REAL NULL," +
                    KEY_Longitude + " REAL NULL," +
                    KEY_IMGURL + " TEXT NULL" +
                    ")";


    // Tag table create statement
    private static final String CreateWeatherDetails =
            "CREATE TABLE " + TB_WeatherDetails +
                    "(" +
                    KEY_DT + " INTEGER NOT NULL," +
                    KEY_CityID + " INTEGER NOT NULL," +
                    KEY_ForecastedTime + " NUMERIC NOT NULL," +
                    KEY_Temp + " REAL NOT NULL," +
                    KEY_TempMin + " REAL NOT NULL," +
                    KEY_TempMax + " REAL NOT NULL," +
                    KEY_Pressure + " REAL NOT NULL," +
                    KEY_SeaLevelPressure + " REAL NOT NULL," +
                    KEY_GroundLevelPressure + " REAL NOT NULL," +

                    KEY_WeatherID + " INTEGER NOT NULL," +
                    KEY_MainWeather + " TEXT NOT NULL," +
                    KEY_Description + " TEXT NOT NULL," +
                    KEY_IconID + " TEXT NOT NULL," +

                    KEY_Cloudiness + " REAL NOT NULL," +
                    KEY_WindSpeed + " REAL NOT NULL," +
                    KEY_WindDirection + " REAL NOT NULL," +
                    KEY_Humidity + " REAL NOT NULL," +

                    " PRIMARY KEY (" + KEY_DT + ", " + KEY_CityID + ")," +
                    " FOREIGN KEY (" + KEY_CityID + ") REFERENCES " + TB_Location + "(" + KEY_CityID + ")" +
                    ")";

    // todo_tag table create statement
    private static final String CreateCurrentWeather =
            "CREATE TABLE " + TB_CurrentDetails +
                    "(" +
                    KEY_ItemID + " INTEGER PRIMARY KEY autoincrement NOT NULL," +
                    KEY_CityID + " INTEGER NOT NULL," +
                    KEY_ForecastedTime + " NUMERIC NOT NULL," +

                    KEY_Temp + " REAL NOT NULL," +
                    KEY_TempMin + " REAL NOT NULL," +
                    KEY_TempMax + " REAL NOT NULL," +

                    KEY_WeatherID + " INTEGER NOT NULL," +
                    KEY_MainWeather + " TEXT NOT NULL," +
                    KEY_Description + " TEXT NOT NULL," +
                    KEY_IconID + " TEXT NOT NULL," +

                    KEY_WindSpeed + " REAL NOT NULL," +
                    KEY_WindDirection + " REAL NOT NULL," +

                    KEY_Humidity + " REAL NOT NULL," +
                    KEY_Cloudiness + " REAL NOT NULL," +

                    KEY_Sunrise + " INTEGER NOT NULL," +
                    KEY_Sunset + " INTEGER NOT NULL," +

                    " FOREIGN KEY (" + KEY_CityID + ") REFERENCES " + TB_Location + "(" + KEY_CityID + ")" +
                    ")";

    /**************************************************************************************/



    public TheWeatherDatabase(Context context) {
        super(context, DB_Name, null, DATABASE_VERSION);
        //Log.e("MYDATABASE", "Created OK");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL("DROP TABLE IF EXISTS " + TB_Location);
        db.execSQL("DROP TABLE IF EXISTS " + TB_WeatherDetails);
        db.execSQL("DROP TABLE IF EXISTS " + TB_CurrentDetails);

        db.execSQL(CreateLocation_TB);
        db.execSQL(CreateCurrentWeather);
        db.execSQL(CreateWeatherDetails);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TB_WeatherDetails);
        db.execSQL("DROP TABLE IF EXISTS " + TB_CurrentDetails);

        // create new tables
        onCreate(db);
    }

    public boolean insertLocation(CityLocation cityLocation) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CityID, cityLocation.getCityID());
        contentValues.put(KEY_Name, cityLocation.getName());
        contentValues.put(KEY_Country, cityLocation.getCountry());
        contentValues.put(KEY_Latitude, cityLocation.getLatitude());
        contentValues.put(KEY_Longitude, cityLocation.getLongitude());
        contentValues.put(KEY_IMGURL, cityLocation.getIMGURL());
        db.insertOrThrow(TB_Location, null, contentValues);
        db.close();
        Log.d("CityLocation Added", "yes");
        return true;
    }

    private Boolean location_TB_Filled = false;

    public void insertLocationArray(CityLocation[] cityLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        location_TB_Filled = false;
        //Log.d("This",cityLocation[74070].toString());
        //Log.d("This",cityLocation[74071].toString());
        db.beginTransaction();
        for (int x = 0; x < cityLocation.length; x++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_CityID, cityLocation[x].getCityID());
            contentValues.put(KEY_Name, cityLocation[x].getName());
            contentValues.put(KEY_Country, cityLocation[x].getCountry());
            contentValues.put(KEY_Latitude, cityLocation[x].getLatitude());
            contentValues.put(KEY_Longitude, cityLocation[x].getLongitude());
            db.insert(TB_Location, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        Log.d("CityLocation Added", "yes");
        setLocation_TB_Filled(true);
        db.close();
    }

    public boolean insertCurrentWeather(CurrentWeather currentWeather) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CityID, currentWeather.getCityID());
        contentValues.put(KEY_ForecastedTime, currentWeather.getForetastedTime());

        contentValues.put(KEY_Temp, currentWeather.getTemp());
        contentValues.put(KEY_TempMax, currentWeather.getTempMax());
        contentValues.put(KEY_TempMin, currentWeather.getTempMin());

        contentValues.put(KEY_WeatherID, currentWeather.getWeatherID());
        contentValues.put(KEY_MainWeather, currentWeather.getMainWeather());
        contentValues.put(KEY_Description, currentWeather.getDescription());
        contentValues.put(KEY_IconID, currentWeather.getIconID());

        contentValues.put(KEY_WindSpeed, currentWeather.getWindSpeed());
        contentValues.put(KEY_WindDirection, currentWeather.getWindDirection());

        contentValues.put(KEY_Humidity, currentWeather.getHumidity());
        contentValues.put(KEY_Cloudiness, currentWeather.getCloudLevel());

        contentValues.put(KEY_Sunrise, currentWeather.getSunrise());
        contentValues.put(KEY_Sunset, currentWeather.getSunset());

        db.insert(TB_CurrentDetails, null, contentValues);
        db.close();
        return true;
    }


    /*********************************************************************************/
    //
    public void updateLocationURL(String url, int cityID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_IMGURL, url);
        db.update(TB_Location, cv, KEY_CityID + "= ?", new String[]{cityID + ""});
        db.close();
    }

    /*********************************************************************************/
    //get Method
    public CityLocation[] geAllLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        CityLocation[] cityLocation;
        CityLocation tempCityLocation;
        String selectQuery =
                "SELECT  *" +
                        " FROM " + TB_Location;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cityLocation = new CityLocation[cursor.getCount()];
        counter = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    tempCityLocation = new CityLocation();
                    tempCityLocation.setCityID(cursor.getInt((cursor.getColumnIndex(KEY_CityID))));
                    tempCityLocation.setName((cursor.getString(cursor.getColumnIndex(KEY_Name))));
                    tempCityLocation.setCountry((cursor.getString(cursor.getColumnIndex(KEY_Country))));
                    tempCityLocation.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_Latitude)));
                    tempCityLocation.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_Longitude)));
                    tempCityLocation.setIMGURL(cursor.getString(cursor.getColumnIndex(KEY_IMGURL)));
                    cityLocation[counter] = tempCityLocation;
                    counter++;
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return cityLocation;
    }

    public CityLocation getLocation(int forCityID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery =
                "SELECT  *" +
                        " FROM " + TB_Location +
                        " WHERE " + KEY_CityID + " = " + forCityID;
        //Log.d("Querying CityLocation", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        CityLocation cityLocation = new CityLocation();
        counter = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    cityLocation = new CityLocation();
                    cityLocation.setCityID(c.getInt((c.getColumnIndex(KEY_CityID))));
                    cityLocation.setName((c.getString(c.getColumnIndex(KEY_Name))));
                    cityLocation.setCountry((c.getString(c.getColumnIndex(KEY_Country))));
                    cityLocation.setLatitude(c.getString(c.getColumnIndex(KEY_Latitude)));
                    cityLocation.setLongitude(c.getString(c.getColumnIndex(KEY_Longitude)));
                    cityLocation.setIMGURL(c.getString(c.getColumnIndex(KEY_IMGURL)));
                } while (c.moveToNext());
            }
        }
        db.close();
        return cityLocation;
    }

    public CurrentWeather getCurrentWeather(String cityID) {
        SQLiteDatabase db = this.getReadableDatabase();
        CurrentWeather currentWeather = new CurrentWeather();
        String selectQuery =
                "SELECT  *" +
                        " FROM " + TB_CurrentDetails +
                        " WHERE " + KEY_CityID + " = " + cityID;
        //Log.d("Querying CityLocation", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    currentWeather.setCityID(c.getInt((c.getColumnIndex(KEY_CityID))));
                    currentWeather.setWeatherID((c.getInt(c.getColumnIndex(KEY_WeatherID))));
                    currentWeather.setTemp(c.getDouble(c.getColumnIndex(KEY_Temp)));
                    currentWeather.setTempMin(c.getDouble(c.getColumnIndex(KEY_TempMin)));
                    currentWeather.setTempMax(c.getDouble(c.getColumnIndex(KEY_TempMax)));
                    currentWeather.setCloudLevel(c.getDouble(c.getColumnIndex(KEY_Cloudiness)));
                    currentWeather.setWindSpeed(c.getDouble(c.getColumnIndex(KEY_WindSpeed)));
                    currentWeather.setWindDirection(c.getDouble(c.getColumnIndex(KEY_WindDirection)));
                    currentWeather.setHumidity(c.getDouble(c.getColumnIndex(KEY_Humidity)));
                    currentWeather.setMainWeather(c.getString(c.getColumnIndex(KEY_MainWeather)));
                    currentWeather.setDescription(c.getString(c.getColumnIndex(KEY_Description)));
                    currentWeather.setIconID(c.getString(c.getColumnIndex(KEY_IconID)));
                    currentWeather.setForetastedTime(c.getString(c.getColumnIndex(KEY_ForecastedTime)));
                    currentWeather.setSunset(c.getInt(c.getColumnIndex(KEY_Sunset)));
                    currentWeather.setSunrise(c.getInt(c.getColumnIndex(KEY_Sunrise)));

                } while (c.moveToNext());
            }
        }
        return currentWeather;
    }

    public Weather getWeather(String cityID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Weather tempWeather = new Weather();
        String selectQuery =
                "SELECT  *" +
                        " FROM " + TB_WeatherDetails +
                        " WHERE " + KEY_CityID + " = " + cityID;
        //Log.d("Querying CityLocation", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    tempWeather.setCityID(c.getInt((c.getColumnIndex(KEY_CityID))));
                    tempWeather.setWeatherID((c.getInt(c.getColumnIndex(KEY_WeatherID))));
                    tempWeather.setDT((c.getInt(c.getColumnIndex(KEY_DT))));
                    tempWeather.setTemp(c.getDouble(c.getColumnIndex(KEY_Temp)));
                    tempWeather.setTempMin(c.getDouble(c.getColumnIndex(KEY_TempMin)));
                    tempWeather.setTempMax(c.getDouble(c.getColumnIndex(KEY_TempMax)));
                    tempWeather.setPressure(c.getDouble(c.getColumnIndex(KEY_Pressure)));
                    tempWeather.setPressureSeaLevel(c.getDouble(c.getColumnIndex(KEY_SeaLevelPressure)));
                    tempWeather.setPressureGroundLevel(c.getDouble(c.getColumnIndex(KEY_GroundLevelPressure)));
                    tempWeather.setCloudLevel(c.getDouble(c.getColumnIndex(KEY_Cloudiness)));
                    tempWeather.setWindSpeed(c.getDouble(c.getColumnIndex(KEY_WindSpeed)));
                    tempWeather.setWindDirection(c.getDouble(c.getColumnIndex(KEY_WindDirection)));
                    tempWeather.setHumidity(c.getDouble(c.getColumnIndex(KEY_Humidity)));
                    tempWeather.setMainWeather(c.getString(c.getColumnIndex(KEY_MainWeather)));
                    tempWeather.setDescription(c.getString(c.getColumnIndex(KEY_Description)));
                    tempWeather.setIconID(c.getString(c.getColumnIndex(KEY_IconID)));
                    tempWeather.setForetastedTime(c.getString(c.getColumnIndex(KEY_ForecastedTime)));

                } while (c.moveToNext());
            }
        }
        db.close();
        return tempWeather;
    }

    public Weather[] getDetailedWeather(String cityID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Weather weather[];
        Weather tempWeather = new Weather();
        String selectQuery =
                "SELECT  *" +
                        " FROM " + TB_WeatherDetails +
                        " WHERE " + KEY_CityID + " = " + cityID;
        //Log.d("Querying CityLocation", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        weather = new Weather[c.getCount()];
        if (c != null) {
            counter = 0;
            if (c.moveToFirst()) {
                do {
                    tempWeather.setCityID(c.getInt((c.getColumnIndex(KEY_CityID))));
                    tempWeather.setWeatherID((c.getInt(c.getColumnIndex(KEY_WeatherID))));
                    tempWeather.setDT((c.getInt(c.getColumnIndex(KEY_DT))));
                    tempWeather.setTemp(c.getDouble(c.getColumnIndex(KEY_Temp)));
                    tempWeather.setTempMin(c.getDouble(c.getColumnIndex(KEY_TempMin)));
                    tempWeather.setTempMax(c.getDouble(c.getColumnIndex(KEY_TempMax)));
                    tempWeather.setPressure(c.getDouble(c.getColumnIndex(KEY_Pressure)));
                    tempWeather.setPressureSeaLevel(c.getDouble(c.getColumnIndex(KEY_SeaLevelPressure)));
                    tempWeather.setPressureGroundLevel(c.getDouble(c.getColumnIndex(KEY_GroundLevelPressure)));
                    tempWeather.setCloudLevel(c.getDouble(c.getColumnIndex(KEY_Cloudiness)));
                    tempWeather.setWindSpeed(c.getDouble(c.getColumnIndex(KEY_WindSpeed)));
                    tempWeather.setWindDirection(c.getDouble(c.getColumnIndex(KEY_WindDirection)));
                    tempWeather.setHumidity(c.getDouble(c.getColumnIndex(KEY_Humidity)));
                    tempWeather.setMainWeather(c.getString(c.getColumnIndex(KEY_MainWeather)));
                    tempWeather.setDescription(c.getString(c.getColumnIndex(KEY_Description)));
                    tempWeather.setIconID(c.getString(c.getColumnIndex(KEY_IconID)));
                    tempWeather.setForetastedTime(c.getString(c.getColumnIndex(KEY_ForecastedTime)));
                    weather[counter] = tempWeather;
                    counter++;
                } while (c.moveToNext());
            }
        }
        db.close();
        return weather;
    }

/*************************************************************************/
    /**
     * REMOVE
     */
    public boolean removeLocation(int fromCityID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TB_Location, KEY_CityID + "=" + fromCityID, null) > 0;
    }


    /****
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */

    ///searc
    private CityLocation[] queryCityLocation;
    private CityLocation cityLocation = new CityLocation();
    private int counter = 0;

    public Boolean getLocation_TB_Filled() {
        return location_TB_Filled;
    }

    public void setLocation_TB_Filled(Boolean location_TB_Filled) {
        this.location_TB_Filled = location_TB_Filled;
    }

    public void dropLocationTableRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TB_Location);
    }

    public CityLocation[] getLocation(String keyWord) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery =
                "SELECT  *" +
                        " FROM " + TB_Location +
                        " WHERE " + KEY_Name + " like " + keyWord + "% " +
                        " LIMIT 10;";
        Log.d("Querying CityLocation", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        queryCityLocation = new CityLocation[c.getCount()];
        counter = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    cityLocation = new CityLocation();
                    cityLocation.setCityID(c.getInt((c.getColumnIndex(KEY_CityID))));
                    cityLocation.setName((c.getString(c.getColumnIndex(KEY_Name))));
                    cityLocation.setCountry((c.getString(c.getColumnIndex(KEY_Country))));
                    cityLocation.setLatitude(c.getString(c.getColumnIndex(KEY_Latitude)));
                    cityLocation.setLongitude(c.getString(c.getColumnIndex(KEY_Longitude)));
                    queryCityLocation[counter] = cityLocation;
                    counter++;
                } while (c.moveToNext());
            }
        }

        return queryCityLocation;
    }

}






















