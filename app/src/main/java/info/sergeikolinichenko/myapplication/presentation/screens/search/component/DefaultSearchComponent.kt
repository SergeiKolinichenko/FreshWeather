package info.sergeikolinichenko.myapplication.presentation.screens.search.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStore
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultSearchComponent @AssistedInject constructor(
  @Assisted("onClickBack") private val onClickBack: () -> Unit,
  @Assisted("onClickItem") private val onClickItem: (CityForScreen) -> Unit,
  @Assisted("savedToFavourite") private val savedToFavourite: () -> Unit,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: SearchStoreFactory
) : SearchComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create() }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect { label ->
        when (label) {
          SearchStore.Label.ClickBack -> onClickBack()
          SearchStore.Label.SavedToFavorite -> savedToFavourite()
          is SearchStore.Label.OpenCityForecast -> onClickItem(label.city)
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<SearchStore.State> = store.stateFlow

  override fun onSearchClicked() = store.accept(SearchStore.Intent.ClickSearch)

  override fun onBackClicked() = store.accept(SearchStore.Intent.ClickBack)

  override fun onQueryChanged(query: String) =
    store.accept(SearchStore.Intent.SearchQueryChanged(query))

  override fun onItemClicked(city: CityForScreen) =
    store.accept(SearchStore.Intent.CityClicked(city))

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("onClickBack") onClickBack: () -> Unit,
      @Assisted("onClickItem") onClickItem: (CityForScreen) -> Unit,
      @Assisted("savedToFavourite") savedToFavourite: () -> Unit,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultSearchComponent
  }
}