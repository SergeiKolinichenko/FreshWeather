package info.sergeikolinichenko.myapplication.presentation.root

import android.os.Parcelable
import android.util.Log
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
import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DefaultDetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.component.DefaultFavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.component.DefaultSearchComponent
import info.sergeikolinichenko.myapplication.presentation.screens.settings.component.DefaultSettingsComponent
import kotlinx.parcelize.Parcelize

class DefaultRootComponent @AssistedInject constructor(
  private val detailsComponentFactory: DefaultDetailsComponent.Factory,
  private val favouriteComponentFactory: DefaultFavouriteComponent.Factory,
  private val searchComponentFactory: DefaultSearchComponent.Factory,
  private val settingsComponentFactory: DefaultSettingsComponent.Factory,
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
        onClickSearch = { navigation.push(Config.Search) },
        onClickItemMenuSettings = { navigation.push(Config.Settings) },
        onItemClicked = { id ->
          navigation.push(Config.Details(id = id))
        }
      )
      RootComponent.Child.Favourite(component)
    }

    is Config.Details -> {
      val component = detailsComponentFactory.create(
        componentContext = componentContext,
        id = config.id,
        onClickBack = { navigation.pop() }
      )
      RootComponent.Child.Details(component)
    }

    is Config.Search -> {
      val component = searchComponentFactory.create(
        componentContext = componentContext,
        onClickBack = { navigation.pop() },
        onClickItem = { navigation.pop() },
      )
      RootComponent.Child.Search(component)
    }

    Config.Settings -> {
      val component = settingsComponentFactory.create(
        componentContext = componentContext,
        onClickBack = { navigation.pop() },
        settingsSaved = {
          navigation.pop {
            (stack.active.instance as? RootComponent.Child.Favourite)?.component?.reloadWeather()
          }
        }
      )
      RootComponent.Child.Settings(component)
    }

  }

  sealed interface Config : Parcelable {
    @Parcelize
    data object Favourite : Config

    @Parcelize
    data class Details(val id: Int) : Config

    @Parcelize
    data object Search : Config

    @Parcelize
    data object Settings : Config
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultRootComponent
  }
}