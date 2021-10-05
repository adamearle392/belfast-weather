package com.adamearle.belfastweather.ui.view;

import static com.adamearle.belfastweather.network.ServiceGenerator.BASE_URL;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adamearle.belfastweather.R;
import com.adamearle.belfastweather.model.Weather;
import com.adamearle.belfastweather.network.util.Resource;
import com.adamearle.belfastweather.ui.adapter.WeatherAdapter;
import com.adamearle.belfastweather.ui.viewmodel.MainActivityViewModel;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainActivityViewModel mViewModel;

    private TextView mTimeTextView;
    private LinearLayout mColumnHeadings;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private WeatherAdapter mAdapter;

    // mLocationName variable can later be extended to an edittext for dynamic fetching
    private String mTime, mLocationName = "belfast";
    private List<Weather> mWeather = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);


        mTimeTextView = findViewById(R.id.main_time);
        mColumnHeadings = findViewById(R.id.main_columnHeadings);
        mRecyclerView = findViewById(R.id.main_recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.main_swipeRefresh);
        mSwipeRefreshLayout.setRefreshing(true);

        // Method 1 of networking using MVVM, Repository pattern and RetroFit (for Location)
        retrofit();
        recyclerView();
        swipeRefresh();
    }

    private void retrofit() {
        mViewModel.fetchLocations(mLocationName, mSwipeRefreshLayout);

        mViewModel.locations().observe(this, source -> {
            if (source.data != null && source.status == Resource.Status.SUCCESS) {
                // Method 2 of networking using Volley to simplify the process (for Weather)
                volley(source.data.get(0).getWoeid());
            }
        });
    }

    private void volley(int woeid) {
        // woeid is passed from previously fetched location, which is then passed to the api again to get weather
        Volley.newRequestQueue(this).add(new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL + "/api/location/" + woeid,
                null,
                response -> {
                    try {
                        // Parse datetime from api to suitable format
                        mTime = response.getString("time");
                        Date date = Date.from(OffsetDateTime.parse(mTime).toInstant());

                        if (date != null) {
                            mTime = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(date);
                        }

                        mTimeTextView.setText(getString(R.string.Last_fetched) + " " + mTime);
                        mTimeTextView.setVisibility(View.VISIBLE);
                        mWeather.clear();

                        // Access nested json array for weather from api
                        JSONArray jsonArray = response.getJSONArray("consolidated_weather");

                        // Loop json array and convert to local weather model list for adapter
                        // Retrieves the next 5 days of forecast, starting index at 1 (tomorrow)
                        // Switch index i to 0 if you want to include today's forecast
                        for (int i = 1; i < jsonArray.length(); i++) {
                            JSONObject day = (JSONObject) jsonArray.get(i);

                            mWeather.add(new Weather(
                                    day.getInt("id"),
                                    day.getString("weather_state_name"),
                                    day.getString("weather_state_abbr"),
                                    day.getString("applicable_date"),
                                    day.getDouble("the_temp")
                            ));
                        }

                        mColumnHeadings.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);

                        // Notify ui recycler view adapter of any changes to weather list
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            recyclerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e(TAG, "volley: error " + error)
        ));
    }

    private void recyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new WeatherAdapter(getApplicationContext(), mWeather);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void swipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.fetchLocations(mLocationName, mSwipeRefreshLayout);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.fetchLocations(mLocationName, mSwipeRefreshLayout);
    }
}
