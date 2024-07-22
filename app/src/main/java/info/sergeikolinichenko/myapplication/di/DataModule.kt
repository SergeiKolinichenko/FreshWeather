package info.sergeikolinichenko.myapplication.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import info.sergeikolinichenko.myapplication.local.db.CitiesDao
import info.sergeikolinichenko.myapplication.local.db.CitiesDatabase
import info.sergeikolinichenko.myapplication.local.preferences.FreshWeatherPreferences
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.repositories.FavouriteRepositoryImpl
import info.sergeikolinichenko.myapplication.repositories.SearchRepositoryImpl
import info.sergeikolinichenko.myapplication.repositories.WeatherRepositoryImpl
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.domain.repositories.SearchRepository
import info.sergeikolinichenko.domain.repositories.SettingsRepository
import info.sergeikolinichenko.domain.repositories.WeatherRepository
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl

/** Created by Sergei Kolinichenko on 23.02.2024 at 22:47 (GMT+3) **/
@Module
interface DataModule {
  @[ApplicationScope Binds]
  fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository
  @[ApplicationScope Binds]
  fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository
  @[ApplicationScope Binds]
  fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
  @[ApplicationScope Binds]
  fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
  @[ApplicationScope Binds]
  fun bindSharedPreferences(preferences: FreshWeatherPreferences): FreshWeatherPreferences

  companion object {
    @[ApplicationScope Provides]
    fun provideApiService() = ApiFactory.apiService
    @[ApplicationScope Provides]
    fun provideCitiesDatabase(context: Context)
    = CitiesDatabase.getInstance(context)
    @[ApplicationScope Provides]
    fun provideCitiesDao(database: CitiesDatabase): CitiesDao = database.citiesDao()
    @[ApplicationScope Provides]
    fun provideSharedPreferences(context: Context) =
      FreshWeatherPreferences.getInstance(context)
  }
}