package com.orbot.theweather;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orbot.theweather.model.SearchDatabaseHelper;
import com.orbot.theweather.util.RecyclerViewItemDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private View coordinatorLayoutView;
    private Intent cardIntent;

    //search
    private MyItemAdapter listAdapter;
    private LinearLayoutManager mLayoutSearchResult;
    private RecyclerView mRecyclerViewResult;

    private SearchDatabaseHelper myDbHelper;
    List<Item> listItem = new ArrayList<>();
    //result
    //private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        //layout
        mToolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(mToolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        coordinatorLayoutView = findViewById(R.id.coordinatorLayout);
        //RecyclerView
        mRecyclerViewResult = (RecyclerView) findViewById(R.id.searchResult_RecycleView);
        //RESULT
        mLayoutSearchResult = new LinearLayoutManager(getApplicationContext());
        mLayoutSearchResult.setOrientation(LinearLayoutManager.VERTICAL);
        //setting adapter and layout
        listAdapter = new MyItemAdapter(listItem);
        mRecyclerViewResult.setAdapter(listAdapter);
        mRecyclerViewResult.setLayoutManager(mLayoutSearchResult);
        mRecyclerViewResult.addItemDecoration(new RecyclerViewItemDivider(this));

        final GestureDetector mGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mRecyclerViewResult.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Item item=listAdapter.getItem(mRecyclerViewResult.getChildAdapterPosition(child));
                    cardIntent.putExtra("cityID",item.getCityID());
                    handleCloseRequest();
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

        myDbHelper = new SearchDatabaseHelper(getApplicationContext());
        cardIntent = new Intent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.Search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.onActionViewExpanded();


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //handleCloseRequest();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            final Cursor cursor = myDbHelper.getLocationMatches(query, null);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    showResults(cursor);
                }

            };
            runOnUiThread(runnable);
            ;

        }
    }

    private void showResults(Cursor cursor) {
        try {
            listItem.clear();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        //
                        Item location = new Item(cursor.getString((cursor.getColumnIndex("_id"))), cursor.getString(cursor.getColumnIndex("nm")),
                                cursor.getString(cursor.getColumnIndex("countryCode")));
                        listItem.add(location);
                    } while (cursor.moveToNext());
                }
            }
            listAdapter.notifyDataSetChanged();
            if (listItem.size() == 0) {
                Snackbar.make(coordinatorLayoutView, "No Result", Snackbar.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCloseRequest() {
        setResult(RESULT_OK, cardIntent);
        this.finish();
    }

    public class Item {
        private String cityID, name, countryCode;

        public Item(String cityID, String name, String countryCode) {
            this.cityID = cityID;
            this.name = name;
            this.countryCode = countryCode;
        }

        public String getCityID() {
            return cityID;
        }

        public void setCityID(String cityID) {
            this.cityID = cityID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
    }

    public class MyItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private int layoutResourceId;
        private List<Item> dataItem;

        public MyItemAdapter(Context context, int layoutResourceId, List<Item> data) {
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.dataItem = data;
        }

        public MyItemAdapter(List<Item> data) {
            this.dataItem = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
            return new SearchAdapterHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView textView, textViewCountry;
            try {
                Item item = dataItem.get(position);
                textView = ((SearchAdapterHolder) holder).getTextView();
                textViewCountry = ((SearchAdapterHolder) holder).getTextViewCountry();

                textView.setText(item.getName());
                Locale l = new Locale(Locale.getDefault().getLanguage(), item.countryCode);
                //localizedCountries.put(new Locale("en", iso).getDisplayCountry(new Locale("en", iso)), new Locale(Locale.getDefault().getLanguage(), iso).getDisplayCountry());
                textViewCountry.setText(l.getDisplayCountry());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return dataItem.size();
        }

        public Item getItem(int position) {
            return dataItem.get(position);
        }

        private class SearchAdapterHolder extends RecyclerView.ViewHolder {
            private TextView textView, textViewCountry;

            public SearchAdapterHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text);
                textViewCountry = (TextView) itemView.findViewById(R.id.textViewCountry);
            }

            public TextView getTextView() {
                return textView;
            }

            public void setTextView(TextView textView) {
                this.textView = textView;
            }

            public TextView getTextViewCountry() {
                return textViewCountry;
            }

            public void setTextViewCountry(TextView textViewCountry) {
                this.textViewCountry = textViewCountry;
            }


        }
    }
}
