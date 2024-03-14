package info.sergeikolinichenko.data.mappers

import info.sergeikolinichenko.data.network.dto.WeatherCurrentDto
import info.sergeikolinichenko.data.network.dto.WeatherDto
import info.sergeikolinichenko.data.network.dto.WeatherForecastDto
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.Weather

/** Created by Sergei Kolinichenko on 23.02.2024 at 20:18 (GMT+3) **/

fun WeatherCurrentDto.toWeather(): Weather = current.toWeather()
fun WeatherForecastDto.toForecast() = Forecast(
  currentWeather = current.toWeather(),
  upcomingDays = forecast.forecastDay.drop(1).map { dayDto ->
    val weatherDto = dayDto.dayWeather
    Weather(
      temperature = weatherDto.avgTemperatureC,
      maxTemp = weatherDto.maxTemperatureC,
      minTemp = weatherDto.minTemperatureC,
      descriptionWeather = weatherDto.conditionDto.condition,
      conditionUrl = weatherDto.conditionDto.conditionUrl.correctUrl(),
      windSpeed = current.windSpeed,
      airPressure = current.airPressure,
      humidity = current.humidity,
      date = dayDto.date
    )
  },

  upcomingHours = forecast.forecastDay.flatMap { day ->
    day.hourWeatherArray.map { hour ->
      Weather(
        temperature = hour.hourTemp,
        maxTemp = null,
        minTemp = null,
        descriptionWeather = hour.hourCond.description,
        conditionUrl = hour.hourCond.icon.correctUrl(),
        windSpeed = hour.hourWindKph,
        airPressure = hour.hourPressure,
        humidity = hour.hourHumidity,
        date = hour.hourTime
      )
    }
  }
)

fun WeatherDto.toWeather(): Weather {
  return Weather(
    temperature = temperatureC,
    maxTemp = null,
    minTemp = null,
    descriptionWeather = condition.condition,
    conditionUrl = condition.conditionUrl.correctUrl(),
    windSpeed = windSpeed,
    airPressure = airPressure,
    humidity = humidity,
    date = date
  )
}

private fun String.correctUrl() = "https:$this".replace(
  "64x64",
  "128x128"
)