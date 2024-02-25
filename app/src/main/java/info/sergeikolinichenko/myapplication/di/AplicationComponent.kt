package info.sergeikolinichenko.myapplication.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import info.sergeikolinichenko.myapplication.presentation.MainActivity

/** Created by Sergei Kolinichenko on 23.02.2024 at 22:39 (GMT+3) **/
@ApplicationScope
@Component(
  modules = [
    DataModule::class,
    PresentationModule::class
  ]
)
interface ApplicationComponent {
  fun inject(activity: MainActivity)

  @Component.Factory
  interface Factory {
    fun create(
      @BindsInstance context: Context
    ): ApplicationComponent
  }
}