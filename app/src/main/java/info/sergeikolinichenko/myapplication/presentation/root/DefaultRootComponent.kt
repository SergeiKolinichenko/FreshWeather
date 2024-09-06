package info.sergeikolinichenko.myapplication.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.details.SourceOfOpening
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DefaultDetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.component.DefaultEditingComponent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingStore
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.component.DefaultFavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.component.DefaultNextdaysComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.component.DefaultSearchComponent
import info.sergeikolinichenko.myapplication.presentation.screens.settings.component.DefaultSettingsComponent
import kotlinx.parcelize.Parcelize

class DefaultRootComponent @AssistedInject constructor(
  private val detailsComponentFactory: DefaultDetailsComponent.Factory,
  private val nextdaysComponentFactory: DefaultNextdaysComponent.Factory,
  private val favouriteComponentFactory: DefaultFavouriteComponent.Factory,
  private val searchComponentFactory: DefaultSearchComponent.Factory,
  private val settingsComponentFactory: DefaultSettingsComponent.Factory,
  private val editingFavouritesComponentFactory: DefaultEditingComponent.Factory,
  @Assisted("componentContext") private val componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

  private val navigation = StackNavigation<Config>()

  override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
    source = navigation,
    serializer = null,
    initialConfiguration = Config.Favourite,
    handleBackButton = true,
    childFactory = ::child
  )

  private fun child(
    config: Config,
    componentContext: ComponentContext
  ): RootComponent.Child = when (config) {

    is Config.Favourite -> {
      val component = favouriteComponentFactory.create(
        componentContext = componentContext,
        onSearchClick = { navigation.push(Config.Search) },
        onClickItemMenuSettings = { navigation.push(Config.Settings(sourceOfOpening = SourceOfOpening.OpenFromFavourite)) },
        onClickItemMenuEditing = { cities ->
          navigation.push(Config.Editing(cities))
        },
        onItemClick = { id, forecasts ->
          navigation.push(Config.Details(
            id = id,
            forecasts = forecasts
          ))
        }
      )
      RootComponent.Child.Favourite(component)
    }

    is Config.Details -> {
      val component = detailsComponentFactory.create(
        componentContext = componentContext,
        id = config.id,
        forecasts = config.forecasts,
        onClickedBack = { navigation.pop() },
        onClickedSettings = { navigation.push(Config.Settings(sourceOfOpening = SourceOfOpening.OpenFromDetails)) },
        onClickedDay = { id, index, forecasts ->
          navigation.push(Config.Nextdays(id = id, index = index, forecasts = forecasts))
        }
      )
      RootComponent.Child.Details(component)
    }

    is Config.Nextdays -> {
      val component = nextdaysComponentFactory.create(
        componentContext = componentContext,
        id = config.id,
        index = config.index,
        forecasts = config.forecasts,
        onSwipedTop = {
          navigation.pop()
        },
        onClickedClose = {
          navigation.popWhile{ it !is Config.Favourite}
        }
      )
      RootComponent.Child.Nextdays(component)
    }

    is Config.Search -> {
      val component = searchComponentFactory.create(
        componentContext = componentContext,
        onClickBack = { navigation.pop() },
        onClickItem = { navigation.pop() },
      )
      RootComponent.Child.Search(component)
    }

    is Config.Settings -> {
      val component = settingsComponentFactory.create(
        sourceOfOpening = config.sourceOfOpening,
        componentContext = componentContext,
        onClickBack = { navigation.pop() },
        settingsSaved = { openSource ->
          navigation.pop {
            when (openSource) {
              SourceOfOpening.OpenFromFavourite -> {
                (stack.active.instance as? RootComponent.Child.Favourite)?.component?.reloadForecast()
              }
              SourceOfOpening.OpenFromDetails -> {
                (stack.active.instance as? RootComponent.Child.Details)?.component?.reloadWeather()
              }
            }
          }
        }
      )
      RootComponent.Child.Settings(component)
    }

    is Config.Editing -> {
      val component = editingFavouritesComponentFactory.create(
        componentContext = componentContext,
        cities = config.cities,
        onBackClicked = { navigation.pop() },
        onClickedDone = {
          navigation.pop {
            (stack.active.instance as? RootComponent.Child.Favourite)?.component?.reloadCities()
          }
        }
      )
      RootComponent.Child.EditingFavourites(component)
    }
  }

  sealed interface Config : Parcelable {
    @Parcelize
    data object Favourite : Config

    @Parcelize
    data class Details(val id: Int, val forecasts: List<ForecastFs>) : Config

    @Parcelize
    data class Nextdays(val id: Int, val index: Int, val forecasts: List<ForecastFs>) : Config

    @Parcelize
    data object Search : Config

    @Parcelize
    data class Settings(val sourceOfOpening: SourceOfOpening) : Config

    @Parcelize
    data class Editing(val cities: List<EditingStore.State.CityItem>) : Config
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultRootComponent
  }
}