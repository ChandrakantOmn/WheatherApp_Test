package com.csk.wheatherapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location")
class Location {
    var id: Int = 0
    var latitude = 0.0
    var longitude = 0.0
    var placeName: String? = null

    @PrimaryKey
    var cityName: String = ""
    var defaultSelection: Boolean = false
}
