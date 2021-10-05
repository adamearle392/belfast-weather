package com.adamearle.belfastweather.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adamearle.belfastweather.model.Location;
import com.adamearle.belfastweather.network.util.Resource;
import com.adamearle.belfastweather.repo.LocationRepository;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private boolean locationQueryRunning;
    private LocationRepository mLocationRepository;
    private MediatorLiveData<Resource<List<Location>>> locations = new MediatorLiveData<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mLocationRepository = LocationRepository.getInstance(application);
    }

    public MediatorLiveData<Resource<List<Location>>> locations() {
        return locations;
    }

    public void fetchLocations(String location, SwipeRefreshLayout swipeRefreshLayout) {
        if (!locationQueryRunning) {
            locationQueryRunning = true;
            final LiveData<Resource<List<Location>>> repositorySource = mLocationRepository.get(location);
            locations.addSource(repositorySource, source -> {
                if (source.data != null) {
                    locations.setValue(source);
                }
                if (source.status != Resource.Status.LOADING) {
                    locations.removeSource(repositorySource);

                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                locationQueryRunning = false;
            });
        }
    }
}
