package com.orbot.theweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.orbot.theweather.Client.TimeZoneHTTPClient;
import com.orbot.theweather.Client.WeatherHttpClient;
import com.orbot.theweather.model.CardViewDisplay;
import com.orbot.theweather.model.CityLocation;
import com.orbot.theweather.model.CurrentWeather;
import com.orbot.theweather.model.FlickrImage;
import com.orbot.theweather.model.JSONImageParser;
import com.orbot.theweather.model.JSONLocationParser;
import com.orbot.theweather.model.SearchDatabaseHelper;
import com.orbot.theweather.model.TimeZoneHelper;
import com.orbot.theweather.model.Weather;
import com.orbot.theweather.util.TheWeatherDatabase;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.orbot.theweather.model.MyJSONWeatherParser.getCurrentWeatherCities;
import static com.orbot.theweather.model.MyJSONWeatherParser.getWeather;
import static com.orbot.theweather.model.MyJSONWeatherParser.getWeatherCurrent;
import static com.orbot.theweather.util.AndroidUtils.isInternetAvailable;

public class LocationListActivity extends EasyLocationAppCompatActivity implements View.OnClickListener {

    //CityLocation Services
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    // Google client to interact with Google API
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    // CityLocation updates intervals in sec
    private static int UPDATE_INTERVAL = 2000000; //  33.33 min
    private static int FASTEST_INTERVAL = 600000; // 15 min

    final private String DEGREE = "\u00b0";
    final private int SEARCHRESULT = 1, SETTINGRESULTS = 2;

    private Toolbar mToolbar;

    private RecyclerView recycleViewMenuList;
    private CardAdapter cardAdapter;
    private LinearLayoutManager llm;

    private TheWeatherDatabase theWeatherDatabase;
    private SearchDatabaseHelper myDbHelper;
    private FloatingActionButton fab;

    private Boolean isFirstRun;
    private Boolean isCelsius;
    private Boolean isMiles;

    //Check for location service available;
    private boolean serviceProviderEnabled;


    private static Boolean locationState;
    private static Boolean isMyLocationOn;
    private CityLocation myCityLocation;
    private CardViewDisplay myLocation_CardView;
    private Weather myLocation_Weather;

    private boolean mTwoPane = false;
    public static Boolean FRESCO = Boolean.FALSE;
    private static final int REQUEST_CODE_LOCATION = 2;
    private SharedPreferences sharedPreferences;

    private LocationRequest locationRequest;
    private EasyLocationRequest easyLocationRequest;

    private boolean ok;

    //todo: create a function that will run one when program runs first time.
    //todo: create a way to incoporate if location is enable then get data out of it if the
    // todo: location provider is available. then handle it like any other weather card.


