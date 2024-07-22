package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.ForecastCurrent
import info.sergeikolinichenko.domain.entity.ForecastDaily
import info.sergeikolinichenko.domain.entity.ForecastHourly
import info.sergeikolinichenko.domain.entity.ForecastLocation
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.network.dto.ForecastDaysDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastLocationDto
import info.sergeikolinichenko.myapplication.network.dto.WeatherDto
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import info.sergeikolinichenko.myapplication.utils.toFahrenheitString

/** Created by Sergei Kolinichenko on 23.02.2024 at 20:18 (GMT+3) **/

// current weather for the favourite screen
fun WeatherDto.toFavouriteScreenWeather(settings: Settings): Weather {

  var temperature = ""
  var maxTemperature = ""
  var minTemperature = ""

  when (settings.temperature) {
    TEMPERATURE.CELSIUS -> {
      temperature = current.tempC.toCelsiusString()
      maxTemperature = weather.forecastDay.first().day.maxTempC.toCelsiusString()
      minTemperature = weather.forecastDay.first().day.minTempC.toCelsiusString()
    }

    TEMPERATURE.FAHRENHEIT -> {
      temperature = current.tempF.toFahrenheitString()
      maxTemperature = weather.forecastDay.first().day.maxTempF.toFahrenheitString()
      minTemperature = weather.forecastDay.first().day.minTempF.toFahrenheitString()
    }
  }

  return Weather(
    temp = temperature,
    maxTemp = maxTemperature,
    minTemp = minTemperature,
    condIconUrl = current.condition.icon.correctUrl(),
    description = current.condition.text
  )
}

// extended 3-day weather forecast for the details screen
fun ForecastDto.toForecast() = Forecast(
  forecastCurrent = toCurrentWeather(),
  forecastLocation = this.location.toLocationCity(),
  upcomingDays = this.forecast.toDailyWeather(),
  upcomingHours = this.forecast.toHourlyWeather()
)

private fun ForecastLocationDto.toLocationCity() = ForecastLocation(
  tzId = tzId
)

// current weather for the details screen
private fun ForecastDto.toCurrentWeather(): ForecastCurrent {
  val todayWeather = forecast.forecastDay.first().dailyWeather
  return ForecastCurrent(
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
private fun ForecastDaysDto.toDailyWeather() = forecastDay.drop(1).map { dayDto ->
  val weatherDto = dayDto.dailyWeather
  ForecastDaily(
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
private fun ForecastDaysDto.toHourlyWeather() = forecastDay.flatMap { day ->
  day.forecastHourDtoArray.map { hour ->
    ForecastHourly(
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