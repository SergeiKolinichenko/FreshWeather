package info.sergeikolinichenko.myapplication.presentation.screens.favourite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultFavouriteComponent @Inject constructor(
  private val onClickSearch: () -> Unit,
  private val onClickButton: () -> Unit,
  private val onClickCity: (City) -> Unit,
  private val storeFactory: FavouriteStoreFactory,
  private val componentContext: ComponentContext
) : FavouriteComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create() }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect { label ->
        when (label) {
          FavouriteStore.Label.ClickSearch -> onClickSearch()
          FavouriteStore.Label.ClickToButton -> onClickButton()
          is FavouriteStore.Label.OnClickCity -> onClickCity(label.city)
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<FavouriteStore.State> = store.stateFlow

  override fun onSearchClicked() {
    store.accept(FavouriteStore.Intent.SearchClicked)
  }

  override fun onButtonClicked() {
    store.accept(FavouriteStore.Intent.ButtonClicked)
  }

  override fun onItemClicked(city: City) {
    store.accept(FavouriteStore.Intent.ItemClicked(city))
  }
}