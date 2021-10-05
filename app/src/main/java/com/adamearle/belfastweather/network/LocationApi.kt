package com.adamearle.belfastweather.network

import androidx.lifecycle.LiveData
import com.adamearle.belfastweather.model.Location
import com.adamearle.belfastweather.network.util.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApi {

    // Simple api interface to store api routes for Location data model, query is added to url as /?query=belfast dynamically
    @GET(Location.API_ROUTE)
    fun get(
        @Query("query") location: String
    ): LiveData<ApiResponse<List<Location>>>
}