package info.sergeikolinichenko.myapplication.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.details.SourceOfOpening
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DefaultDetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.component.DefaultEditingFavouritesComponent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.component.DefaultFavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.component.DefaultNextdaysComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.component.DefaultSearchComponent
import info.sergeikolinichenko.myapplication.presentation.screens.settings.component.DefaultSettingsComponent
import info.sergeikolinichenko.myapplication.utils.ORDER_LIST_CITIES_CHANGED
import kotlinx.parcelize.Parcelize

class DefaultRootComponent @AssistedInject constructor(
  private val detailsComponentFactory: DefaultDetailsComponent.Factory,
  private val nextdaysComponentFactory: DefaultNextdaysComponent.Factory,
  private val favouriteComponentFactory: DefaultFavouriteComponent.Factory,
  private val searchComponentFactory: DefaultSearchComponent.Factory,
  private val settingsComponentFactory: DefaultSettingsComponent.Factory,
  private val editingFavouritesComponentFactory: DefaultEditingFavouritesComponent.Factory,
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
          navigation.push(Config.EditingFavourites(cities))
        },
        onItemClick = { id ->
          navigation.push(Config.Details(id = id))
        }
      )
      RootComponent.Child.Favourite(component)
    }

    is Config.Details -> {
      val component = detailsComponentFactory.create(
        componentContext = componentContext,
        id = config.id,
        onClickedBack = { navigation.pop() },
        onClickedSettings = { navigation.push(Config.Settings(sourceOfOpening = SourceOfOpening.OpenFromDetails)) },
        onClickedDay = { id, index, forecast ->
          navigation.push(Config.Nextdays(id = id, index = index, forecast = forecast))
        }
      )
      RootComponent.Child.Details(component)
    }

    is Config.Nextdays -> {
      val component = nextdaysComponentFactory.create(
        componentContext = componentContext,
        id = config.id,
        index = config.index,
        forecast = config.forecast,
        onClickClose = { id, forecast ->
          navigation.pop()
        },
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
                (stack.active.instance as? RootComponent.Child.Favourite)?.component?.reloadWeather()
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

    is Config.EditingFavourites -> {
      val component = editingFavouritesComponentFactory.create(
        componentContext = componentContext,
        cities = config.cities,
        onBackClicked = { navigation.pop() },
        onClickedDone = {
          navigation.pop {
            if (ORDER_LIST_CITIES_CHANGED) (stack.active.instance as? RootComponent.Child.Favourite)?.component?.reloadCities()
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
    data class Details(val id: Int) : Config

    @Parcelize
    data class Nextdays(val id: Int, val index: Int, val forecast: ForecastFs) : Config

    @Parcelize
    data object Search : Config

    @Parcelize
    data class Settings(val sourceOfOpening: SourceOfOpening) : Config

    @Parcelize
    data class EditingFavourites(val cities: List<EditingFavouritesStore.State.CityItem>) : Config
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultRootComponent
  }
}