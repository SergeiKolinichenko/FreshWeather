package info.sergeikolinichenko.myapplication.presentation.screens.favourite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.CityToScreen
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultFavouriteComponent @AssistedInject constructor(
  @Assisted("onClickSearch") private val onClickSearch: () -> Unit,
  @Assisted("onClickButton") private val onClickButton: () -> Unit,
  @Assisted("onClickCity") private val onClickCity: (CityToScreen) -> Unit,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: FavouriteStoreFactory
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

  override fun onItemClicked(city: CityToScreen) {
    store.accept(FavouriteStore.Intent.ItemClicked(city))
  }
  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("onClickSearch") onClickSearch: () -> Unit,
      @Assisted("onClickButton") onClickButton: () -> Unit,
      @Assisted("onClickCity") onClickCity: (CityToScreen) -> Unit,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultFavouriteComponent
  }
}