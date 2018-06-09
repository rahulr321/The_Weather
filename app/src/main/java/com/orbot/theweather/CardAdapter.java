package com.orbot.theweather;

/**
 * Created by Rahul on 05/08/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.orbot.theweather.model.CardViewDisplay;
import com.orbot.theweather.model.My;
import com.orbot.theweather.model.Weather;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final private static String TAG = "CardAdaptor";
    private Boolean Celsius = Boolean.TRUE, Miles = Boolean.TRUE;
    public static final int TYPE_MY_LOCATION = 0, TYPE_CARD = 1;
    private View.OnClickListener onClickListener;
    private List<CardViewDisplay> cardList;
    private static Boolean mTwoPane;

    public Boolean getCelsius() {
        return Celsius;
    }

    public void setCelsius(Boolean celsius) {
        Celsius = celsius;
    }

    public Boolean getMiles() {
        return Miles;
    }

    public void setMiles(Boolean miles) {
        Miles = miles;
    }

    public List<CardViewDisplay> getCardList() {
        return cardList;
    }

    public void setCardList(List<CardViewDisplay> cardList) {
        this.cardList = cardList;
    }

    private RecyclerView.OnItemTouchListener mListener;

    CardAdapter() {
        this.cardList = null;
    }

    public CardAdapter(List<CardViewDisplay> cardList) {
        this.cardList = cardList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate layout and pass it to view holder
        if (viewType == TYPE_CARD) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list, parent, false);
            return new VHCARD(itemView);
        } else if (viewType == TYPE_MY_LOCATION) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_my_location, parent, false);
            itemView.setOnClickListener(getOnClickListener());

            return new VHCARD_MyLocation(itemView);
        }
        throw new RuntimeException("View Type : " + viewType + ", doesn't exist!");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            CardViewDisplay ci = getItem(position);
            Weather weather = ci.getWeather();
            Context context = holder.itemView.getContext();
            int resourceID;
            if (weather != null) {
                resourceID = context.getResources().getIdentifier(
                        My.getWeatherResourceName(weather.getWeatherID()), "string", context.getPackageName());

                if (holder instanceof VHCARD) {
                    final RecyclerView.ViewHolder viewHolder = holder;
                    final TextView cityText, conDesc, tempCel, tempFeh;
                    final WeatherIconView weatherIconView;
                    RelativeLayout rlCardLoad = ((VHCARD) holder).getRL_card_load();

                    weatherIconView = ((VHCARD) holder).getWeatherIconView();
                    tempCel = ((VHCARD) holder).getTempCel();
                    tempFeh = ((VHCARD) holder).getTempFeh();
                    conDesc = ((VHCARD) holder).getCondDescr();
                    cityText = ((VHCARD) holder).getCityText();

                    weatherIconView.setIconResource(context.getResources().getString(resourceID));
                    weatherIconView.setIconSize(30);
                    weatherIconView.setIconColor(Color.WHITE);

                    cityText.setText(weather.getCityLocation().getName());
                    conDesc.setText(weather.getDescription());
                    if (Celsius) {
                        tempCel.setText(My.getTempToCelsius(weather.getTemp()));
                        tempCel.setVisibility(View.VISIBLE);
                        tempFeh.setVisibility(View.GONE);
                    } else {
                        tempFeh.setText(My.getTempToFahrenheit(weather.getTemp()));
                        tempFeh.setVisibility(View.VISIBLE);
                        tempCel.setVisibility(View.GONE);
                    }
                    //Fresco
                    Uri uri = Uri.parse(weather.getCityLocation().getIMGURL());
                    SimpleDraweeView draweeView = ((VHCARD) holder).backDrop;
                    draweeView.setImageURI(uri);


                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                    ImageRequest imageRequest = ImageRequestBuilder
                            .newBuilderWithSource(uri)
                            .setRequestPriority(Priority.HIGH)
                            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                            .build();

                    DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchImageFromBitmapCache(imageRequest, viewHolder.itemView.getContext());

                    try {
                        dataSource.subscribe(new BaseBitmapDataSubscriber() {
                            @Override
                            public void onNewResultImpl(@Nullable Bitmap bitM) {
                                if (bitM == null) {
                                    Log.d("BITMAPSSS", "Bitmap data source returned success, but bitmap null.");
                                    return;
                                } else {
                                    Object[] object = {viewHolder, bitM, position};

                                    MyTaskHelper myTaskHelper = new MyTaskHelper();
                                    myTaskHelper.execute(object);
                                }
                            }

                            @Override
                            public void onFailureImpl(DataSource dataSource) {
                                // No cleanup required here
                            }
                        }, CallerThreadExecutor.getInstance());
                    } catch (Exception e) {
                        Log.d("MyCardAdapter", e.toString() + e.getLocalizedMessage());
                        e.printStackTrace();
                    } finally {
                        if (dataSource != null) {
                            dataSource.close();
                        }
                    }

                    rlCardLoad.setVisibility(RelativeLayout.GONE);

                    rlCardLoad = ((VHCARD) holder).getRL_card_info();
                    rlCardLoad.setVisibility(RelativeLayout.VISIBLE);
                } else if (holder instanceof VHCARD_MyLocation) {
                    final RecyclerView.ViewHolder viewHolder = holder;
                    final TextView cityText, conDesc, tempCel, tempFeh;
                    final WeatherIconView weatherIconView;
                    RelativeLayout rlCardLoad = ((VHCARD_MyLocation) holder).getRL_card_load();
                    //CurrentWeather currentWeather = ci.getCurrentWeather();

                    weatherIconView = ((VHCARD_MyLocation) holder).getWeatherIconView();
                    tempCel = ((VHCARD_MyLocation) holder).getTempCel();
                    tempFeh = ((VHCARD_MyLocation) holder).getTempFeh();
                    conDesc = ((VHCARD_MyLocation) holder).getCondDescr();
                    cityText = ((VHCARD_MyLocation) holder).getCityText();

                    weatherIconView.setIconResource(viewHolder.itemView.getResources().getString(resourceID));
                    weatherIconView.setIconSize(30);
                    weatherIconView.setIconColor(Color.WHITE);

                    cityText.setText(weather.getCityLocation().getName() + "");
                    conDesc.setText(weather.getDescription());

                    if (Celsius) {
                        tempCel.setText(My.getTempToCelsius(weather.getTemp()));
                        tempCel.setVisibility(View.VISIBLE);
                        tempFeh.setVisibility(View.GONE);
                    } else {
                        tempFeh.setText(My.getTempToFahrenheit(weather.getTemp()));
                        tempFeh.setVisibility(View.VISIBLE);
                        tempCel.setVisibility(View.GONE);
                    }

                    //Fresco
                    Uri uri = Uri.parse(weather.getCityLocation().getIMGURL());
                    SimpleDraweeView draweeView = ((VHCARD_MyLocation) holder).backDrop;
                    draweeView.setImageURI(uri);


                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                    ImageRequest imageRequest = ImageRequestBuilder
                            .newBuilderWithSource(uri)
                            .setRequestPriority(Priority.HIGH)
                            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                            .build();

                    DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, viewHolder.itemView.getContext());

                    try {
                        dataSource.subscribe(new BaseBitmapDataSubscriber() {
                            @Override
                            public void onNewResultImpl(@Nullable Bitmap bitM) {
                                if (bitM == null) {
                                    Log.d("BITMAPSSS", "Bitmap data source returned success, but bitmap null.");
                                    return;
                                } else {
                                    Object[] object = {viewHolder, bitM, position};

                                    MyTaskHelper myTaskHelper = new MyTaskHelper();
                                    myTaskHelper.execute(object);

                                }
                            }

                            @Override
                            public void onFailureImpl(DataSource dataSource) {
                                // No cleanup required here
                            }
                        }, CallerThreadExecutor.getInstance());
                    } catch (Exception e) {
                        Log.d("MyCardAdapterBitmapLoad", e.toString() + e.getLocalizedMessage());
                        e.printStackTrace();
                    } finally {
                        if (dataSource != null) {
                            dataSource.close();
                        }
                    }

                    rlCardLoad.setVisibility(RelativeLayout.GONE);

                    rlCardLoad = ((VHCARD_MyLocation) holder).getRL_card_info();
                    rlCardLoad.setVisibility(RelativeLayout.VISIBLE);
                }
            }
        } catch (Exception e) {
            Log.d("MyCardAdapter", e.toString() + e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_MY_LOCATION;
        return TYPE_CARD;
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    private boolean isPositionHeader(int position) {
        return position == TYPE_MY_LOCATION;
    }

    public CardViewDisplay getItem(int position) {
        return cardList.get(position);
    }

    public void addItemList(List<CardViewDisplay> cardList) {
        this.cardList.addAll(cardList);
    }

    public void addItem(CardViewDisplay cardList) {
        this.cardList.add(cardList);
        notifyDataSetChanged();
    }

    public void addItemTo(int position, CardViewDisplay cardList) {
        this.cardList.add(position, cardList);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        this.cardList.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }

    public void setTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }
    /**************************************************************************/
    /**************************************************************************/

    //CLASS
    /*************************************************************************************************/
    /*************************************************************************************************/
    class VHCARD extends RecyclerView.ViewHolder {
        private TextView cityText, condDescr, tempCel, tempFeh;
        private SimpleDraweeView backDrop;
        private RelativeLayout RL_card_info;
        private RelativeLayout RL_card_load;
        private WeatherIconView weatherIconView;

        public VHCARD(final View view) {
            super(view);
            CardView cardView = (CardView) view;
            cardView.setUseCompatPadding(true);
            cityText = (TextView) view.findViewById(R.id.cityText);
            condDescr = (TextView) view.findViewById(R.id.textView_condDescription);
            tempCel = (TextView) view.findViewById(R.id.tempCel);
            tempFeh = (TextView) view.findViewById(R.id.tempFeh);
            backDrop = (SimpleDraweeView) view.findViewById(R.id.backDrop);

            weatherIconView = (WeatherIconView) view.findViewById(R.id.weatherIconView);

            RL_card_info = (RelativeLayout) view.findViewById(R.id.RL_Card_Info);
            RL_card_load = (RelativeLayout) view.findViewById(R.id.RL_Card_Load);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    int currentPos = getLayoutPosition();

                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(LocationDetailFragment.ARG_ITEM_ID, cardList.get(currentPos).getWeather().getCityID());
                        arguments.putSerializable(LocationDetailFragment.ARG_WEATHER_DETAIL, cardList.get(currentPos));

                        if (cardList.get(currentPos).getWeather().getCityID() != 0) {
                            LocationDetailFragment fragment = new LocationDetailFragment();
                            fragment.setArguments(arguments);
                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.location_detail_container, fragment)
                                    .commit();
                        } else {
                            Snackbar.make(v, R.string.unable_to_open, Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        if (cardList.get(currentPos).getWeather().getCityID() != 0) {
                            Intent intent = new Intent(context, LocationDetailActivity.class);
                            intent.putExtra(LocationDetailFragment.ARG_ITEM_ID, cardList.get(currentPos).getWeather().getCityID());
                            intent.putExtra(LocationDetailFragment.ARG_WEATHER_DETAIL, cardList.get(currentPos));

                            context.startActivity(intent);
                        } else {
                            Snackbar.make(v, R.string.unable_to_open, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });


        }

        public TextView getCityText() {
            return cityText;
        }

        public void setCityText(TextView cityText) {
            this.cityText = cityText;
        }

        public TextView getCondDescr() {
            return condDescr;
        }

        public void setCondDescr(TextView condDescr) {
            this.condDescr = condDescr;
        }

        public TextView getTempCel() {
            return tempCel;
        }

        public void setTempCel(TextView tempCel) {
            this.tempCel = tempCel;
        }

        public TextView getTempFeh() {
            return tempFeh;
        }

        public void setTempFeh(TextView tempFeh) {
            this.tempFeh = tempFeh;
        }

        public void setWeatherIconView(WeatherIconView weatherIconView) {
            this.weatherIconView = weatherIconView;
        }

        public SimpleDraweeView getBackDrop() {
            return backDrop;
        }

        public void setBackDrop(SimpleDraweeView backDrop) {
            this.backDrop = backDrop;
        }

        public RelativeLayout getRL_card_info() {
            return RL_card_info;
        }

        public RelativeLayout getRL_card_load() {
            return RL_card_load;
        }

        public WeatherIconView getWeatherIconView() {
            return weatherIconView;
        }
    }

    class VHCARD_MyLocation extends RecyclerView.ViewHolder {
        private TextView cityText, condDescr, tempCel, tempFeh;
        private SimpleDraweeView backDrop;
        private RelativeLayout RL_card_info;
        private RelativeLayout RL_card_load;
        private WeatherIconView weatherIconView;

        public VHCARD_MyLocation(final View view) {
            super(view);
            CardView cardView = (CardView) view;
            //cardView.setUseCompatPadding(false);
            cityText = (TextView) view.findViewById(R.id.cityText);
            condDescr = (TextView) view.findViewById(R.id.textView_condDescription);
            tempCel = (TextView) view.findViewById(R.id.tempCel);
            tempFeh = (TextView) view.findViewById(R.id.tempFeh);
            backDrop = (SimpleDraweeView) view.findViewById(R.id.backDrop);

            weatherIconView = (WeatherIconView) view.findViewById(R.id.weatherIconView);

            RL_card_info = (RelativeLayout) view.findViewById(R.id.RL_Card_Info);
            RL_card_load = (RelativeLayout) view.findViewById(R.id.RL_Card_Load);

            ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageView_LocationToggle);
            imageButton.setOnClickListener(getOnClickListener());
        }

        public TextView getCityText() {
            return cityText;
        }

        public void setCityText(TextView cityText) {
            this.cityText = cityText;
        }

        public TextView getCondDescr() {
            return condDescr;
        }

        public void setCondDescr(TextView condDescr) {
            this.condDescr = condDescr;
        }

        public TextView getTempFeh() {
            return tempFeh;
        }

        public void setTempFeh(TextView tempFeh) {
            this.tempFeh = tempFeh;
        }

        public TextView getTempCel() {
            return tempCel;
        }

        public void setTempCel(TextView tempCel) {
            this.tempCel = tempCel;
        }

        public SimpleDraweeView getBackDrop() {
            return backDrop;
        }

        public void setBackDrop(SimpleDraweeView backDrop) {
            this.backDrop = backDrop;
        }

        public RelativeLayout getRL_card_info() {
            return RL_card_info;
        }

        public RelativeLayout getRL_card_load() {
            return RL_card_load;
        }

        public WeatherIconView getWeatherIconView() {
            return weatherIconView;
        }
    }

