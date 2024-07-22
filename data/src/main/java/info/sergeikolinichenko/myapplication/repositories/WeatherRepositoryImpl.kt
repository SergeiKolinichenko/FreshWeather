package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.domain.repositories.WeatherRepository
import info.sergeikolinichenko.myapplication.mappers.toFavouriteScreenWeather
import info.sergeikolinichenko.myapplication.mappers.toForecast
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.SETTINGS_KEY
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
  private val apiService: ApiService,
  private val preferences: SharedPreferences
) : WeatherRepository {

  override suspend fun getWeather(id: Int): Result<Weather> {
    val response = apiService.getWeather("$PREFIX_CITY_ID$id")
    if (response.isSuccessful) {
      return Result.success(response.body()!!.toFavouriteScreenWeather(getMySettings()))
    } else {
      return Result.failure(Exception(response.errorBody()?.string() ?: ERROR_MESSAGE_GET_WEATHER))
    }
  }

  override suspend fun getForecast(id: Int): Forecast {
    val response = apiService.getForecast("$PREFIX_CITY_ID$id")
    if (!response.isSuccessful) {
      throw Exception("Error while getting forecast")
    } else {
      return response.body()!!.toForecast()
    }
  }

  private fun getMySettings(): Settings {

    val jsonObject = preferences.getString(SETTINGS_KEY, null)

    return jsonObject?.let {
      val settings = Gson().fromJson(jsonObject, Settings::class.java)
      settings
    } ?:
      Settings(
      temperature = TEMPERATURE.CELSIUS,
      precipitation = PRECIPITATION.MM,
      pressure = PRESSURE.HPA
    )
  }

  companion object {
    private const val PREFIX_CITY_ID = "id:"
    const val ERROR_MESSAGE_GET_WEATHER = "Error while getting weather"
  }
}