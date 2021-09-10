package com.csk.wheatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.csk.wheatherapp.data.local.entity.Location
import com.csk.wheatherapp.data.local.entity.LocationDao

@Database(
    entities = [Location::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract val locationDao: LocationDao
}