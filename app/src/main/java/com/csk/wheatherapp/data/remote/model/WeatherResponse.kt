package com.csk.wheatherapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    var base: String?, // stations
    var clouds: Clouds?,
    var cod: Int?, // 200
    var coord: Coord?,
    var dt: Int?, // 1631201073
    var id: Int?, // 7302856
    var main: Main?,
    var name: String?, // Serilingampalle
    var sys: Sys?,
    var timezone: Int?, // 19800
    var visibility: Int?, // 6000
    var weather: List<Weather?>?,
    var wind: Wind?
) {
    data class Clouds(
        var all: Int? // 75
    )

    data class Coord(
        var lat: Double?, // 17.475
        var lon: Double? // 78.3174
    )

    data class Main(
        @SerializedName("feels_like")
        var feelsLike: Double?, // 298.66
        var humidity: Int?, // 83
        var pressure: Int?, // 1011
        var temp: Double?, // 297.96
        @SerializedName("temp_max")
        var tempMax: Double?, // 298.45
        @SerializedName("temp_min")
        var tempMin: Double? // 297.96
    )

    data class Sys(
        var country: String?, // IN
        var id: Int?, // 9214
        var sunrise: Int?, // 1631147645
        var sunset: Int?, // 1631192071
        var type: Int? // 1
    )

    data class Weather(
        var description: String?, // broken clouds
        var icon: String?, // 04n
        var id: Int?, // 803
        var main: String? // Clouds
    )

    data class Wind(
        var deg: Int?, // 0
        var speed: Double? // 1.54
    )
}