    private List<CardViewDisplay> PreList() {
        List<CardViewDisplay> result = new ArrayList<>();

        myLocation_CardView = new CardViewDisplay(myLocation_Weather);
        result.add(myLocation_CardView);
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        FRESCO = Boolean.TRUE;
        setContentView(R.layout.activity_location_list);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(mToolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        //LAYOUT
        /*****************************************************************************************/
        //Weather Card for Recycleview
        recycleViewMenuList = (RecyclerView) findViewById(R.id.RV_card_list);
        llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycleViewMenuList.setLayoutManager(llm);
        cardAdapter = new CardAdapter(PreList());
        recycleViewMenuList.setAdapter(cardAdapter);
        /*****************************************************************************************/

        //Search
        final Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, SEARCHRESULT);
            }
        });

        //LOCATION PROVIDER
        //Check for location service available;
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        serviceProviderEnabled = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //EASY LOCATION SERVICE
        //SET PARAMETER FOR LOCATION SERVICES
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);
        easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(3000)
                .build();
        requestLocationUpdates(easyLocationRequest);

        locationState = serviceProviderEnabled;

        //PREFERENCES VALUE
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        HandlePreferenceTask();

        //DATABASE
        theWeatherDatabase = new TheWeatherDatabase(getApplicationContext());
        myDbHelper = new SearchDatabaseHelper(getApplicationContext());

        //main
        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

        if (isFirstRun) {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();

            //PROMPT & ENABLE LOCATION PROVIDER
            requestSingleLocationFix(easyLocationRequest);
        }


        //Database
        /***************************************************************************************************/
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }


        /***********************************************************************************************************************/
        /***********************************************************************************************************************/

        //LOAD SAVED CITY  DETAILS
        CityLocation[] cityLocations = theWeatherDatabase.geAllLocation();
        for (CityLocation location : cityLocations) {
            MyStartUPWeatherTask myAddTask = new MyStartUPWeatherTask();
            myAddTask.execute(location);
        }

        //LOAD MY-LOCATION PROCESS IF ENABLE

        if (isMyLocationOn) {
            requestLocationUpdates(easyLocationRequest);
        }


        //
        //
        //
        cardAdapter.setOnClickListener(this);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallBack = new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.ACTION_STATE_IDLE) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();

                //don't allow location based to change order
                if (viewHolder.getItemViewType() != target.getItemViewType()) {
                    Snackbar.make(fab, "This functionality is not available yet", Snackbar.LENGTH_SHORT).show();
                    return false;
                } else {
                    // Notify the adapter of the move
                    cardAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    Log.d("Card Move", "Item" + cardAdapter.getItem(viewHolder.getAdapterPosition()).getWeather().getCityLocation().getName() + " moved from " + fromPos + " to " + toPos);
                    return true;// true if moved, false otherwise
                }
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!(viewHolder instanceof CardAdapter.VHCARD_MyLocation)) {
                    // Set movement flags based on the layout manager
                    if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT;
                        final int swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                        return makeMovementFlags(dragFlags, swipeFlags);
                    } else {
                        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                        final int swipeFlags = ItemTouchHelper.RIGHT;
                        return makeMovementFlags(dragFlags, swipeFlags);
                    }
                } else {
                    return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.ACTION_STATE_IDLE);
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (position != 0) {
                    final CardViewDisplay cardViewDisplay = new CardViewDisplay(cardAdapter.getItem(position).getWeather());
                    final CityLocation cityLocation = cardAdapter.getItem(position).getWeather().getCityLocation();
                    if (viewHolder.getItemViewType() != CardAdapter.TYPE_MY_LOCATION) {
                        theWeatherDatabase.removeLocation(cardViewDisplay.getWeather().getCityID());
                        cardAdapter.removeItem(position);

                        Snackbar.make(fab, "Card for " + cityLocation.getName() + " has been Deleted.", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    theWeatherDatabase.insertLocation(cityLocation);
                                    cardAdapter.addItemTo(position, cardViewDisplay);
                                } catch (Exception e) {
                                    Snackbar.make(fab, "Unable to add Location", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                    }
                } else {
                    recycleViewMenuList.invalidate();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(recycleViewMenuList);

        if (findViewById(R.id.location_detail_container) != null) {
            mTwoPane = true;
        }
        cardAdapter.setTwoPane(mTwoPane);
    }

    // Check Wi-Fi is on
    boolean confirmWiFiAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isAvailable();
    }

    // Check Airplane Mode - we want airplane mode off
    boolean confirmAirplaneModeOff(Context context) {
        int airplaneSetting =
                Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        return airplaneSetting == 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //// TODO: Add better refresh functionality
            //// Todo: better setting page functionality
            case R.id.action_refresh:
                //load item from database
                // check for update online and update content on database and display
                Snackbar.make(fab, "Testing Refresh Action", Snackbar.LENGTH_LONG).show();
                int totalCities = cardAdapter.getCardList().size();
                String[] cities = new String[totalCities];

                for (int y = 0; y < totalCities; y++) {
                    if (cardAdapter.getCardList().get(0).getWeather() != null) {
                        cities[y] = cardAdapter.getCardList().get(y).getWeather().getCityID() + "";
                    }
                }
                UpdateCard updateCard = new UpdateCard();
                updateCard.execute(cities);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, SETTINGRESULTS);
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    //GET PREFERENCE WHEN APP STARTS
    private void HandlePreferenceTask() {
        String stringValue = getString(R.string.temp_celsius);

        String keyString = sharedPreferences.getString(getString(R.string.temperature_categories_key),
                stringValue);
        if (stringValue.equals(keyString)) {
            isCelsius = true;
        } else {
            isCelsius = false;
        }
        cardAdapter.setCelsius(isCelsius);

        //WIND SPEED
        stringValue = getString(R.string.wind_miles);
        keyString = sharedPreferences.getString(getString(R.string.wind_speed_categories_key),
                stringValue);
        if (stringValue.equals(keyString)) {
            isMiles = true;
        } else {
            isMiles = false;
        }
        cardAdapter.setMiles(isMiles);
        cardAdapter.notifyDataSetChanged();

        //PROVIDE CURRENT LOCATION WEATHER DETAILS TOGGLE
        isMyLocationOn = sharedPreferences.getBoolean(getString(R.string.location_categories_key),
                true);
        if (!locationState) {
            isMyLocationOn = false;
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean(getString(R.string.location_categories_key),
                        isMyLocationOn).apply();

        if (isMyLocationOn) {
            //turn on my location and carry out it task
            CardViewDisplay cardViewDisplay = new CardViewDisplay(new CurrentWeather());
            myLocation_CardView = cardViewDisplay;
            cardAdapter.getCardList().set(0, myLocation_CardView);
            cardAdapter.notifyDataSetChanged();

        }
    }

    //LISTEN FOR CURRENT LOCATION  WEATHER ON OR OFF
    @Override
    public void onClick(View v) {
        int viewID = v.getId();

        switch (viewID) {

            //SWITCH FOR LOCATION TOGGLE
            case R.id.imageView_LocationToggle:
                if (locationState) {
                    //TOGGLE CURRENT LOCATION WEATHER PROVIDER
                    if (isMyLocationOn) {
                        // IF CLICKED AND IT'S ON THEN TURN IT OFF AND REMOVE THE DETAILS
                        // FROM LIST AND CARD VIEW
                        isMyLocationOn = false;
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                .putBoolean(getString(R.string.location_categories_key),
                                        false).apply();

                        CardViewDisplay cardViewDisplay = new CardViewDisplay(new CurrentWeather());
                        myLocation_CardView = cardViewDisplay;
                        cardAdapter.getCardList().set(0, myLocation_CardView);
                        cardAdapter.getItem(0);
                        cardAdapter.notifyDataSetChanged();
                        Snackbar.make(v, "Location : " + isMyLocationOn, Snackbar.LENGTH_SHORT).show();
                    } else {
                        isMyLocationOn = true;
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                .putBoolean(getString(R.string.location_categories_key),
                                        true).apply();

                        requestSingleLocationFix(easyLocationRequest);
                    }
                } else {
                    Snackbar.make(v, "Location Provider is Disabled", Snackbar.LENGTH_LONG).show();
                }
                break;

            //CLICKING CARD WILL OPEN DETAILED VIEW
            case R.id.myCardView:
                if (isMyLocationOn) {
                    final Intent intent = new Intent(v.getContext(),
                            LocationDetailActivity.class);

                    int currentPos = recycleViewMenuList.getChildAdapterPosition(v);
                    boolean tempErrorNotOpening = false;

                    //CHECK IF LAST LOCATION IS AVAILABLE OR NOT OTHERWISE START LOCATION UPDATE
                    if (mLastLocation != null) {

                        if (cardAdapter.getItem(currentPos).getWeather().getCityID() != 0) {
                            if (mTwoPane) {
                                Bundle arguments = new Bundle();
                                arguments.putInt(LocationDetailFragment.ARG_ITEM_ID,
                                        cardAdapter.getItem(currentPos).getWeather().getCityID());
                                arguments.putSerializable(LocationDetailFragment
                                        .ARG_WEATHER_DETAIL, cardAdapter.getItem(currentPos));
                                arguments.putSerializable(LocationDetailFragment
                                        .ARG_COLOUR_SCHEME, v.getDrawingCacheBackgroundColor() + "");

                                LocationDetailFragment fragment = new LocationDetailFragment();
                                fragment.setArguments(arguments);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.location_detail_container, fragment)
                                        .commit();
                            } else {
                                if (cardAdapter.getItem(currentPos).getWeather().getCityID() != 0) {
                                    Context context = v.getContext();
                                    intent.putExtra(LocationDetailFragment.ARG_ITEM_ID, cardAdapter
                                            .getItem(currentPos).getWeather().getCityID());
                                    intent.putExtra(LocationDetailFragment.ARG_WEATHER_DETAIL,
                                            cardAdapter.getItem(currentPos));

                                    context.startActivity(intent);
                                } else {
                                    tempErrorNotOpening = true;
                                }
                            }
                        } else {
                            tempErrorNotOpening = true;
                        }
                    } else {
                        requestLocationUpdates(easyLocationRequest);
                    }


                    if (tempErrorNotOpening) {
                        Snackbar.make(v, "Unable to open weather details", Snackbar.LENGTH_SHORT).show();
                    }
                } /*else {
                    if (locationState) {
                        requestSingleLocationFix(easyLocationRequest);
                        Snackbar.make(v, "Getting data for current location", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(v, "Location Provider is Disabled", Snackbar.LENGTH_SHORT).show();
                    }
                }*/
                break;
        }
    }

    //LOAD WEATHER CARD ON START UP
    private class MyStartUPWeatherTask extends AsyncTask<CityLocation, Void, CurrentWeather> {
        private CardViewDisplay cardViewDisplay;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CurrentWeather doInBackground(CityLocation... cityLocations) {
            CityLocation cityLocation = cityLocations[0];

            CurrentWeather currentWeather = new CurrentWeather();
            try {
                if (isInternetAvailable(getApplicationContext())) {
                    //FileNotFoundException
                    String data = ((new WeatherHttpClient()).getWeatherDataCityID(cityLocation.getCityID() + ""));
                    currentWeather = getWeatherCurrent(data);
                    currentWeather.getCityLocation().setIMGURL(cityLocation.getIMGURL());

                    //getUpdated TimeZone
                    data = ((new TimeZoneHTTPClient()).getLocationTimeZone(cityLocation.getLatitude(),
                            cityLocation.getLongitude(), currentWeather.getDT()));
                    TimeZoneHelper timeZoneHelper = new TimeZoneHelper(data);

                    currentWeather.getCityLocation().setTimeZone(timeZoneHelper.getTimeZoneId());
                    currentWeather.getCityLocation().setTimeZoneHelper(timeZoneHelper);

                    //Log.d("Timezone", timeZoneHelper.toString());
                } else if (currentWeather.getMainWeather() == "") {
                    currentWeather = theWeatherDatabase.getCurrentWeather(cityLocation.getCityID() + "");
                    currentWeather.setCityLocation(cityLocation);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return currentWeather;
        }

        @Override
        protected void onPostExecute(CurrentWeather currentWeather) {
            super.onPostExecute(currentWeather);
            try {
                cardViewDisplay = new CardViewDisplay(currentWeather);
                cardAdapter.addItem(cardViewDisplay);
                if (isInternetAvailable(getApplicationContext())) {
                    theWeatherDatabase.insertCurrentWeather(currentWeather);
                } else {
                    Snackbar.make(fab, "Unable to connect, check your Internet connection", Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("Adding New City", e.toString() + e.getLocalizedMessage());
                //Toast.makeText(LocationListActivity.this, e.toString() + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(fab.getContext(), "Unable to retrieve latest information", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void callAsynchronousTask(CityLocation cityLocation) {
        final CityLocation locatn = cityLocation;
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            MyStartUPWeatherTask myAddTask = new MyStartUPWeatherTask();
                            myAddTask.execute(locatn);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 250); //execute in every 50000 ms
    }

    //LOCATION

    /**********************************************************************************************/

    @Override
    public void onLocationPermissionGranted() {
        //fetch location detail and get weather data
        //show weather update

    }

    @Override
    public void onLocationPermissionDenied() {
        //show message to enable location permission for app,
        // then load details for other weather card
    }

    @Override
    public void onLocationReceived(Location location) {
        //check current location with new location then
        //if new update
        //else check last update time
        //if soon then don't do anything
        //else update
        mLastLocation = location;
        new RetrieveMyLocation().execute();


    }

    @Override
    public void onLocationProviderEnabled() {
        locationState = true;
        requestLocationUpdates(easyLocationRequest);
        //update data an location
    }

    @Override
    public void onLocationProviderDisabled() {
        //ask user to switch on location
        locationState = false;


    }

    /*********************************************************************************************/


    //UPDATE

    /*********************************************************************************************/
    private class UpdateCard extends AsyncTask<String, CurrentWeather, CurrentWeather[]> {

        @Override
        protected CurrentWeather[] doInBackground(String... params) {
            String[] cityIDs = params;
            String data = (new WeatherHttpClient()).getWeatherDataCityIDs(cityIDs);
            CurrentWeather[] currentCitiesWeather = new CurrentWeather[cityIDs.length];
            try {
                currentCitiesWeather = getCurrentWeatherCities(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return currentCitiesWeather;
        }

        @Override
        protected void onPostExecute(CurrentWeather[] currentWeathers) {
            super.onPostExecute(currentWeathers);
            try {
                List<CardViewDisplay> cardList = cardAdapter.getCardList();
                for (int x = 0; x < currentWeathers.length; x++) {
                    for (int y = 0; y < cardList.size(); y++) {
                        //if (currentWeathers[x].getCityID() == cardList.get(y).getCurrentWeather().getCityID()) {
                        //cardList.get(y).setCurrentWeather(currentWeathers[x]);
                        //}
                    }
                }

                cardAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                Toast toast = Toast.makeText(fab.getContext(), ex.getLocalizedMessage(), Toast.LENGTH_LONG);
                toast.show();
                //Snackbar.make(fab, "Location Access is Disabled", Snackbar.LENGTH_LONG).show();

            }

        }


    }
    /*********************************************************************************************/


    //ADD NEW CITY

    /*********************************************************************************************/
    private class MyAddTask extends AsyncTask<String, Void, CurrentWeather> {
        private CardViewDisplay cardViewDisplay;
        private boolean errorOccured = false;
        private Exception errorException = null;

        @Override
        protected CurrentWeather doInBackground(String... params) {
            //param is the myLocationCordinate's name that is being process;
            CurrentWeather currentWeather = new CurrentWeather();
            try {
                String data = (new WeatherHttpClient()).getWeatherDataCityID(params[0]);
                currentWeather = getWeatherCurrent(data);

                //Image
                //the searchData for CityLocation and Criteria
                JSONImageParser jsonImageParser = new JSONImageParser();
                data = jsonImageParser.getImageDataFor(JSONImageParser.FLICKERIMAGEREQUESTDATA, currentWeather.getCityLocation().getName(), " Landscape");

                //imageData for first Image from search result
                FlickrImage flickrImage = jsonImageParser.getFlickrImageData(data);
                data = jsonImageParser.getFlickrImageURL(flickrImage.getFarmID(), flickrImage.getServerID(), flickrImage.getID(), flickrImage.getSecret());
                //setImage URL
                currentWeather.getCityLocation().setIMGURL(data);

                //get TimeZone
                data = ((new TimeZoneHTTPClient()).getLocationTimeZone(currentWeather.getCityLocation().getLatitude(), currentWeather.getCityLocation().getLongitude(), currentWeather.getDT()));
                TimeZoneHelper timeZoneHelper = new TimeZoneHelper(data);
                currentWeather.getCityLocation().setTimeZone(timeZoneHelper.getTimeZoneId());
                currentWeather.getCityLocation().setTimeZoneHelper(timeZoneHelper);
                Log.d("Timezone", timeZoneHelper.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                errorOccured = true;
            } catch (Exception e) {
                e.printStackTrace();
                errorOccured = true;
                errorException = e;
            }
            return currentWeather;
        }

        @Override
        protected void onPostExecute(CurrentWeather weathr) {
            super.onPostExecute(weathr);
            try {
                theWeatherDatabase.insertLocation(weathr.getCityLocation());
                theWeatherDatabase.insertCurrentWeather(weathr);
                cardViewDisplay = new CardViewDisplay(weathr);
                cardAdapter.addItem(cardViewDisplay);
                cardAdapter.notifyDataSetChanged();

                if (errorOccured) {
                    Snackbar.make(fab, "Error : " + errorException.toString() + errorException.getLocalizedMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            } catch (SQLiteException e) {
                Snackbar.make(fab, "Card for " + weathr.getCityLocation().getName() + " already exist.", Snackbar.LENGTH_LONG).setAction("OK", null).show();
                e.printStackTrace();
            } catch (Exception e) {
                Snackbar.make(fab, "EXCEPTION : " + e.toString() + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            } catch (Throwable throwable) {
                Snackbar.make(fab, "Throwable :MyAdd : " + throwable.toString() + throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCHRESULT) {
            if (resultCode == RESULT_OK) {
                String cityName = data.getStringExtra("cityID");
                if (cityName != null) {
                    MyAddTask myAddTask = new MyAddTask();
                    myAddTask.execute(cityName);
                }
            }
        } else if (requestCode == SETTINGRESULTS) {
            HandlePreferenceTask();
        }
    }

    //PROCESS LOCATION UPDATE
    private class RetrieveMyLocation extends AsyncTask<Void, Void, Boolean> {
        private Exception exception;

        protected Boolean doInBackground(Void... urls) {
            String data;
            try {
                JSONImageParser jsonImageParser = new JSONImageParser();
                FlickrImage flickrImage;
                data = (new WeatherHttpClient()).getWeatherDataForCoordinate(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                myLocation_Weather = getWeather(data);
                myCityLocation = JSONLocationParser.getLocation(data);
                myLocation_Weather.setCityLocation(myCityLocation);

                //Image
                //the searchData for CityLocation and Criteria
                data = jsonImageParser.getImageDataFor(JSONImageParser.FLICKERIMAGEREQUESTDATA, myLocation_Weather.getCityLocation().getName(), " Landscape");
                //imageData for first Image from search result
                flickrImage = jsonImageParser.getFlickrImageData(data);
                data = jsonImageParser.getFlickrImageURL(flickrImage.getFarmID(), flickrImage.getServerID(), flickrImage.getID(), flickrImage.getSecret());
                //setImage URL
                myLocation_Weather.getCityLocation().setIMGURL(data);

                //getUpdated TimeZone
                data = ((new TimeZoneHTTPClient()).getLocationTimeZone(myCityLocation.getLatitude(), myCityLocation.getLongitude(), myLocation_Weather.getDT()));

                TimeZoneHelper timeZoneHelper = new TimeZoneHelper(data);
                myCityLocation.setTimeZone(timeZoneHelper.getTimeZoneId());
                myCityLocation.setTimeZoneHelper(timeZoneHelper);

                Log.d("Timezone", timeZoneHelper.toString());
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                this.exception = e;
                return false;
            }
        }

        protected void onPostExecute(Boolean ok) {
            if (ok) {
                myLocation_Weather.setCityLocation(myCityLocation);
                myLocation_CardView = cardAdapter.getItem(0);
                myLocation_CardView.setWeather(myLocation_Weather);
                cardAdapter.notifyItemChanged(0);
            } else {
                Snackbar.make(fab, "Unable to refresh data for your location.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    /*********************************************************************************************/

}

































