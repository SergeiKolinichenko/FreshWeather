package info.sergeikolinichenko.myapplication.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import info.sergeikolinichenko.data.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.presentation.ui.theme.FreshWeatherTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val apiService = ApiFactory.apiService

    CoroutineScope(Dispatchers.Main).launch {
      val currentWeather = apiService.getCurrentWeather("Tashkent")
      val weatherForecast = apiService.getWeatherForecast("Blagoevgrad")
      val search = apiService.searchCities("Kentau")
      Log.d(TAG, "current weather: $currentWeather\nweather forecast: $weatherForecast\nsearch: $search")
    }

    setContent {
      FreshWeatherTheme {

      }
    }
  }
  companion object {
    private const val TAG = "MyLog"
  }
}