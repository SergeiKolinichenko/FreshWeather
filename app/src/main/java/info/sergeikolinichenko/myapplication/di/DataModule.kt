package info.sergeikolinichenko.myapplication.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import info.sergeikolinichenko.myapplication.local.db.FreshWeatherDao
import info.sergeikolinichenko.myapplication.local.db.FreshWeatherDatabase
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

  @Binds
  @ApplicationScope
  fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository

  @Binds
  @ApplicationScope
  fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

  @Binds
  @ApplicationScope
  fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

  @Binds
  @ApplicationScope
  fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

  companion object {

    @Provides
    @ApplicationScope
    fun provideFreshWeatherDatabase(context: Context) = FreshWeatherDatabase.getInstance(context)

    @Provides
    @ApplicationScope
    fun provideCitiesDao(database: FreshWeatherDatabase): FreshWeatherDao = database.citiesDao()

    @Provides
    @ApplicationScope
    fun provideSharedPreferences(context: Context) = FreshWeatherPreferences.getInstance(context) }
}