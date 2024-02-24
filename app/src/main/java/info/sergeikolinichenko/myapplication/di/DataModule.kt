package info.sergeikolinichenko.myapplication.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import info.sergeikolinichenko.data.local.db.CitiesDao
import info.sergeikolinichenko.data.local.db.CitiesDatabase
import info.sergeikolinichenko.data.network.api.ApiFactory
import info.sergeikolinichenko.data.repositories.FavouriteRepositoryImpl
import info.sergeikolinichenko.data.repositories.WeatherRepositoryImpl
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.domain.repositories.WeatherRepository

/** Created by Sergei Kolinichenko on 23.02.2024 at 22:47 (GMT+3) **/
@Module
interface DataModule {
  @[ApplicationScope Binds]
  fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository
  @[ApplicationScope Binds]
  fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository
  @[ApplicationScope Binds]
  fun bindSearchRepository(impl: WeatherRepositoryImpl): WeatherRepository

  companion object {
    @[ApplicationScope Provides]
    fun provideApiService() = ApiFactory.apiService
    @[ApplicationScope Provides]
    fun provideCitiesDatabase(context: Context) = CitiesDatabase.getInstance(context)
    @[ApplicationScope Provides]
    fun provideCitiesDao(database: CitiesDatabase): CitiesDao = database.citiesDao()
  }
}