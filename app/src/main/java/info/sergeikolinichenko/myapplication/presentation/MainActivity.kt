package info.sergeikolinichenko.myapplication.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import info.sergeikolinichenko.myapplication.presentation.ui.theme.FreshWeatherTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      FreshWeatherTheme {

      }
    }
  }
}