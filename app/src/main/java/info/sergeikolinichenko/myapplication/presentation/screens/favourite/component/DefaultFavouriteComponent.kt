package info.sergeikolinichenko.myapplication.presentation.screens.favourite.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import info.sergeikolinichenko.myapplication.utils.toCity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultFavouriteComponent @AssistedInject constructor(
  @Assisted("onClickSearch") onSearchClicked: () -> Unit,
  @Assisted("onClickItemMenuSettings") onClickItemMenuSettings: () -> Unit,
  @Assisted("onItemClicked") onItemClicked: (Int) -> Unit,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: FavouriteStoreFactory
) : FavouriteComponent, ComponentContext by componentContext {

  private val store: FavouriteStore = instanceKeeper.getStore { storeFactory.create() }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect { label ->
        when (label) {
          FavouriteStore.Label.OnClickSearch -> onSearchClicked()
          is FavouriteStore.Label.OnClickCity -> onItemClicked(label.id)
          FavouriteStore.Label.OnClickItemMenuSettings -> onClickItemMenuSettings()
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<FavouriteStore.State> = store.stateFlow

  override fun onSearchClicked() {
    store.accept(FavouriteStore.Intent.SearchClicked)
  }

  override fun onActionMenuClicked() {
    store.accept(FavouriteStore.Intent.ActionMenuClicked)
  }

  override fun onClosingActionMenu() {
    store.accept(FavouriteStore.Intent.ClosingActionMenu)
  }

  override fun reloadWeather() {

    val list: List<City> = store.state.cityItems.map { it.city.toCity() }

    store.accept(FavouriteStore.Intent.ReloadWeather(list))
  }

  override fun onItemMenuSettingsClicked() {
    store.accept(FavouriteStore.Intent.ItemMenuSettingsClicked)
  }

  override fun onItemClicked(id: Int) {
    store.accept(FavouriteStore.Intent.ItemCityClicked(id = id)) }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("onClickSearch") onClickSearch: () -> Unit,
      @Assisted("onClickItemMenuSettings") onClickItemMenuSettings: () -> Unit,
      @Assisted("onItemClicked") onItemClicked: (Int) -> Unit,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultFavouriteComponent
  }
}