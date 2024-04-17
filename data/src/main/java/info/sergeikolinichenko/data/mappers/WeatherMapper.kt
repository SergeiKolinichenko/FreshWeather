package info.sergeikolinichenko.data.mappers

import info.sergeikolinichenko.data.network.dto.ForecastDto
import info.sergeikolinichenko.data.network.dto.WeatherCurrentDto
import info.sergeikolinichenko.data.network.dto.WeatherForecastDto
import info.sergeikolinichenko.domain.entity.CurrentWeather
import info.sergeikolinichenko.domain.entity.DailyWeather
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.HourlyWeather
import info.sergeikolinichenko.domain.entity.Weather

/** Created by Sergei Kolinichenko on 23.02.2024 at 20:18 (GMT+3) **/

// current weather for the favourite screen
fun WeatherCurrentDto.toFavouriteScreenWeather() = Weather(
  tempC = current.tempC,
  condIconUrl = current.condition.icon.correctUrl()
)

// extended 3-day weather forecast for the details screen
fun WeatherForecastDto.toForecast() = Forecast(
    currentWeather = toCurrentWeather(),
    upcomingDays = this.forecast.toDailyWeather(),
    upcomingHours = this.forecast.toHourlyWeather()
  )

// current weather for the details screen
private fun WeatherForecastDto.toCurrentWeather(): CurrentWeather {
  val todayWeather = forecast.forecastDay.first().dailyWeather
  return CurrentWeather(
    date = current.lastUpdatedEpoch,
    tempC = current.tempC,
    maxTempC = todayWeather.maxTempC,
    minTempC = todayWeather.minTempC,
    feelsLikeC = current.feelsLikeC,
    cloud = current.cloud,
    precipMm = current.precipMm,
    windDir = current.windDir,
    uv = current.uv,
    descriptionText = current.condition.text,
    condIconUrl = current.condition.icon.correctUrl(),
    windKph = current.windKph,
    pressureMb = current.pressureMb,
    humidity = current.humidity
  )
}

// daily weather for the details screen
private fun ForecastDto.toDailyWeather() = forecastDay.drop(1).map { dayDto ->
    val weatherDto = dayDto.dailyWeather
    DailyWeather(
      date = dayDto.dateEpoch,
      maxTempC = weatherDto.maxTempC,
      minTempC = weatherDto.minTempC,
      condIconUrl = weatherDto.conditionDto.icon.correctUrl(),
      windKph = weatherDto.maxWindKph,
      uv = weatherDto.uv,
      dailyWillTtRain = weatherDto.dailyWillTtRain,
      dailyChanceOfRain = weatherDto.dailyChanceOfRain,
      dailyWillItSnow = weatherDto.dailyWillItSnow,
      dailyChanceOfSnow = weatherDto.dailyChanceOfSnow
    )
  }

// hourly weather for the details screen
private fun ForecastDto.toHourlyWeather() = forecastDay.flatMap { day ->
  day.forecastHourlyDtoArray.map { hour ->
    HourlyWeather(
      date = hour.timeEpoch,
      tempC = hour.tempC,
      maxTempC = hour.tempC,
      minTempC = hour.tempC,
      descriptionText = hour.condition.text,
      condIconUrl = hour.condition.icon.correctUrl(),
      windKph = hour.windKph,
      windDir = hour.windDir,
      pressureMb = hour.pressureMb,
      humidity = hour.humidity,
      uv = hour.uv,
      willItRain = hour.willItRain,
      chanceOfRain = hour.chanceOfRain,
      willItSnow = hour.willItSnow,
      chanceOfSnow = hour.chanceOfSnow
    )
  }
}
private fun String.correctUrl() = "https:$this".replace(
  "64x64",
  "128x128"
)