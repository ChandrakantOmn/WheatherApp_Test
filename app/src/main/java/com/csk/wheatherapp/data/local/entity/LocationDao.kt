package com.csk.wheatherapp.data.local.entity

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocationDao {

    @Query("SELECT * FROM Location")
     fun getAll(): LiveData<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(users: List<Location>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(user: Location)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: Location)

    @Delete
    suspend fun delete(location: Location)

}
