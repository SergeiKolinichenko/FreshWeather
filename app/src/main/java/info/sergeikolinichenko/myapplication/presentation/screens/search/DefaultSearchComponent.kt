package info.sergeikolinichenko.myapplication.presentation.screens.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.CityScreen
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultSearchComponent @AssistedInject constructor(
  @Assisted("onClickBack") private val onClickBack: () -> Unit,
  @Assisted("onClickItem") private val onClickItem: (CityScreen) -> Unit,
  @Assisted("savedToFavourite") private val savedToFavourite: () -> Unit,
  @Assisted("openingOptions") private val openingOptions: OpeningOptions,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: SearchStoreFactory
) : SearchComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create(openingOptions) }
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

  override fun onItemClicked(city: CityScreen) =
    store.accept(SearchStore.Intent.CityClicked(city))

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("onClickBack") onClickBack: () -> Unit,
      @Assisted("onClickItem") onClickItem: (CityScreen) -> Unit,
      @Assisted("savedToFavourite") savedToFavourite: () -> Unit,
      @Assisted("openingOptions") openingOptions: OpeningOptions,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultSearchComponent
  }
}