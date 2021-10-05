package com.adamearle.belfastweather.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adamearle.belfastweather.model.Location
import com.adamearle.belfastweather.model.Weather

@Database(
    entities = [Location::class, Weather::class],
    version = 6
)
abstract class DB : RoomDatabase() {
    companion object {
        private var instance: DB? = null

        @JvmStatic
        fun getInstance(context: Context): DB? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    DB::class.java,
                    "weather_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }

    abstract fun getLocationDao(): LocationDao
}