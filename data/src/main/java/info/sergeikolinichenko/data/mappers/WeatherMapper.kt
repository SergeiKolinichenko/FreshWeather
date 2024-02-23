package info.sergeikolinichenko.data.mappers

import info.sergeikolinichenko.data.network.dto.WeatherCurrentDto
import info.sergeikolinichenko.data.network.dto.WeatherDto
import info.sergeikolinichenko.data.network.dto.WeatherForecastDto
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.Weather
import java.util.Calendar

/** Created by Sergei Kolinichenko on 23.02.2024 at 20:18 (GMT+3) **/

fun WeatherCurrentDto.toWeather(): Weather = current.toWeather()
fun WeatherForecastDto.toForecast() = Forecast(
  currentWeather = current.toWeather(),
  upcoming = forecast.forecastDay.drop(1).map { dayDto ->
    val weatherDto = dayDto.dayWeatherDto
    Weather(
      temperature = weatherDto.avgTemperatureC,
      descriptionWeather = weatherDto.conditionDto.condition,
      conditionUrl = weatherDto.conditionDto.conditionUrl.correctUrl(),
      date = dayDto.date.toCalendar()
    )
  }
)
fun WeatherDto.toWeather(): Weather {
  return Weather(
    temperature = temperatureC,
    descriptionWeather = condition.condition,
    conditionUrl = condition.conditionUrl.correctUrl(),
    date = date.toCalendar()
  )
}

private fun Long.toCalendar(): Calendar {
  val calendar = Calendar.getInstance()
  calendar.timeInMillis = this * 1000
  return calendar
}

private fun String.correctUrl() = "https:$this".replace(
  "64x64",
  "128x128"
)