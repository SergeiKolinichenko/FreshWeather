package info.sergeikolinichenko.myapplication.presentation.components.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
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
import info.sergeikolinichenko.myapplication.presentation.components.details.DefaultDetailsComponent
import info.sergeikolinichenko.myapplication.presentation.components.editing.DefaultEditingComponent
import info.sergeikolinichenko.myapplication.presentation.components.favourite.DefaultFavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.components.nextdays.DefaultNextdaysComponent
import info.sergeikolinichenko.myapplication.presentation.components.search.DefaultSearchComponent
import info.sergeikolinichenko.myapplication.presentation.components.settings.DefaultSettingsComponent
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.SourceOfOpening
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

  override val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack(
    source = navigation,
    serializer = null,
    initialConfiguration = Config.Favourite,
    handleBackButton = true,
    childFactory = ::child
  )

  @OptIn(DelicateDecomposeApi::class)
  private fun child(
    config: Config,
    componentContext: ComponentContext
  ): RootComponent.Child = when (config) {

    is Config.Favourite -> {
      val component = favouriteComponentFactory.create(
        componentContext = componentContext,
        onSearchClick = { navigation.push(Config.Search) },
        onClickItemMenuSettings = { navigation.push(Config.Settings(sourceOfOpening = SourceOfOpening.OpenFromFavourite)) },
        onClickItemMenuEditing = { navigation.push(Config.Editing) },
        onItemClick = { navigation.push(Config.Details(id = it)) }
      )
      RootComponent.Child.Favourite(component)
    }

    is Config.Details -> {
      val component = detailsComponentFactory.create(
        componentContext = componentContext,
        id = config.id,
        onClickedBack = { navigation.pop() },
        onClickedSettings = { navigation.push(Config.Settings(sourceOfOpening = SourceOfOpening.OpenFromDetails)) },
        onClickedDay = { id, index -> navigation.push(Config.Nextdays(id = id, index = index))}
      )
      RootComponent.Child.Details(component)
    }

    is Config.Nextdays -> {
      val component = nextdaysComponentFactory.create(
        componentContext = componentContext,
        id = config.id,
        index = config.index,
        onSwipedTop = {
          navigation.pop()
        },
        onClickedClose = { navigation.popWhile{ it !is Config.Favourite } }
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
                (childStack.active.instance as? RootComponent.Child.Favourite)?.component?.reloadForecast()
              }
              SourceOfOpening.OpenFromDetails -> {
                (childStack.active.instance as? RootComponent.Child.Details)?.component?.reloadWeather()
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
        onBackClicked = { navigation.pop() },
        onClickedDone = {
          navigation.pop {
            (childStack.active.instance as? RootComponent.Child.Favourite)?.component?.reloadCities()
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
    data class Nextdays(val id: Int, val index: Int) : Config

    @Parcelize
    data object Search : Config

    @Parcelize
    data class Settings(val sourceOfOpening: SourceOfOpening) : Config

    @Parcelize
    data object Editing : Config
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultRootComponent
  }
}