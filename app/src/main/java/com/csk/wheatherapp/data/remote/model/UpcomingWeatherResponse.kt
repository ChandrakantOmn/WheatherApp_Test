package com.csk.wheatherapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class UpcomingWeatherResponse(
    var city: City?,
    var cnt: Int?, // 40
    var cod: String?, // 200
    @SerializedName("list")
    var list: List<Daily?>?,
    var message: Int? // 0
) {
    data class City(
        var coord: Coord?,
        var country: String?,
        var id: Int?, // 6295630
        var name: String?, // Globe
        var population: Int?, // 2147483647
        var sunrise: Int?, // 1631166849
        var sunset: Int?, // 1631210449
        var timezone: Int? // 0
    ) {
        class Coord
    }

    data class Daily(
        var clouds: Clouds?,
        var dt: Int?, // 1631167200
        @SerializedName("dt_txt")
        var dtTxt: String?, // 2021-09-09 06:00:00
        var main: Main?,
        var pop: Double?, // 0.2
        var rain: Rain?,
        var sys: Sys?,
        var visibility: Int?, // 10000
        var weather: List<Weather?>?,
        var wind: Wind?
    ) {
        data class Clouds(
            var all: Int? // 99
        )

        data class Main(
            @SerializedName("feels_like")
            var feelsLike: Double?, // 24.92
            @SerializedName("grnd_level")
            var grndLevel: Int?, // 1012
            var humidity: Int?, // 86
            var pressure: Int?, // 1005
            @SerializedName("sea_level")
            var seaLevel: Int?, // 1005
            var temp: Double?, // 24.2
            @SerializedName("temp_kf")
            var tempKf: Double?, // -1.47
            @SerializedName("temp_max")
            var tempMax: Double?, // 25.67
            @SerializedName("temp_min")
            var tempMin: Double? // 24.2
        )

        data class Rain(
            @SerializedName("3h")
            var h: Double? // 0.13
        )

        data class Sys(
            var pod: String? // d
        )

        data class Weather(
            var description: String?, // light rain
            var icon: String?, // 10d
            var id: Int?, // 500
            var main: String? // Rain
        )

        data class Wind(
            var deg: Int?, // 186
            var gust: Double?, // 4.33
            var speed: Double? // 4.92
        )
    }
}