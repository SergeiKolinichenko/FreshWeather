package info.sergeikolinichenko.myapplication.presentation.screens.search

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

class DefaultSearchComponent @Inject constructor(
    private val onClickBack: () -> Unit,
    private val onClickItem: (City) -> Unit,
    private val savedToFavourite: () -> Unit,
    private val openingOptions: OpeningOptions,
    private val storeFactory: SearchStoreFactory,
    private val componentContext: ComponentContext
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

      override fun onItemClicked(city: City) =
          store.accept(SearchStore.Intent.CityClicked(city))
}