package com.orbot.theweather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.pwittchen.weathericonview.WeatherIconView;
import com.orbot.theweather.Client.WeatherHttpClient;
import com.orbot.theweather.model.CardViewDisplay;
import com.orbot.theweather.model.CityLocation;
import com.orbot.theweather.model.DailyForecast;
import com.orbot.theweather.model.ForecastDailyDetails;
import com.orbot.theweather.model.My;
import com.orbot.theweather.model.MyJSONWeatherParser;
import com.orbot.theweather.model.Weather;
import com.orbot.theweather.util.DetailItemDivider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LocationDetailFragment extends Fragment {
    private Boolean isCelsius = Boolean.TRUE;
    final private int defaultPosition = 0;
    private static int currentLocationID;
    private CardViewDisplay cardViewDisplay;
    private CityLocation cityLocation;

    private Weather weather;
    private WeatherIconView weatherIconView;

    private TextView cityText, condDescr, tempCel, tempFeh;
    private ConstraintLayout section_land,detail_view_loading;
    private LinearLayout location_detail;
    private SimpleDraweeView imageView_backDrop;
    private Palette colorPalette;
    private Palette.Swatch currentSwatch;

    public static String PACKAGE_NAME;
    private static float colorFactor = 0.280f;

    private RecyclerView mRecycleViewDay, mRecycleViewTimeLine;
    private LinearLayoutManager mLayoutManagerDay, mLayoutManagerTimeLine;
    private DailyTimeLineAdapter dailyTimeLineAdapter;
    private WeatherDayAdapter weatherDayAdapter;
    private List<WeatherDay> weatherDays = new ArrayList<>();
    private List<ForecastDailyDetails> dailyList = new ArrayList<>();
    private DailyForecast tempDailyForecast = new DailyForecast();

    private List<DetailData> detailData = new ArrayList<>();
    private DailyForecast[] dailyForecast;
    private ForecastDailyDetails[] dailyDetailsTimeLine;

    //Visual Feedback
    private int lastClickPositionTimeLine, lastClickPositionDay = -1, firstCardWidth;
    private View lastViewClicked, firstCard;

    //Color
    private WindowManager windowManager;

    public static final String ARG_ITEM_ID = "locationID";
    public static final String ARG_WEATHER_DETAIL = "weatherDetailed";
    public static final String ARG_COLOUR_SCHEME = "colorScheme";

    private Toolbar mToolbar;
    private SharedPreferences sharedPreferences;

    private Boolean isMainDataSorted,isDayDataSorted,isImageLoaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String stringValue=getString(R.string.temp_celsius);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String keyString = sharedPreferences.getString(getString(R.string.temperature_categories_key),
                stringValue);
        isCelsius = stringValue.equals(keyString);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            currentLocationID = getArguments().getInt(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            mToolbar = activity.findViewById(R.id.detail_toolbar);
            if (mToolbar != null) {
                cardViewDisplay = (CardViewDisplay) getArguments().getSerializable(ARG_WEATHER_DETAIL);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.location_detail, null);
        this.location_detail = v.findViewById(R.id.location_detail);
        this.detail_view_loading= v.findViewById(R.id.detail_view_loading);

        //setUp Layout
        /*******************o*****************************************/
        /*component
        /RECYCLEVIEW*/
        mLayoutManagerDay = new LinearLayoutManager(getContext());
        mLayoutManagerTimeLine = new LinearLayoutManager(getContext());
        mLayoutManagerDay.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLayoutManagerTimeLine.setOrientation(LinearLayoutManager.HORIZONTAL);
        weatherDayAdapter = new WeatherDayAdapter(weatherDays);
        dailyTimeLineAdapter = new DailyTimeLineAdapter(dailyList, tempDailyForecast, cityLocation);
        mRecycleViewDay = location_detail.findViewById(R.id.day_recycler_view);
        mRecycleViewTimeLine = location_detail.findViewById(R.id.timeline_recycler_view);
        mRecycleViewTimeLine.addItemDecoration(new DetailItemDivider(getContext()));
        mRecycleViewDay.addItemDecoration(new DetailItemDivider(getContext()));
        //section three
        mRecycleViewDay.setAdapter(weatherDayAdapter);
        mRecycleViewDay.setLayoutManager(mLayoutManagerDay);
        //daily
        mRecycleViewTimeLine.setAdapter(dailyTimeLineAdapter);
        mRecycleViewTimeLine.setLayoutManager(mLayoutManagerTimeLine);

        cityText = location_detail.findViewById(R.id.cityText);
        condDescr = location_detail.findViewById(R.id.textView_condDescription);
        tempCel = location_detail.findViewById(R.id.tempCel);
        tempFeh = location_detail.findViewById(R.id.tempFeh);

        imageView_backDrop = location_detail.findViewById(R.id.backdrop_imgiew);
        weatherIconView = location_detail.findViewById(R.id.iconView);
        /******************************************************************************/

        PACKAGE_NAME = getContext().getPackageName();
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            currentLocationID = getArguments().getInt(ARG_ITEM_ID, 0);
            cardViewDisplay = (CardViewDisplay) getArguments().getSerializable(ARG_WEATHER_DETAIL);

            dailyTimeLineAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mRecycleViewTimeLine.getChildLayoutPosition(v);
                    LinearLayout linearLayoutTimeWeather = v.findViewById(R.id.timeWeather);
                    LinearLayout linearLayoutTimeWeatherDetail = v.findViewById(R.id.timeWeatherDetail);

                    //PREVIOUS
                    if (position != lastClickPositionTimeLine && lastViewClicked != null) {
                        LinearLayout previousTimeWeather = lastViewClicked.findViewById(R.id.timeWeather);
                        LinearLayout previousTimeWeatherDetail = lastViewClicked.findViewById(R.id.timeWeatherDetail);
                        previousTimeWeatherDetail.setVisibility(View.GONE);
                        int width = linearLayoutTimeWeather.getWidth(), height = linearLayoutTimeWeather.getHeight();//
                        //RecyclerView.LayoutParams.MATCH_PARENT;
                        lastViewClicked.setMinimumWidth(width);
                    }

                    //NEW CLICK
                    if (linearLayoutTimeWeatherDetail.getVisibility() == View.GONE) {
                        lastViewClicked = v;
                        lastClickPositionTimeLine = position;

                        linearLayoutTimeWeatherDetail.setMinimumWidth(firstCardWidth
                                - linearLayoutTimeWeather.getWidth());
                        linearLayoutTimeWeatherDetail.setVisibility(View.VISIBLE);

                        linearLayoutTimeWeatherDetail.invalidate();
                    } else {
                        linearLayoutTimeWeatherDetail.setVisibility(View.GONE);
                        v.setMinimumWidth(linearLayoutTimeWeather.getMinimumWidth());
                        lastClickPositionTimeLine = -1;
                        lastViewClicked = null;
                    }
                    v.invalidate();
                    mLayoutManagerTimeLine.scrollToPositionWithOffset(position, 0);
                }
            });


            if (cardViewDisplay != null && currentLocationID != 0) {
                weather = cardViewDisplay.getWeather();
                cityLocation = weather.getCityLocation();
                dailyTimeLineAdapter.setCityLocation(cityLocation);

                populateMainData();

                int resourceID = getResources().getIdentifier(
                        My.getWeatherResourceName(weather.getWeatherID()), "string", PACKAGE_NAME);

                weatherIconView.setIconResource(getString(resourceID));
                weatherIconView.setIconSize(35);
                weatherIconView.setIconColor(Color.WHITE);


                //initialise boolean for start up task which determine if action have been completed
                isImageLoaded=false;
                isDayDataSorted=false;
                isMainDataSorted=false;


                MyTaskHelper myTaskHelper = new MyTaskHelper();
                myTaskHelper.execute(currentLocationID);
            }
        }
        return v;
    }

    private class MyTaskHelper extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... Integer) {
            try {
                String data = (new WeatherHttpClient()).getDailyWeatherForecast(Integer[0] + "");
                dailyForecast = MyJSONWeatherParser.get7DailyWeather(data);

                data = (new WeatherHttpClient()).getWeatherForecast(Integer[0] + "");
                dailyDetailsTimeLine = MyJSONWeatherParser.getDailyWeather(data);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            try {
                populateMainData();
                doSectionThree();
                sortWeather2();
                loadImage();

                handleClickData(defaultPosition);
                MyLoadTask myLoadTask = new MyLoadTask();
                myLoadTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class MyLoadTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            if (isImageLoaded == true &&
                    isDayDataSorted == true &&
                    isMainDataSorted == true) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                detail_view_loading.setVisibility(View.GONE);
                location_detail.setVisibility(View.VISIBLE);
            }
            else{
                detail_view_loading.setVisibility(View.VISIBLE);
                location_detail.setVisibility(View.GONE);
            }
        }
    }

    private void sortWeather2() {
        List<ForecastDailyDetails> dailyDetail;
        int x, counter = 0, y = 0, dailyDetailNextDate, dailyDetailPreviousDate;
        Calendar dailyDetailNextDateCalendar = Calendar.getInstance(),
                dailyDetailPreviousDateCalendar, dailyDetailsTimeLineCalendar = Calendar.getInstance();


        //add daily detail
        //TOoDO PROPERLY SHORT DATA
        //add and sort data for each day
        for (x = 0; x < dailyForecast.length; x++) {
            //Log.d("dailyForecast counter", x + "");
            detailData.add(new DetailData());
            detailData.get(x).setDailyForecast(dailyForecast[x]);
            detailData.get(x).setCityLocation(cityLocation);
            dailyDetail = new ArrayList<>();
            if (x != 0) {
                dailyDetailPreviousDateCalendar = (Calendar) dailyDetailNextDateCalendar.clone();
            }
            dailyDetailNextDateCalendar.setTime(dailyForecast[x].getForecastTime());
            for (y = 0; y < dailyDetailsTimeLine.length; y++) {
                dailyDetailsTimeLineCalendar.setTime(dailyDetailsTimeLine[y].getForecastedTime());
                if (dailyDetailsTimeLineCalendar.get(Calendar.DATE) == dailyDetailNextDateCalendar.get(Calendar.DATE)) {
                    //Log.d("dailyDetailsTimeLine counter", y + "");
                    dailyDetail.add(dailyDetailsTimeLine[y]);
                }
            }
            detailData.get(x).setDailyDetailTimeline(dailyDetail);
            //Log.d("dailyForecast counter", x + "");
        }
        weatherDayAdapter.notifyDataSetChanged();
        isMainDataSorted=true;

    }

    /**********************************************************************************************************************************************/
    private void loadImage() {
        //fresco
        Uri uri = Uri.parse(weather.getCityLocation().getIMGURL());
        imageView_backDrop.setImageURI(uri);
        isImageLoaded = true;
        ImagePipeline imagepipeline = Fresco.getImagePipeline();

        ImageRequest imagerequest = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setRequestPriority(Priority.HIGH)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build();

        DataSource<CloseableReference<CloseableImage>> datasource =
                imagepipeline.fetchDecodedImage(imagerequest, getContext());

        try {
            datasource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                }

                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    if (bitmap == null) {
                        Log.d("Fresco Imzge", "bitmap data source returned success, but bitmap null.");
                        return;
                    }
                    // the bitmap provided to this method is only guaranteed to be around
                    // for the lifespan of this method. the image pipeline frees the
                    // bitmap's memory after this method has completed.
                    //
                    // this is fine when passing the bitmap to a system process as
                    // android automatically creates a copy.
                    //
                    // if you need to keep the bitmap around, look into using a
                    // basedatasubscriber instead of a basebitmapdatasubscriber.

                    else
                    {
                        //asynch method for in background
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                colorPalette = p;

                                Palette.Swatch swatch = colorPalette.getDarkMutedSwatch();
                                int x = 0;
                                do {
                                    if (swatch == null) {
                                        swatch = colorPalette.getSwatches().get(x);
                                        x++;
                                    }
                                } while (swatch == null);
                                currentSwatch = swatch;
                                doGood(swatch.getRgb());
                                fancyColour(swatch);
                            }

                        });
                    }


                }

            }, CallerThreadExecutor.getInstance());
        } finally {
            if (datasource != null) {
                datasource.close();
            }
        }
    }

    private void populateMainData() {
        if (isCelsius) {
            tempCel.setText(My.getTempToCelsius(weather.getTemp()));
            tempCel.setVisibility(View.VISIBLE);
            tempFeh.setVisibility(View.GONE);
        } else {
            tempFeh.setText(My.getTempToFahrenheit(weather.getTemp()));
            tempFeh.setVisibility(View.VISIBLE);
            tempCel.setVisibility(View.GONE);
        }
        if (mToolbar == null) {
            cityText.setText(cityLocation.getName() + "");
        } else {
            cityText.setVisibility(View.GONE);
        }
        condDescr.setText(weather.getDescription());
    }

    private void doSectionThree() {
        //final Calendar c = new GregorianCalendar(TimeZone.getTimeZone(cityLocation.getTimeZone()));
        final Calendar c = cityLocation.getLocationTime();

        //Calendar.getInstance();
        int dayOfWeek = 0;
        List list = new ArrayList();
        int weathID = 0;
        double temp = 0;

        for (int x = 0; x < dailyForecast.length; x++) {
            c.setTime(dailyForecast[x].getForecastTime());
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            if (!list.contains(dayOfWeek)) {
                list.add(dayOfWeek);

                weathID = dailyForecast[x].getWeathr().getWeatherID();
                temp = Math.round(dailyForecast[x].getTemp().getMin());

                weatherDays.add(new WeatherDay(weathID, dailyForecast[x].getForecastTime(), temp));
            }
        }
        final GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mRecycleViewDay.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    handleClickData(recyclerView.getChildAdapterPosition(child));
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        isDayDataSorted=true;
    }

    private int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    private int darker(int color, float factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }

    private void doGood(int col) {
        //Log.d("col",col+"");
        int mainColor = col;//Color.parseColor(Mtrial.hexColorToMaterial(col));
        int lightColor = lighter(mainColor, colorFactor);
        int darkColor = lighter(mainColor, colorFactor / 2);
        int[] colors = {lightColor, darkColor, mainColor};
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gd.setCornerRadius(0f);
        location_detail.setBackground(gd);
        if (mToolbar != null) {
            //mToolbar.setBackgroundColor(darkColor);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darkColor);
        }


    }

    private void fancyColour(Palette.Swatch swatch) {
        int labelColor, dataColor;
        labelColor = swatch.getTitleTextColor();
        dataColor = swatch.getBodyTextColor();

        //Value
        cityText.setTextColor(labelColor);

    }

    /**********************************************************************************************************************************************/
    /**********************************************************************************************************************************************/

    /*************
     * SECTION THREE
     ************/
    private class WeatherDay {
        private int weatherId;
        private Date day;
        private double temp;

        private WeatherDay(int weatherId, Date day, double temp) {
            this.weatherId = weatherId;
            this.day = day;
            this.temp = temp;
        }

        public int getWeatherId() {
            return weatherId;
        }

        public void setWeatherId(int weatherId) {
            this.weatherId = weatherId;
        }

        public Date getDay() {
            return day;
        }

        public void setDay(Date day) {
            this.day = day;
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        @Override
        public String toString() {
            return "WeatherDay{" +
                    "weatherId=" + weatherId +
                    ", day=" + day +
                    ", temp=" + temp +
                    '}';
        }

    }

    private class WeatherDayHolder extends RecyclerView.ViewHolder {
        private TextView weatherId, day, textView_TempCel, textView_TempFeh;
        private WeatherIconView iconView;

        private WeatherDayHolder(View view) {
            super(view);
            day = view.findViewById(R.id.textView_Day);
            textView_TempCel = view.findViewById(R.id.textView_TempCel);
            textView_TempFeh = view.findViewById(R.id.textView_TempFeh);
            iconView = view.findViewById(R.id.view_weatherIcon);
        }

        public TextView getWeatherId() {
            return weatherId;
        }

        public void setWeatherId(TextView weatherId) {
            this.weatherId = weatherId;
        }

        public TextView getDay() {
            return day;
        }

        public void setDay(TextView day) {
            this.day = day;
        }

        public TextView getTextView_TempCel() {
            return textView_TempCel;
        }

        public void setTextView_TempCel(TextView textView_TempCel) {
            this.textView_TempCel = textView_TempCel;
        }

        public TextView getTextView_TempFeh() {
            return textView_TempFeh;
        }

        public void setTextView_TempFeh(TextView textView_TempFeh) {
            this.textView_TempFeh = textView_TempFeh;
        }

        public WeatherIconView getIconView() {
            return iconView;
        }

        public void setIconView(WeatherIconView iconView) {
            this.iconView = iconView;
        }
    }

    private class WeatherDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<WeatherDay> weatherDays;
        private Calendar calendar = Calendar.getInstance();
        private int selectView;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_section_three, parent, false);
            return new WeatherDayHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            try {
                WeatherDay weatherDay = getItem(position);
                if (weatherDay != null) {
                    final TextView textViewDay, textViewTempCel, textViewTempFeh;
                    final WeatherIconView iconViewWeather;

                    textViewTempCel = ((WeatherDayHolder) holder).getTextView_TempCel();
                    textViewTempFeh = ((WeatherDayHolder) holder).getTextView_TempFeh();
                    iconViewWeather = ((WeatherDayHolder) holder).getIconView();
                    textViewDay = ((WeatherDayHolder) holder).getDay();

                    if (isCelsius) {
                        textViewTempCel.setText(My.getTempToCelsius(weatherDay.getTemp()));
                        textViewTempCel.setVisibility(View.VISIBLE);
                        textViewTempFeh.setVisibility(View.GONE);
                    } else {
                        textViewTempFeh.setText(My.getTempToFahrenheit(weather.getTemp()));
                        textViewTempFeh.setVisibility(View.VISIBLE);
                        textViewTempCel.setVisibility(View.GONE);
                    }
                    calendar.setTime(weatherDay.getDay());
                    textViewDay.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + "");

                    String iconName = "wi_owm_" + weatherDay.getWeatherId();
                    int resourceID = getResources().getIdentifier(iconName, "string", PACKAGE_NAME);

                    iconViewWeather.setIconResource(getString(resourceID));
                    iconViewWeather.setIconSize(20);
                    iconViewWeather.setIconColor(Color.WHITE);


                    LinearLayout linearLayout= (LinearLayout) iconViewWeather.getParent();
                    if(position==selectView){
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selectedDay));
                        linearLayout.setVisibility(View.INVISIBLE);
                    }else{
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.notSelectedDay));
                        linearLayout.setVisibility(View.VISIBLE);

                    }
                }
            } catch (Exception e) {
                Snackbar.make(getView(), "Unable to Process", Snackbar.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return weatherDays.size();
        }

        public int getSelectView() {
            return selectView;
        }

        public void setSelectView(int selectView) {
            this.selectView = selectView;
        }

        public WeatherDayAdapter(List<WeatherDay> myDataset) {
            weatherDays = myDataset;
        }

        public WeatherDay getItem(int position) {
            return weatherDays.get(position);
        }
    }

    /**********************************************************************************************************************************************/
    /**********************************************************************************************************************************************/

    public static class DetailData {
        private DailyForecast dailyForecast;
        private List<ForecastDailyDetails> dailyDetailTimeline;
        private CityLocation cityLocation;

        public DetailData() {
            this.dailyDetailTimeline = null;
            this.dailyForecast = null;
        }

        public DetailData(List<ForecastDailyDetails> dailyDetailTimeline, DailyForecast dailyForecast) {
            this.dailyDetailTimeline = dailyDetailTimeline;
            this.dailyForecast = dailyForecast;
        }

        public List<ForecastDailyDetails> getDailyDetailTimeline() {
            return dailyDetailTimeline;
        }

        public void setDailyDetailTimeline(List<ForecastDailyDetails> dailyDetailTimeline) {
            this.dailyDetailTimeline = dailyDetailTimeline;
        }

        public DailyForecast getDailyForecast() {
            return dailyForecast;
        }

        public void setDailyForecast(DailyForecast dailyForecast) {
            this.dailyForecast = dailyForecast;
        }

        public CityLocation getCityLocation() {
            return cityLocation;
        }

        public void setCityLocation(CityLocation cityLocation) {
            this.cityLocation = cityLocation;
        }

    }

    private void handleClickData(int position) {

        //TODO: PROPERLY ACTION SHOULD TAKE PLACE
        if (position != lastClickPositionDay) {
            //reset visual feedback for timeline
            lastClickPositionTimeLine = -1;
            lastViewClicked = null;
            //removing previous details and resetting layout
            mLayoutManagerTimeLine.removeAllViews();
            //adding selected day detail
            dailyTimeLineAdapter.setDailyList(detailData.get(position).getDailyDetailTimeline());
            dailyTimeLineAdapter.setDailyForecast(detailData.get(position).getDailyForecast());
            dailyTimeLineAdapter.setCelsius(isCelsius);
            dailyTimeLineAdapter.notifyDataSetChanged();
            //reset position to beginning
            mLayoutManagerTimeLine.scrollToPosition(0);

            RecyclerView.ViewHolder viewHolderNew = mRecycleViewDay.findViewHolderForAdapterPosition(position);

            //removing previous details and resetting layout
            weatherDayAdapter.setSelectView(position);
            weatherDayAdapter.notifyDataSetChanged();
            //mLayoutManagerDay.smoothScrollToPosition(mRecycleViewDay);//position,20);
            lastClickPositionDay = position;
            //debugg
            //showMessage("Position : " + position + " - clicked", Snackbar.LENGTH_SHORT);
        }
    }

    private void showMessage(String message, int length) {
        //Snackbar.make(getView(), message, length).show();
        if (getView() != null) {
            Snackbar snackbar = Snackbar.make(getView(), message, length);// Snackbar message

            View snackBarView = snackbar.getView();
            if (currentSwatch != null) {
                snackBarView.setBackgroundColor(darker(currentSwatch.getRgb(), colorFactor * 2)); // snackbar background color
                snackbar.setActionTextColor(currentSwatch.getBodyTextColor()); // snackbar action text color
            }
            snackbar.show();
        }
    }

    public Calendar getLocationTime() {
        Calendar calendar = new GregorianCalendar();
        TimeZone currentTimeZone = TimeZone.getTimeZone(cityLocation.getTimeZone());
        calendar.setTimeZone(currentTimeZone);
        return calendar;
    }
}


//TODO: IMPLEMENT CLOCK