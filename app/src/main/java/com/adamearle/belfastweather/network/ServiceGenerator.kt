package com.adamearle.belfastweather.network

import com.adamearle.belfastweather.network.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetroFit instance builder, can add additional headers or request query interceptors here
// Also provides access to Api service call interfaces such as LocationApi
object ServiceGenerator {
    const val BASE_URL = "https://www.metaweather.com"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("$BASE_URL/api/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @JvmStatic
    fun locations(): LocationApi = retrofit.create(LocationApi::class.java)
}