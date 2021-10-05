package com.adamearle.belfastweather.network.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

// Networking utility class derived from Android guide to architecture (MVVM):
// https://github.com/android/architecture-components-samples
// and
// https://developer.android.com/jetpack/guide
public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

    private Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NotNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @NotNull
    @Override
    public LiveData<ApiResponse<R>> adapt(@NotNull final Call<R> call) {
        return new LiveData<>() {
            @Override
            protected void onActive() {
                super.onActive();
                final ApiResponse apiResponse = new ApiResponse();
                call.clone().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NotNull Call<R> call, @NotNull Response<R> response) {
                        postValue(apiResponse.create(response));
                    }

                    @Override
                    public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
                        postValue(apiResponse.create(t));
                    }
                });
            }
        };
    }
}
