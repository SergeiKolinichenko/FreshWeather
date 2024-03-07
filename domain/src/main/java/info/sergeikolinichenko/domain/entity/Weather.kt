package info.sergeikolinichenko.domain.entity

import java.util.Calendar

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:21 (GMT+3) **/

data class Weather(
    val temperature: Float,
    val maxTemp: Float?,
    val minTemp: Float?,
    val descriptionWeather: String,
    val conditionUrl: String,
    val windSpeed: Float,
    val airPressure: Float,
    val humidity: Int,
    val date: Calendar
    )
