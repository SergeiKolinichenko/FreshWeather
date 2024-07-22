package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.ForecastCurrent
import info.sergeikolinichenko.domain.entity.ForecastDaily
import info.sergeikolinichenko.domain.entity.ForecastHourly
import info.sergeikolinichenko.domain.entity.ForecastLocation
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.ForecastDaysDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastLocationDto
import info.sergeikolinichenko.myapplication.network.dto.WeatherDto
import info.sergeikolinichenko.myapplication.utils.forecastDto
import info.sergeikolinichenko.myapplication.utils.testWeatherDto
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response


/** Created by Sergei Kolinichenko on 17.06.2024 at 21:03 (GMT+3) **/

class WeatherRepositoryImplShould {
  // region constants
  private val apiService = mock<ApiService>()
  private val preferences = mock<SharedPreferences>()
  private val id = 1
  private val location = "id:$id"
  private val errorMessage = "Some kind of message"
  // endregion constants

  private val SUT = WeatherRepositoryImpl(apiService, preferences)

  @Test
  fun `get Response with CurrentDto from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getWeather(location)).thenReturn(Response.success(testWeatherDto))
    // Act
    SUT.getWeather(id)
    // Assert
    verify(apiService, times(1)).getWeather(location)
  }

  @Test
  fun `get mapped Weather from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getWeather(location)).thenReturn(Response.success(testWeatherDto))
    val expected = testWeatherDto.toCurrentWeather()
    // Act
    val result = SUT.getWeather(id).getOrNull()
    // Assert
    assert(result == expected)
  }

  @Test
  fun `get error of receiving Weather from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.getWeather(location)).thenReturn(
//      Response.error(400, "".toByteArray().toResponseBody(null))
      Response.error(400, errorMessage.toByteArray().toResponseBody())
    )
    // Act
    val result = SUT.getWeather(id).exceptionOrNull()?.message
    // Assert
      assert(result == errorMessage)
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
      Response.error(400, errorMessage.toByteArray().toResponseBody())
    )
    // Act
    val thrown = assertThrows(Exception::class.java) { runBlocking { SUT.getForecast(id) } }
    // Assert
    assert(thrown.message == "Error while getting forecast")
  }

  // region helper functions
  private fun WeatherDto.toCurrentWeather() = Weather(
    temp = current.tempC.toCelsiusString(),
    maxTemp = weather.forecastDay.first().day.maxTempC.toCelsiusString(),
    minTemp = weather.forecastDay.first().day.minTempC.toCelsiusString(),
    condIconUrl = current.condition.icon.correctUrl(),
    description = current.condition.text
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

  private fun String.correctUrl() = "https:$this".replace(
    "64x64",
    "128x128"
  )
  // endregion helper functions
}