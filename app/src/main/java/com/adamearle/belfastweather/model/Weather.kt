package com.adamearle.belfastweather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// This data model class does not feature built in serialization and is
// manually handled with Volley in MainActivity volley() method

@Entity
data class Weather(
    @PrimaryKey val id: Int,
    val weather_state_name: String,
    val weather_state_abbr: String,
    val applicable_date: String,
    val the_temp: Double
)