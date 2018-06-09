package com.orbot.theweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orbot.theweather.model.CardViewDisplay;

public class LocationDetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LocationListActivity.FRESCO == null || LocationListActivity.FRESCO == Boolean.FALSE) {
            Fresco.initialize(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_location_detail);
        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            CardViewDisplay cardViewDisplay = (CardViewDisplay) getIntent().getSerializableExtra(LocationDetailFragment.ARG_WEATHER_DETAIL);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (cardViewDisplay.getWeather().getCityLocation() != null) {
                actionBar.setTitle(cardViewDisplay.getWeather().getCityLocation().getName());
            }
        }
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(LocationDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(LocationDetailFragment.ARG_ITEM_ID, 0));
            arguments.putSerializable(LocationDetailFragment.ARG_WEATHER_DETAIL,
                    getIntent().getSerializableExtra(LocationDetailFragment.ARG_WEATHER_DETAIL));

            LocationDetailFragment fragment = new LocationDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.location_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, LocationListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
