package com.adamearle.belfastweather.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.adamearle.belfastweather.model.Location

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg locations: Location): LongArray

    @RawQuery(observedEntities = [Location::class])
    fun get(query: SimpleSQLiteQuery): LiveData<List<Location>>

    @Query("SELECT * FROM ${Location.TABLE_NAME} WHERE title = :title")
    fun find(title: String): Location

    @Query("DELETE FROM ${Location.TABLE_NAME}")
    fun truncate()
}