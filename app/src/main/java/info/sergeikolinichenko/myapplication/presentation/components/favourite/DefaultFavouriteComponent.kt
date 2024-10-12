package info.sergeikolinichenko.myapplication.presentation.components.favourite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.presentation.stors.favourites.FavouriteStore
import info.sergeikolinichenko.myapplication.presentation.stors.favourites.FavouriteStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultFavouriteComponent @AssistedInject constructor(
  @Assisted("onSearchClicked") private val onClickedSearch: () -> Unit,
  @Assisted("onItemClicked") private val onClickedItem: (Int) -> Unit,
  @Assisted("onClickItemMenuSettings") private val onClickItemMenuSettings: () -> Unit,
  @Assisted("onClickItemMenuEditing") private val onClickItemMenuEditing: () -> Unit,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: FavouriteStoreFactory
) : FavouriteComponent, ComponentContext by componentContext {

  private val store: FavouriteStore = instanceKeeper.getStore { storeFactory.create() }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect { label ->
        when (label) {
          FavouriteStore.Label.OnSearchClicked -> {
            onClickedSearch()
          }
          is FavouriteStore.Label.OnItemClicked -> {
            onClickedItem(label.id)
          }
          FavouriteStore.Label.OnItemMenuSettingsClicked ->
            onClickItemMenuSettings()

          is FavouriteStore.Label.OnItemMenuEditingClicked ->
            onClickItemMenuEditing()
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

  override fun reloadForecast() {
    store.accept(FavouriteStore.Intent.ReloadWeather)
  }

  override fun reloadCities() {
    store.accept(FavouriteStore.Intent.ReloadCities)
  }

  override fun onItemMenuSettingsClicked() {
    store.accept(FavouriteStore.Intent.ItemMenuSettingsClicked)
  }

  override fun onItemMenuEditingClicked() {
    store.accept(FavouriteStore.Intent.ItemMenuEditingClicked)
  }

  override fun onItemClicked(id: Int) {
    store.accept(FavouriteStore.Intent.ItemCityClicked(id = id)) }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("onSearchClicked") onSearchClick: () -> Unit,
      @Assisted("onItemClicked") onItemClick: (Int) -> Unit,
      @Assisted("onClickItemMenuSettings") onClickItemMenuSettings: () -> Unit,
      @Assisted("onClickItemMenuEditing") onClickItemMenuEditing: () -> Unit,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultFavouriteComponent
  }
}