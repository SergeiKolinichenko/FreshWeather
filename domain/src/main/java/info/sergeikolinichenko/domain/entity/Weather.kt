package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:21 (GMT+3) **/

data class Weather(
    val temperature: Float,
    val maxTempC: Float?,
    val minTempC: Float?,
    val feelsLikeC: Float,
    val descriptionWeather: String,
    val conditionUrl: String,
    val windSpeed: Float,
    val windDirection: String,
    val airPressure: Float,
    val humidity: Int,
    val precipiceMm: Float,
    val cloudCover: Int,
    val uvIndex: Float,
    val date: Long
    )
