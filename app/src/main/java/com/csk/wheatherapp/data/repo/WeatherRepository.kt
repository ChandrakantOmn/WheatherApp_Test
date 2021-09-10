package com.csk.wheatherapp.data.repo

import androidx.lifecycle.LiveData
import com.csk.wheatherapp.data.local.AppDatabase
import com.csk.wheatherapp.data.local.entity.Location
import com.csk.wheatherapp.data.remote.AppApi
import com.csk.wheatherapp.data.remote.model.UpcomingWeatherResponse
import com.csk.wheatherapp.data.remote.model.WeatherResponse

class WeatherRepository(
    private val api: AppApi,
    private val db: AppDatabase
) :
    SafeApiRequest() {

    suspend fun getWeatherReport(selectedLocation: Location): WeatherResponse {
        return apiRequest {
            api.getWeatherReport(
                selectedLocation.latitude,
                selectedLocation.longitude
            )
        }
    }

    suspend fun getUpComingWeatherReport(selectedLocation: Location): UpcomingWeatherResponse {
        return apiRequest {
            api.getUpComingWeatherReport(
                selectedLocation.latitude,
                selectedLocation.longitude,
            )
        }
    }

    suspend fun saveAddress(location: Location) {
        db.locationDao.add(location)
    }

    fun getLocations(): LiveData<List<Location>> {
        return db.locationDao.getAll()
    }

    suspend fun removeLocation(location: Location) {
        db.locationDao.delete(location)
    }
}
