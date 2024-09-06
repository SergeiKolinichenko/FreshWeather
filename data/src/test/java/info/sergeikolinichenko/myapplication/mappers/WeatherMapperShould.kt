package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.utils.testForecastDto
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import info.sergeikolinichenko.myapplication.utils.toFahrenheitString
import info.sergeikolinichenko.myapplication.utils.toHpaString
import info.sergeikolinichenko.myapplication.utils.toMmHgString
import org.junit.Test

/** Created by Sergei Kolinichenko on 17.06.2024 at 18:14 (GMT+3) **/
class WeatherMapperShould {

  @Test
  fun `map WeatherDto to toForecast with Celsius anf Hpa`() {
    //
    val id = 101
    val settings = Settings(
      temperature = TEMPERATURE.CELSIUS,
      pressure = PRESSURE.HPA,
      precipitation = PRECIPITATION.MM
    )
    // Act
    val result = testForecastDto.mapToForecast(id = id, settings = settings)
    // Assert
    assert(result.id == id)
    assert(result.tzId == testForecastDto.timeZone)
    // currentForecast
    assert(result.currentForecast.temp == testForecastDto.currentWeatherDto.temp.toCelsiusString())
    assert(result.currentForecast.feelsLike == testForecastDto.currentWeatherDto.feelsLike.toCelsiusString())
    assert(result.currentForecast.humidity == testForecastDto.currentWeatherDto.humidity)
    assert(result.currentForecast.windSpeed == testForecastDto.currentWeatherDto.windSpeed)
    assert(result.currentForecast.windDir == testForecastDto.currentWeatherDto.windDir)
    assert(result.currentForecast.pressure == testForecastDto.currentWeatherDto.pressure.toHpaString())
    assert(result.currentForecast.uvIndex == testForecastDto.currentWeatherDto.uvIndex)
    assert(result.currentForecast.cloudCover == testForecastDto.currentWeatherDto.cloudCover)
    assert(result.currentForecast.conditions == testForecastDto.daysForecast.first().description)
    assert(result.currentForecast.icon == testForecastDto.currentWeatherDto.icon)
    // upcomingDays
    assert(result.upcomingDays.first().date == testForecastDto.daysForecast.first().datetimeEpoch)
    assert(result.upcomingDays.first().temp == testForecastDto.daysForecast.first().temp.toCelsiusString())
    assert(result.upcomingDays.first().tempMax == testForecastDto.daysForecast.first().tempMax.toCelsiusString())
    assert(result.upcomingDays.first().tempMin == testForecastDto.daysForecast.first().tempMin.toCelsiusString())
    assert(result.upcomingDays.first().humidity == testForecastDto.daysForecast.first().humidity)
    assert(result.upcomingDays.first().windSpeed == testForecastDto.daysForecast.first().windSpeed)
    assert(result.upcomingDays.first().windDir == testForecastDto.daysForecast.first().windDir)
    assert(result.upcomingDays.first().pressure == testForecastDto.daysForecast.first().pressure.toHpaString())
    assert(result.upcomingDays.first().uvIndex == testForecastDto.daysForecast.first().uvIndex)
    assert(result.upcomingDays.first().cloudCover == testForecastDto.daysForecast.first().cloudCover)
    assert(result.upcomingDays.first().precipProb == testForecastDto.daysForecast.first().precipProb)
    assert(result.upcomingDays.first().description == testForecastDto.daysForecast.first().description)
    assert(result.upcomingDays.first().icon == testForecastDto.daysForecast.first().icon)
    assert(result.upcomingDays.first().sunrise == testForecastDto.daysForecast.first().sunrise)
    assert(result.upcomingDays.first().sunset == testForecastDto.daysForecast.first().sunset)
    assert(result.upcomingDays.first().moonrise == testForecastDto.daysForecast.first().moonrise)
    assert(result.upcomingDays.first().moonset == testForecastDto.daysForecast.first().moonset)
    assert(result.upcomingDays.first().moonPhase == testForecastDto.daysForecast.first().moonPhase)
    // upcomingHours
    assert(result.upcomingHours.first().date == testForecastDto.daysForecast.first().hoursForecast.first().datetimeEpoch)
    assert(result.upcomingHours.first().temp == testForecastDto.daysForecast.first().hoursForecast.first().temp.toCelsiusString())
    assert(result.upcomingHours.first().icon == testForecastDto.daysForecast.first().hoursForecast.first().icon)
    assert(result.upcomingHours.first().pressure == testForecastDto.daysForecast.first().hoursForecast.first().pressure.toHpaString())
    assert(result.upcomingHours.first().humidity == testForecastDto.daysForecast.first().hoursForecast.first().humidity)
    assert(result.upcomingHours.first().uvIndex == testForecastDto.daysForecast.first().hoursForecast.first().uvIndex)
    assert(result.upcomingHours.first().precipProb == testForecastDto.daysForecast.first().hoursForecast.first().precipProb)
  }

