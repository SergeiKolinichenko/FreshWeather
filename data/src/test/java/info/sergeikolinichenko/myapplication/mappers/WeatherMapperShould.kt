package info.sergeikolinichenko.myapplication.mappers

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.utils.forecastCurrentDto
import info.sergeikolinichenko.myapplication.utils.forecastDayDto
import info.sergeikolinichenko.myapplication.utils.forecastDto
import info.sergeikolinichenko.myapplication.utils.forecastLocationDto
import info.sergeikolinichenko.myapplication.utils.testWeatherCurrentDto
import info.sergeikolinichenko.myapplication.utils.testWeatherDaysDto
import info.sergeikolinichenko.myapplication.utils.testWeatherDto
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import org.junit.Test

/** Created by Sergei Kolinichenko on 17.06.2024 at 18:14 (GMT+3) **/
class WeatherMapperShould {
  @Test
  fun `map WeatherDto to toFavouriteScreenWeather`() {
    // Arrange
    val settings = mock<Settings>()
    whenever(settings.temperature).thenReturn(TEMPERATURE.CELSIUS)
    // Act
    val result = testWeatherDto.toFavouriteScreenWeather(settings)
    // Assert
    assert(result.temp == testWeatherCurrentDto.tempC.toCelsiusString())
    assert(result.maxTemp == testWeatherDaysDto.forecastDay.first().day.maxTempC.toCelsiusString())
    assert(result.minTemp == testWeatherDaysDto.forecastDay.first().day.minTempC.toCelsiusString())
    assert(result.description == testWeatherCurrentDto.condition.text)
    assert(result.condIconUrl == testWeatherCurrentDto.condition.icon.correctUrl())
  }
  @Test
  fun `map ForecastDto to Forecast`() {
    // Act
    val result = forecastDto.toForecast()
    // Assert
    assert(result.forecastLocation.tzId == forecastLocationDto.tzId)
    assert(result.forecastCurrent.date == forecastCurrentDto.lastUpdatedEpoch)
    assert(result.forecastCurrent.tempC == forecastCurrentDto.tempC)
    assert(result.forecastCurrent.maxTempC == forecastDayDto.maxTempC)
    assert(result.forecastCurrent.minTempC == forecastDayDto.minTempC)
    assert(result.forecastCurrent.feelsLikeC == forecastCurrentDto.feelsLikeC)
    assert(result.forecastCurrent.cloud == forecastCurrentDto.cloud)
    assert(result.forecastCurrent.precipMm == forecastCurrentDto.precipMm)
    assert(result.forecastCurrent.windDir == forecastCurrentDto.windDir)
    assert(result.forecastCurrent.uv == forecastCurrentDto.uv)
    assert(result.forecastCurrent.descriptionText == forecastCurrentDto.condition.text)
    assert(result.forecastCurrent.condIconUrl == forecastCurrentDto.condition.icon.correctUrl())
    assert(result.forecastCurrent.windKph == forecastCurrentDto.windKph)
    assert(result.forecastCurrent.windDir == forecastCurrentDto.windDir)
    assert(result.forecastCurrent.pressureMb == forecastCurrentDto.pressureMb)
    assert(result.forecastCurrent.humidity == forecastCurrentDto.humidity)
  }

  // region helper functions
  private fun String.correctUrl() = "https:$this".replace(
    "64x64",
    "128x128"
  )
  // endregion helper functions
}