package com.csk.wheatherapp.data.remote

import com.csk.wheatherapp.data.remote.model.UpcomingWeatherResponse
import com.csk.wheatherapp.data.remote.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppApi {
    @GET("data/2.5/weather")
    suspend fun getWeatherReport(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<WeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getUpComingWeatherReport(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<UpcomingWeatherResponse>


}