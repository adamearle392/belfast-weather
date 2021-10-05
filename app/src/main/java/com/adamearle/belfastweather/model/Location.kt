package com.adamearle.belfastweather.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// This data model class uses Google's GSON for Json serialization with RetroFit

@Entity(tableName = Location.TABLE_NAME)
data class Location(
    @PrimaryKey @SerializedName("title") val title: String,
    @SerializedName("location_type") val location_type: String,
    @SerializedName("woeid") val woeid: Int,
) {
    companion object {
        const val TABLE_NAME = "locations"
        const val API_ROUTE = "location/search"
    }
}