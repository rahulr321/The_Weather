package com.orbot.theweather.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rahul on 19/02/2016.
 */
public class SearchDatabaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "";

    private static String DB_NAME = "myData.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;
    private static final String Location_TABLE = "CityLocation";
    private static final String KEY_CityID = "_id";
    private static final String KEY_Name = "nm";
    private static final String KEY_Country = "countryCode";

    //CREATE VIRTUAL TABLE
    private static final String FTS_VIRTUAL_TABLE = "SearchTable";
    private static final String Create_FTS_Location_TB =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                    " USING fts4(" +
                    KEY_CityID + "," +
                    KEY_Name + "," +
                    KEY_Country +
                    ")";
    private static final String Insert_FTSDATA =
            "insert  into " + FTS_VIRTUAL_TABLE +
                    " Select " +
                    KEY_CityID + "," +
                    KEY_Name + "," +
                    KEY_Country +
                    " from " + Location_TABLE;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public SearchDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 4);
        this.myContext = context;
        DB_PATH = myContext.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                //throw new Error("Error copying database");
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            File file = new File(myPath);
            if (file.exists() && !file.isDirectory())
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            //database does't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public boolean isDatabaseExist() {
        return checkDataBase();
    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + FTS_VIRTUAL_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        if (openDb) {
            if (myDataBase == null || !myDataBase.isOpen()) {
                myDataBase = getReadableDatabase();
            }

            if (!myDataBase.isReadOnly()) {
                myDataBase.close();
                myDataBase = getReadableDatabase();
            }
        }

        Cursor cursor = myDataBase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
    /***********************************************************************************************/
    /********************************************F T S**********************************************/
    /***********************************************************************************************/

    public boolean createFTSSearch() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            db.execSQL(Create_FTS_Location_TB);
            db.execSQL(Insert_FTSDATA);
            return true;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }
    /***********************************************************************************************/

    /**********************************************************************************************/
    /****************************************S E A R C H*******************************************/
    /**********************************************************************************************/
    public Cursor getLocationMatches(String query, String[] columns) {
        String selection = KEY_Name + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;

    }

    private Cursor query(String match) {
        //getProfilesCount();
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        Cursor cursor;
        if (match.equals("non")) {
            query = "SELECT  * FROM " + FTS_VIRTUAL_TABLE;
        } else {
            query = "SELECT * FROM " + FTS_VIRTUAL_TABLE + " WHERE " + KEY_Name + " match '" + match + "'";
        }
        cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor getLocation(String query) {
        return query(query);
    }

    public Cursor getLocation() {
        String countQuery = "SELECT  * FROM " + FTS_VIRTUAL_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor;
    }
    /**********************************************************************************************/
    /**********************************************************************************************/

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}