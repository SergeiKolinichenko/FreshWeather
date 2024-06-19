package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.myapplication.utils.conditionDto
import info.sergeikolinichenko.myapplication.utils.currentDto
import info.sergeikolinichenko.myapplication.utils.currentWeatherDto
import info.sergeikolinichenko.myapplication.utils.dayDto
import info.sergeikolinichenko.myapplication.utils.forecastCurrentDto
import info.sergeikolinichenko.myapplication.utils.forecastDto
import info.sergeikolinichenko.myapplication.utils.forecastLocationDto
import org.junit.Test

/** Created by Sergei Kolinichenko on 17.06.2024 at 18:14 (GMT+3) **/
class WeatherMapperShould {
  @Test
  fun `map CurrentDto to CurrentWeather`() {
    // Act
    val result = currentDto.toFavouriteScreenWeather()
    // Assert
    assert(result.tempC == currentWeatherDto.tempC)
    assert(result.condIconUrl == conditionDto.icon.correctUrl())
  }
  @Test
  fun `map ForecastDto to Forecast`() {
    // Act
    val result = forecastDto.toForecast()
    // Assert
    assert(result.forecastLocation.tzId == forecastLocationDto.tzId)
    assert(result.forecastCurrent.date == forecastCurrentDto.lastUpdatedEpoch)
    assert(result.forecastCurrent.tempC == forecastCurrentDto.tempC)
    assert(result.forecastCurrent.maxTempC == dayDto.maxTempC)
    assert(result.forecastCurrent.minTempC == dayDto.minTempC)
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