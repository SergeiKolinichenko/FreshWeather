package info.sergeikolinichenko.myapplication.repositories

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.CurrentWeather
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.ForecastCurrent
import info.sergeikolinichenko.domain.entity.ForecastDaily
import info.sergeikolinichenko.domain.entity.ForecastHourly
import info.sergeikolinichenko.domain.entity.ForecastLocation
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.CurrentDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDayDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastLocationDto
import info.sergeikolinichenko.myapplication.utils.currentDto
import info.sergeikolinichenko.myapplication.utils.forecastDto
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response


/** Created by Sergei Kolinichenko on 17.06.2024 at 21:03 (GMT+3) **/

class WeatherRepositoryImplShould {
  // region constants
  private val apiService = mock<ApiService>()
  private val id = 1
  private val location = "id:$id"
  private val exception = Exception("Some kind of message")
  // endregion constants

  private val SUT = WeatherRepositoryImpl(apiService)

  @Test
  fun `get Response with CurrentDto from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getWeather(location)).thenReturn(Response.success(currentDto))
    // Act
    SUT.getWeather(id)
    // Assert
    verify(apiService, times(1)).getWeather(location)
  }

  @Test
  fun `get mapped CurrentWeather from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getWeather(location)).thenReturn(Response.success(currentDto))
    val expected = currentDto.toCurrentWeather()
    // Act
    val result = SUT.getWeather(id)
    // Assert
    assert(result == expected)
  }

  @Test
  fun `get error of receiving CurrentWeather from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getWeather(location)).thenReturn(
      Response.error(
        400,
        "".toByteArray().toResponseBody(null)
      )
    )
    // Act
    val thrown = assertThrows(Exception::class.java) { runBlocking { SUT.getWeather(id) } }
    // Assert
    assert(thrown.cause == exception.cause)
    assert(thrown.message == "Error while getting weather")
  }

  @Test
  fun `get Response with ForecastDto from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getForecast(location)).thenReturn(Response.success(forecastDto))
    // Act
    SUT.getForecast(id)
    // Assert
    verify(apiService, times(1)).getForecast(location)
  }

  @Test
  fun `get mapped Forecast from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getForecast(location)).thenReturn(Response.success(forecastDto))
    val expected = forecastDto.toForecast()
    // Act
    val result = SUT.getForecast(id)
    // Assert
    assert(result == expected)
  }

  @Test
  fun `get error of receiving Forecast from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getForecast(location)).thenReturn(
      Response.error(
        400,
        "".toByteArray().toResponseBody(null)
      )
    )
    // Act
    val thrown = assertThrows(Exception::class.java) { runBlocking { SUT.getForecast(id) } }
    // Assert
    assert(thrown.cause == exception.cause)
    assert(thrown.message == "Error while getting forecast")
  }

  // region helper functions
  private fun CurrentDto.toCurrentWeather() = CurrentWeather(
    tempC = current.tempC,
    condIconUrl = current.condition.icon.correctUrl()
  )
  private fun ForecastDto.toForecast() = Forecast(
    forecastCurrent = toCurrentWeather(),
    forecastLocation = this.location.toLocationCity(),
    upcomingDays = this.forecast.toDailyWeather(),
    upcomingHours = this.forecast.toHourlyWeather()
  )
  private fun ForecastLocationDto.toLocationCity() = ForecastLocation(
    tzId = tzId
  )
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

  private fun ForecastDayDto.toHourlyWeather() = forecastDay.flatMap { day ->
    day.hourDtoArray.map { hour ->
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

  private fun ForecastDayDto.toDailyWeather() = forecastDay.drop(1).map { dayDto ->
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

  private fun String.correctUrl() = "https:$this".replace(
    "64x64",
    "128x128"
  )
  // endregion helper functions
}