/*************************************************************************************************/
//*************************************************************************************************/

    /**
     * *
     * *
     **/

    private class MyTaskHelper extends AsyncTask<Object, Void, Palette> {
        private RecyclerView.ViewHolder holder;
        private Bitmap bitmap;
        private int position;

        @Override
        protected Palette doInBackground(Object... objects) {
            holder = (RecyclerView.ViewHolder) objects[0];
            bitmap = (Bitmap) objects[1];
            position = (int) objects[2];
            Palette p = Palette.from(bitmap).generate();
            return p;
        }


        @Override
        protected void onPostExecute(Palette palette) {
            super.onPostExecute(palette);

            final RecyclerView.ViewHolder viewHolder = holder;
            CardView cardView = (CardView) viewHolder.itemView;
            int mainColour;
            Palette.Swatch swatch = null;

            swatch = palette.getMutedSwatch();
            if (swatch == null) {
                swatch = palette.getDarkMutedSwatch();
                if (swatch == null) {
                    swatch = palette.getVibrantSwatch();
                }
            }
            mainColour = swatch.getRgb();

            //setting colour
            cardView.setCardBackgroundColor(mainColour);
            cardView.invalidate();

            //cardList.ge

        }

    }

    private class PostWork extends AsyncTask<Object, Void, Void> {
        private RecyclerView.ViewHolder holder;

        @Override
        protected Void doInBackground(Object... params) {
            holder = (RecyclerView.ViewHolder) params[0];
            return null;
        }

        protected void onPostExecute(Void ok) {
            holder.itemView.invalidate();
        }

    }

    public void callAsynchronousTask(RecyclerView.ViewHolder holder) {
        final Object[] o = {holder};
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            PostWork postWork = new PostWork();
                            // postWork this class is the class that extends AsynchTask
                            postWork.execute(o);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 50000); //execute in every 50000 ms
    }
}