  @Test
  fun `map WeatherDto to toForecast with Fahrenheit anf Mmhg`() {
    // Arrange
    val id = 101
    val settings = Settings(
      temperature = TEMPERATURE.FAHRENHEIT,
      pressure = PRESSURE.MMHG,
      precipitation = PRECIPITATION.MM
    )
    // Act
    val result = testForecastDto.mapToForecast(id = id, settings = settings)
    // Assert
    assert(result.id == id)
    assert(result.tzId == testForecastDto.timeZone)
    // currentForecast
    assert(result.currentForecast.temp == testForecastDto.currentWeatherDto.temp.toFahrenheitString())
    assert(result.currentForecast.feelsLike == testForecastDto.currentWeatherDto.feelsLike.toFahrenheitString())
    assert(result.currentForecast.humidity == testForecastDto.currentWeatherDto.humidity)
    assert(result.currentForecast.windSpeed == testForecastDto.currentWeatherDto.windSpeed)
    assert(result.currentForecast.windDir == testForecastDto.currentWeatherDto.windDir)
    assert(result.currentForecast.pressure == testForecastDto.currentWeatherDto.pressure.toMmHgString())
    assert(result.currentForecast.uvIndex == testForecastDto.currentWeatherDto.uvIndex)
    assert(result.currentForecast.cloudCover == testForecastDto.currentWeatherDto.cloudCover)
    assert(result.currentForecast.conditions == testForecastDto.daysForecast.first().description)
    assert(result.currentForecast.icon == testForecastDto.currentWeatherDto.icon)
    // upcomingDays
    assert(result.upcomingDays.first().date == testForecastDto.daysForecast.first().datetimeEpoch)
    assert(result.upcomingDays.first().temp == testForecastDto.daysForecast.first().temp.toFahrenheitString())
    assert(result.upcomingDays.first().tempMax == testForecastDto.daysForecast.first().tempMax.toFahrenheitString())
    assert(result.upcomingDays.first().tempMin == testForecastDto.daysForecast.first().tempMin.toFahrenheitString())
    assert(result.upcomingDays.first().humidity == testForecastDto.daysForecast.first().humidity)
    assert(result.upcomingDays.first().windSpeed == testForecastDto.daysForecast.first().windSpeed)
    assert(result.upcomingDays.first().windDir == testForecastDto.daysForecast.first().windDir)
    assert(result.upcomingDays.first().pressure == testForecastDto.daysForecast.first().pressure.toMmHgString())
    assert(result.upcomingDays.first().uvIndex == testForecastDto.daysForecast.first().uvIndex)
    assert(result.upcomingDays.first().cloudCover == testForecastDto.daysForecast.first().cloudCover)
    assert(result.upcomingDays.first().precipProb == testForecastDto.daysForecast.first().precipProb)
    assert(result.upcomingDays.first().description == testForecastDto.daysForecast.first().description)
    assert(result.upcomingDays.first().icon == testForecastDto.daysForecast.first().icon)
    assert(result.upcomingDays.first().sunrise == testForecastDto.daysForecast.first().sunrise)
    assert(result.upcomingDays.first().sunset == testForecastDto.daysForecast.first().sunset)
    assert(result.upcomingDays.first().moonrise == testForecastDto.daysForecast.first().moonrise)
    assert(result.upcomingDays.first().moonset == testForecastDto.daysForecast.first().moonset)
    assert(result.upcomingDays.first().moonPhase == testForecastDto.daysForecast.first().moonPhase)
    // upcomingHours
    assert(result.upcomingHours.first().date == testForecastDto.daysForecast.first().hoursForecast.first().datetimeEpoch)
    assert(result.upcomingHours.first().temp == testForecastDto.daysForecast.first().hoursForecast.first().temp.toFahrenheitString())
    assert(result.upcomingHours.first().icon == testForecastDto.daysForecast.first().hoursForecast.first().icon)
    assert(result.upcomingHours.first().pressure == testForecastDto.daysForecast.first().hoursForecast.first().pressure.toMmHgString())
    assert(result.upcomingHours.first().humidity == testForecastDto.daysForecast.first().hoursForecast.first().humidity)
    assert(result.upcomingHours.first().uvIndex == testForecastDto.daysForecast.first().hoursForecast.first().uvIndex)
    assert(result.upcomingHours.first().precipProb == testForecastDto.daysForecast.first().hoursForecast.first().precipProb)
  }

}