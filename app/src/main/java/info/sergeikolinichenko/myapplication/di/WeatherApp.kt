package info.sergeikolinichenko.myapplication.di

import android.app.Application

/** Created by Sergei Kolinichenko on 23.02.2024 at 22:43 (GMT+3) **/

class WeatherApp: Application(){

  lateinit var appComponent: ApplicationComponent
  override fun onCreate() {
    super.onCreate()
    appComponent = DaggerApplicationComponent.factory().create(this)
  }
}