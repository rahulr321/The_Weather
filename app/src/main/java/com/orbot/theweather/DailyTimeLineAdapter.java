package com.orbot.theweather;

/**
 * Created by Rahul on 05/08/2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.pwittchen.weathericonview.WeatherIconView;
import com.orbot.theweather.model.CalendarTimeHelper;
import com.orbot.theweather.model.CityLocation;
import com.orbot.theweather.model.DailyForecast;
import com.orbot.theweather.model.ForecastDailyDetails;
import com.orbot.theweather.model.My;
import com.orbot.theweather.model.TimeZoneHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DailyTimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Boolean isCelsius = Boolean.TRUE;
    public static final int TYPE_FIRST_CARD = 0;
    public static final int TYPE_TIMELINE_CARD = 1;
    private List<ForecastDailyDetails> dailyList;
    private DailyForecast dailyForecast;
    private View.OnClickListener onClickListener;
    private Calendar cityCalendar;
    private CityLocation cityLocation;
    private static int MORN = 0, DAY = 1, EVE = 2, NIGHT = 3;
    private static String sMORN = "Morning", sDAY = "Afternoon", sEVE = "Evening", sNIGHT = "Night";

    public Boolean getCelsius() {
        return isCelsius;
    }

    public void setCelsius(Boolean celsius) {
        isCelsius = celsius;
    }

    public DailyTimeLineAdapter(List<ForecastDailyDetails> dailyList) {
        this.dailyList = dailyList;
    }

    public DailyTimeLineAdapter(List<ForecastDailyDetails> dailyList,
                                DailyForecast dailyForecast, CityLocation cityLocation) {
        this.dailyForecast = dailyForecast;
        this.dailyList = dailyList;
        this.cityLocation = cityLocation;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FIRST_CARD) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_detail_content_first,
                    parent, false);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
            return new VHFirstCard(itemView);

        } else if (viewType == TYPE_TIMELINE_CARD) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_detail_content_timeline,
                    parent, false);
            itemView.setOnClickListener(getOnClickListener());
            return new VHTimeLineCard(itemView);
        }
        throw new RuntimeException("View Type : " + viewType + ", doesn't exist!");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof VHTimeLineCard) {
            final TextView windDegree, windSpeed, humidity, pressure, textView_Time,
                    textView_TempFeh, textView_TempCel, weatherMain,
                    pressureLabel, windLabel, humidityLabel, weatherDescription, cloudLevel;
            final ForecastDailyDetails forecastDaily = getItem(position - 1);
            final WeatherIconView iconView, mainWeatherIcon;

            windDegree = ((VHTimeLineCard) holder).getWindDegree();
            windSpeed = ((VHTimeLineCard) holder).getWindSpeed();
            humidity = ((VHTimeLineCard) holder).getHumidity();
            pressure = ((VHTimeLineCard) holder).getPressure();
            textView_Time = ((VHTimeLineCard) holder).getTextView_Time();
            textView_TempFeh = ((VHTimeLineCard) holder).getTextView_TempFeh();
            textView_TempCel = ((VHTimeLineCard) holder).getTextView_TempCel();
            mainWeatherIcon = ((VHTimeLineCard) holder).getMainWeatherIcon();
            weatherMain = ((VHTimeLineCard) holder).getWeatherMain();
            weatherDescription = ((VHTimeLineCard) holder).getWeatherDescription();
            cloudLevel = ((VHTimeLineCard) holder).getCloudLevel();

            pressureLabel = ((VHTimeLineCard) holder).getPressureLabel();
            windLabel = ((VHTimeLineCard) holder).getWindLabel();
            humidityLabel = ((VHTimeLineCard) holder).getHumidityLabel();

            //weather label
            iconView = ((VHTimeLineCard) holder).getIconView();
            iconView.setRotation((float) forecastDaily.getWind().getWindDirection());

            Context context = cloudLevel.getContext();
            int resourceID = context.getResources().getIdentifier(My.
                    getWeatherResourceName(forecastDaily.getWeathr().
                            getWeatherID(),forecastDaily.getForecastedTime()), "string", context.getPackageName());
            mainWeatherIcon.setIconResource(context.getString(resourceID));

            windDegree.setText(Math.round(forecastDaily.getWind().getWindDirection()) + "");
            windSpeed.setText(Math.round(forecastDaily.getWind().getWindSpeed()) + "");
            humidity.setText(Math.round(forecastDaily.getWeatherMain().getHumidity()) + "");
            pressure.setText(Math.round(forecastDaily.getWeatherMain().getPressure()) + "");
            weatherMain.setText(forecastDaily.getWeathr().getMainWeather());
            weatherDescription.setText(forecastDaily.getWeathr().getDescription());
            cloudLevel.setText(Math.round(forecastDaily.getWeatherCloud().getCloudLevel()) + "");

            if (isCelsius) {
                textView_TempCel.setText(My.getTempToCelsius(forecastDaily.getWeatherMain().getTemp()));
                textView_TempCel.setVisibility(View.VISIBLE);
                //hide
                textView_TempFeh.setVisibility(View.GONE);
            } else {
                textView_TempFeh.setText(My.getTempToFahrenheit(forecastDaily.getWeatherMain().getTemp()));
                textView_TempFeh.setVisibility(View.VISIBLE);
                //hide
                textView_TempCel.setVisibility(View.GONE);
            }
            textView_Time.setText(String.format("%02d", forecastDaily.getForecastedTime().getHours()) +
                    ":" + String.format("%02d", forecastDaily.getForecastedTime().getMinutes()));
        } else if (holder instanceof VHFirstCard) {
            TextView textView_MainTempCelsius, textView_MainTempFahrenheit, textView_Day,
                    textView_Sunrise_Time, textViewTemp_MinCelsius, textViewTemp_MinFahrenheit,
                    textViewTemp_MaxCelsius, textViewTemp_MaxFahrenheit, textView_DayTime,
                    textView_Sunset_Time,textView_condDescription;
            WeatherIconView mainWeatherIconView, sunriseIconView, sunsetIconView;
            LinearLayout sunTimeContainer = ((VHFirstCard) holder).getSunTimeContainer();

            textView_MainTempCelsius = ((VHFirstCard) holder).getTextView_MainTempCelsius();
            textView_MainTempFahrenheit = ((VHFirstCard) holder).getTextView_MainTempFahrenheit();
            textView_Day = ((VHFirstCard) holder).getTextView_Day();
            textView_DayTime = ((VHFirstCard) holder).getTextView_DayTime();
            textView_Sunrise_Time = ((VHFirstCard) holder).getTextView_Sunrise_Time();
            textView_Sunset_Time = ((VHFirstCard) holder).getTextView_Sunset_Time();
            textViewTemp_MinCelsius = ((VHFirstCard) holder).getTextViewTemp_MinCelsius();
            textViewTemp_MinFahrenheit = ((VHFirstCard) holder).getTextViewTemp_MinFahrenheit();
            textViewTemp_MaxCelsius = ((VHFirstCard) holder).getTextViewTemp_MaxCelsius();
            textViewTemp_MaxFahrenheit = ((VHFirstCard) holder).getTextViewTemp_MaxFahrenheit();
            textView_condDescription= ((VHFirstCard) holder).getTextView_condDescription();
            mainWeatherIconView = ((VHFirstCard) holder).getMainWeatherIconView();
            sunriseIconView = ((VHFirstCard) holder).getSunriseIconView();
            sunsetIconView = ((VHFirstCard) holder).getSunsetIconView();

            if (dailyForecast.getTemp() != null) {
                int day = dailyForecast.getForecastTime().getDay();
                //calendar.setTime(weatherDay.getDay());
                final TimeZoneHelper timeZoneHelper = getCityLocation().getTimeZoneHelper();
                Calendar locationTime = getCityLocation().getLocationTime();
                CalendarTimeHelper calendarTimeHelper = new CalendarTimeHelper(locationTime);

                textView_condDescription.setText(dailyForecast.getWeathr().getMainWeather());
                textView_Day.setText(locationTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
                        getCityLocation().getLocale()));
                double mainTemp = 0;
                String timeOfDay = sDAY;
                DailyForecast.Temp temp = dailyForecast.getTemp();
                switch (getTimeForCurrentCity(calendarTimeHelper.getHour())) {
                    case 0:
                        mainTemp = temp.getMorn();
                        timeOfDay = sMORN;
                        break;
                    case 1:
                        mainTemp = temp.getDay();
                        timeOfDay = sDAY;
                        break;
                    case 2:
                        mainTemp = temp.getEve();
                        timeOfDay = sEVE;
                        break;
                    case 3:
                        mainTemp = temp.getNight();
                        timeOfDay = sNIGHT;
                        break;
                }
                Context context = textView_Day.getContext();
                int iconColor = Color.parseColor("#deffffff");
                if (cityLocation.getSunrise() != 0) {
                    Date date = new Date(cityLocation.getSunrise() * 1000L);
                    textView_Sunrise_Time.setText(date.getHours() + ":" + date.getMinutes());
                    date = new Date(cityLocation.getSunset() * 1000L);
                    textView_Sunset_Time.setText(date.getHours() + ":" + date.getMinutes());

                    sunriseIconView.setIconResource(context.getString(R.string.wi_sunrise));
                    sunriseIconView.setIconSize(20);
                    sunriseIconView.setIconColor(iconColor);
                    sunsetIconView.setIconResource(context.getString(R.string.wi_sunset));
                    sunsetIconView.setIconSize(20);
                    sunsetIconView.setIconColor(iconColor);

                } else {
                    if (sunTimeContainer != null)
                        sunTimeContainer.setVisibility(View.GONE);
                }
                textView_DayTime.setText(timeOfDay);
                int resourceID = context.getResources().getIdentifier(My.
                        getWeatherResourceName(dailyForecast.getWeathr().
                                getWeatherID(),dailyForecast.getForecastTime()), "string", context.getPackageName());
                mainWeatherIconView.setIconResource(context.getString(resourceID));
                mainWeatherIconView.setIconSize(75);
                mainWeatherIconView.setIconColor(iconColor);

                if (isCelsius) {
                    //Show
                    textViewTemp_MaxCelsius.setVisibility(View.VISIBLE);
                    textViewTemp_MinCelsius.setVisibility(View.VISIBLE);
                    textView_MainTempCelsius.setVisibility(View.VISIBLE);
                    //set data
                    textViewTemp_MinCelsius.setText(My.getTempToCelsius((dailyForecast.getTemp().getMin())));
                    textViewTemp_MaxCelsius.setText(My.getTempToCelsius((dailyForecast.getTemp().getMax())));
                    textView_MainTempCelsius.setText(My.getTempToCelsius(mainTemp));
                    //hide
                    textViewTemp_MinFahrenheit.setVisibility(View.GONE);
                    textViewTemp_MaxFahrenheit.setVisibility(View.GONE);
                    textView_MainTempFahrenheit.setVisibility(View.GONE);
                } else {
                    //show
                    textViewTemp_MinFahrenheit.setVisibility(View.VISIBLE);
                    textViewTemp_MaxFahrenheit.setVisibility(View.VISIBLE);
                    textView_MainTempFahrenheit.setVisibility(View.VISIBLE);
                    //set data
                    textViewTemp_MinFahrenheit.setText(My.getTempToFahrenheit(dailyForecast.getTemp().getMin()));
                    textViewTemp_MaxFahrenheit.setText(My.getTempToFahrenheit(dailyForecast.getTemp().getMax()));
                    textView_MainTempFahrenheit.setText(My.getTempToFahrenheit(mainTemp));
                    //hide
                    textViewTemp_MaxCelsius.setVisibility(View.GONE);
                    textViewTemp_MinCelsius.setVisibility(View.GONE);
                    textView_MainTempCelsius.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        //increasing getItemcount to 1. This will be the row of header
        return dailyList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFirstCard(position))
            return TYPE_FIRST_CARD;
        return TYPE_TIMELINE_CARD;
    }

    private boolean isPositionFirstCard(int position) {
        return position == TYPE_FIRST_CARD;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public ForecastDailyDetails getItem(int position) {
        return dailyList.get(position);
    }

    public List<ForecastDailyDetails> getDailyList() {
        return dailyList;
    }

    public void setDailyList(List<ForecastDailyDetails> dailyList) {
        this.dailyList = dailyList;
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

    //TIME FUNCTION
    /**********************************************************************************************/
    /**********************************************************************************************/
    private int getTimeForCurrentCity(int hour) {
        //Calendar locationTime = new GregorianCalendar(TimeZone.getTimeZone(getCityLocation().getTimeZone()));
        /*Date locationDate = locationTime.getTime();
        int hour = locationDate.getHours();*/
        int no = MORN;
        if (hour >= 5 && hour < 12)
            no = MORN;
        else if (hour >= 12 && hour < 17)
            no = DAY;
        else if (hour >= 17 && hour < 21)
            no = EVE;
        else if (hour >= 21 && hour < 5)
            no = NIGHT;
        return no;
    }

    public Calendar getLocationTime(CityLocation cityLocation) {
        Calendar calendar = new GregorianCalendar();
        TimeZone currentTimeZone = TimeZone.getTimeZone(cityLocation.getTimeZone());
        calendar.setTimeZone(currentTimeZone);
        return calendar;

/*
        long timeCPH = calendar.getTimeInMillis();
        System.out.println("timeCPH  = " + timeCPH);
        System.out.println("hour     = " + calendar.get(Calendar.HOUR_OF_DAY));


        long timeLA = calendar.getTimeInMillis();
        System.out.println("timeLA   = " + timeLA);
        System.out.println("hour     = " + calendar.get(Calendar.HOUR_OF_DAY));*/
    }

    //VIEW.HOLDER CLASS
    /**********************************************************************************************/
    /**********************************************************************************************/
    class VHTimeLineCard extends RecyclerView.ViewHolder {
        private TextView windDegree;
        private TextView windSpeed;
        private TextView humidity;
        private TextView pressure;
        private TextView textView_Time;
        private TextView textView_TempFeh, textView_TempCel;
        private TextView weatherMain;
        private TextView weatherDescription, cloudLevel;

        private TextView pressureLabel;
        private TextView windLabel;
        private TextView humidityLabel;

        private WeatherIconView iconView, mainWeatherIcon;
        private LinearLayout linearLayout;

        public VHTimeLineCard(final View view) {
            super(view);
            itemView.setBackgroundColor(Color.TRANSPARENT);

            //timeWeather
            linearLayout = (LinearLayout) view.findViewById(R.id.timeWeather);
            textView_Time = (TextView) view.findViewById(R.id.textView_Time);
            textView_TempFeh = (TextView) view.findViewById(R.id.textView_TempFeh);
            textView_TempCel = (TextView) view.findViewById(R.id.textView_TempCel);
            mainWeatherIcon = (WeatherIconView) view.findViewById(R.id.mainWeatherIcon);
            weatherMain = (TextView) view.findViewById(R.id.textView_WeatherMain);
            weatherDescription = (TextView) view.findViewById(R.id.textView_WeatherDescription);
            cloudLevel = (TextView) view.findViewById(R.id.textView_Clouds);

            //timeWeatherDetail
            LinearLayout frameLayout = (LinearLayout) view.findViewById(R.id.timeWeatherDetail);
            frameLayout.setVisibility(View.GONE);

            //main
            windDegree = (TextView) view.findViewById(R.id.windDeg);
            windSpeed = (TextView) view.findViewById(R.id.windSpeed);
            humidity = (TextView) view.findViewById(R.id.hum);
            pressure = (TextView) view.findViewById(R.id.press);
            // /Label
            windLabel = (TextView) view.findViewById(R.id.windLab);
            humidityLabel = (TextView) view.findViewById(R.id.humLab);
            pressureLabel = (TextView) view.findViewById(R.id.pressLab);

            iconView = (WeatherIconView) view.findViewById(R.id.windDegIcon);
        }

        public TextView getWindDegree() {
            return windDegree;
        }

        public void setWindDegree(TextView windDegree) {
            this.windDegree = windDegree;
        }

        public TextView getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(TextView windSpeed) {
            this.windSpeed = windSpeed;
        }

        public TextView getHumidity() {
            return humidity;
        }

        public void setHumidity(TextView humidity) {
            this.humidity = humidity;
        }

        public TextView getPressure() {
            return pressure;
        }

        public void setPressure(TextView pressure) {
            this.pressure = pressure;
        }

        public TextView getPressureLabel() {
            return pressureLabel;
        }

        public void setPressureLabel(TextView pressureLabel) {
            this.pressureLabel = pressureLabel;
        }

        public TextView getWindLabel() {
            return windLabel;
        }

        public void setWindLabel(TextView windLabel) {
            this.windLabel = windLabel;
        }

        public TextView getHumidityLabel() {
            return humidityLabel;
        }

        public void setHumidityLabel(TextView humidityLabel) {
            this.humidityLabel = humidityLabel;
        }

        public WeatherIconView getIconView() {
            return iconView;
        }

        public void setIconView(WeatherIconView iconView) {
            this.iconView = iconView;
        }


        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public void setLinearLayout(LinearLayout linearLayout) {
            this.linearLayout = linearLayout;
        }

        public TextView getTextView_Time() {
            return textView_Time;
        }

        public void setTextView_Time(TextView textView_Time) {
            this.textView_Time = textView_Time;
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

        public WeatherIconView getMainWeatherIcon() {
            return mainWeatherIcon;
        }

        public void setMainWeatherIcon(WeatherIconView mainWeatherIcon) {
            this.mainWeatherIcon = mainWeatherIcon;
        }

        public TextView getWeatherMain() {
            return weatherMain;
        }

        public void setWeatherMain(TextView weatherMain) {
            this.weatherMain = weatherMain;
        }

        public TextView getCloudLevel() {
            return cloudLevel;
        }

        public void setCloudLevel(TextView cloudLevel) {
            this.cloudLevel = cloudLevel;
        }

        public TextView getWeatherDescription() {
            return weatherDescription;
        }

        public void setWeatherDescription(TextView weatherDescription) {
            this.weatherDescription = weatherDescription;
        }
    }

    class VHFirstCard extends RecyclerView.ViewHolder {
        private TextView textView_MainTempCelsius, textView_MainTempFahrenheit, textView_Day,
                textView_Sunrise_Time, textView_Sunset_Time, textViewTemp_MinCelsius,
                textViewTemp_MinFahrenheit, textViewTemp_MaxCelsius, textViewTemp_MaxFahrenheit,
                textView_DayTime,textView_condDescription;

        private WeatherIconView mainWeatherIconView, sunriseIconView, sunsetIconView;
        private LinearLayout sunTimeContainer;

        public VHFirstCard(View itemView) {
            super(itemView);
            itemView.setBackgroundColor(Color.TRANSPARENT);

            textView_MainTempCelsius = (TextView) itemView.findViewById(R.id.textView_MainTempCelsius);
            textView_MainTempFahrenheit = (TextView) itemView.findViewById(R.id.textView_MainTempFahrenheit);
            textView_Day = (TextView) itemView.findViewById(R.id.textView_Day);
            textView_DayTime = (TextView) itemView.findViewById(R.id.textView_DayTime);
            textView_Sunrise_Time = (TextView) itemView.findViewById(R.id.textView_Sunrise_Time);
            textView_Sunset_Time = (TextView) itemView.findViewById(R.id.textView_Sunset_Time);
            textViewTemp_MinCelsius = (TextView) itemView.findViewById(R.id.textViewTemp_MinCelsius);
            textViewTemp_MaxCelsius = (TextView) itemView.findViewById(R.id.textViewTemp_MaxCelsius);
            textViewTemp_MinFahrenheit = (TextView) itemView.findViewById(R.id.textViewTemp_MinFahrenheit);
            textViewTemp_MaxFahrenheit = (TextView) itemView.findViewById(R.id.textViewTemp_MaxFahrenheit);
            textView_condDescription = (TextView) itemView.findViewById(R.id.textView_condDescription);


            sunTimeContainer = (LinearLayout) itemView.findViewById(R.id.sunTimeContainer);
            mainWeatherIconView = (WeatherIconView) itemView.findViewById(R.id.mainWeatherIconView);
            sunriseIconView = (WeatherIconView) itemView.findViewById(R.id.sunriseIconView);
            sunsetIconView = (WeatherIconView) itemView.findViewById(R.id.sunsetIconView);
        }

        public TextView getTextView_MainTempCelsius() {
            return textView_MainTempCelsius;
        }

        public void setTextView_MainTempCelsius(TextView textView_MainTempCelsius) {
            this.textView_MainTempCelsius = textView_MainTempCelsius;
        }

        public TextView getTextView_MainTempFahrenheit() {
            return textView_MainTempFahrenheit;
        }

        public void setTextView_MainTempFahrenheit(TextView textView_MainTempFahrenheit) {
            this.textView_MainTempFahrenheit = textView_MainTempFahrenheit;
        }

        public TextView getTextView_condDescription() {
            return textView_condDescription;
        }

        public void setTextView_condDescription(TextView textView_condDescription) {
            this.textView_condDescription = textView_condDescription;
        }

        public TextView getTextView_Day() {
            return textView_Day;
        }

        public void setTextView_Day(TextView textView_Day) {
            this.textView_Day = textView_Day;
        }

        public TextView getTextView_Sunrise_Time() {
            return textView_Sunrise_Time;
        }

        public void setTextView_Sunrise_Time(TextView textView_Sunrise_Time) {
            this.textView_Sunrise_Time = textView_Sunrise_Time;
        }

        public TextView getTextView_Sunset_Time() {
            return textView_Sunset_Time;
        }

        public void setTextView_Sunset_Time(TextView textView_Sunset_Time) {
            this.textView_Sunset_Time = textView_Sunset_Time;
        }

        public TextView getTextViewTemp_MinCelsius() {
            return textViewTemp_MinCelsius;
        }

        public void setTextViewTemp_MinCelsius(TextView textViewTemp_MinCelsius) {
            this.textViewTemp_MinCelsius = textViewTemp_MinCelsius;
        }

        public TextView getTextViewTemp_MinFahrenheit() {
            return textViewTemp_MinFahrenheit;
        }

        public void setTextViewTemp_MinFahrenheit(TextView textViewTemp_MinFahrenheit) {
            this.textViewTemp_MinFahrenheit = textViewTemp_MinFahrenheit;
        }

        public TextView getTextViewTemp_MaxCelsius() {
            return textViewTemp_MaxCelsius;
        }

        public void setTextViewTemp_MaxCelsius(TextView textViewTemp_MaxCelsius) {
            this.textViewTemp_MaxCelsius = textViewTemp_MaxCelsius;
        }

        public TextView getTextViewTemp_MaxFahrenheit() {
            return textViewTemp_MaxFahrenheit;
        }

        public void setTextViewTemp_MaxFahrenheit(TextView textViewTemp_MaxFahrenheit) {
            this.textViewTemp_MaxFahrenheit = textViewTemp_MaxFahrenheit;
        }

        public TextView getTextView_DayTime() {
            return textView_DayTime;
        }

        public void setTextView_DayTime(TextView textView_DayTime) {
            this.textView_DayTime = textView_DayTime;
        }

        public WeatherIconView getMainWeatherIconView() {
            return mainWeatherIconView;
        }

        public void setMainWeatherIconView(WeatherIconView mainWeatherIconView) {
            this.mainWeatherIconView = mainWeatherIconView;
        }

        public WeatherIconView getSunriseIconView() {
            return sunriseIconView;
        }

        public void setSunriseIconView(WeatherIconView sunriseIconView) {
            this.sunriseIconView = sunriseIconView;
        }

        public WeatherIconView getSunsetIconView() {
            return sunsetIconView;
        }

        public void setSunsetIconView(WeatherIconView sunsetIconView) {
            this.sunsetIconView = sunsetIconView;
        }

        public LinearLayout getSunTimeContainer() {
            return sunTimeContainer;
        }

        public void setSunTimeContainer(LinearLayout sunTimeContainer) {
            this.sunTimeContainer = sunTimeContainer;
        }
    }
    /**********************************************************************************************/
    //*********************************************************************************************/

}