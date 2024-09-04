package info.sergeikolinichenko.myapplication.local.preferences

import android.content.Context
import android.content.SharedPreferences

/** Created by Sergei Kolinichenko on 13.04.2024 at 18:02 (GMT+3) **/

object FreshWeatherPreferences {

  private const val SHARED_PREFERENCES_NAME = "fresh_weather_shared_preferences"
  private var INSTANCE: SharedPreferences? = null
  private val LOCK = Any()

  fun getInstance(context: Context): SharedPreferences {
    INSTANCE?.let {
      return it
    }
    synchronized(LOCK) {
      INSTANCE?.let {
        return it
      }
      val sharPref =
        context.applicationContext.getSharedPreferences(
          SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
      INSTANCE = sharPref
      return sharPref
    }
  }
}