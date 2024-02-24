package info.sergeikolinichenko.myapplication.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides

/** Created by Sergei Kolinichenko on 24.02.2024 at 17:26 (GMT+3) **/
@Module
interface PresentationModule {
  companion object {
    @Provides
    fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
  }
}