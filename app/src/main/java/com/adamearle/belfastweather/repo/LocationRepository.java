package com.adamearle.belfastweather.repo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.adamearle.belfastweather.data.DB;
import com.adamearle.belfastweather.data.LocationDao;
import com.adamearle.belfastweather.model.Location;
import com.adamearle.belfastweather.network.ServiceGenerator;
import com.adamearle.belfastweather.network.util.ApiResponse;
import com.adamearle.belfastweather.network.util.AppExecutors;
import com.adamearle.belfastweather.network.util.NetworkBoundResource;
import com.adamearle.belfastweather.network.util.Resource;

import java.util.List;
import java.util.Objects;

// Repository pattern using NetworkBoundResource to decide when to
// fetch data based on device network status

@SuppressLint("StaticFieldLeak")
public class LocationRepository {

    private static LocationRepository instance;
    private LocationDao dao;
    private Context context;

    // Provides Singleton instance of this class, can later be upgraded to Hilt dependency injection
    public static LocationRepository getInstance(Context context) {
        if (instance == null) {
            instance = new LocationRepository(context);
        }
        return instance;
    }

    private LocationRepository(Context context) {
        this.context = context;
        dao = Objects.requireNonNull(DB.getInstance(context)).getLocationDao();
    }

    public LiveData<Resource<List<Location>>> get(String location) {
        return new NetworkBoundResource<List<Location>, List<Location>>(Objects.requireNonNull(AppExecutors.getInstance())) {
            @Override
            protected void saveCallResult(@NonNull List<Location> item) {
                Location[] array = new Location[item.size()];
                dao.truncate();
                dao.insert(item.toArray(array));
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Location> data) {
                // Only fetch from api if device is connected to a network, otherwise load from local db
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = null;

                if (connectivityManager != null) {
                    try {
                        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<Location>> loadFromDb() {
                // A custom sqlite query can be built up here from parent method parameters to filter local results
                return dao.get(new SimpleSQLiteQuery("SELECT * FROM " + Location.TABLE_NAME));
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Location>>> createCall() {
                return ServiceGenerator.locations().get(location);
            }
        }.getAsLiveData();
    }
}
