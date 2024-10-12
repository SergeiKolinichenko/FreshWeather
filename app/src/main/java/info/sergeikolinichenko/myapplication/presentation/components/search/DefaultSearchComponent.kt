package info.sergeikolinichenko.myapplication.presentation.components.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.myapplication.presentation.stors.search.SearchStore
import info.sergeikolinichenko.myapplication.presentation.stors.search.SearchStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultSearchComponent @AssistedInject constructor(
  @Assisted("onClickBack") private val onClickBack: () -> Unit,
  @Assisted("onClickItem") private val onClickItem: () -> Unit,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: SearchStoreFactory
) : SearchComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create() }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect { label ->
        when (label) {
          SearchStore.Label.ClickedBack -> onClickBack()
          SearchStore.Label.ClickedCityItem -> onClickItem()
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<SearchStore.State> = store.stateFlow

  override fun onClickedClearLine() = store.accept(SearchStore.Intent.OnClickedClearLine)

  override fun onBackClicked() = store.accept(SearchStore.Intent.OnClickedBack)

  override fun onQueryChanged(query: String) =
    store.accept(SearchStore.Intent.OnQueryChanged(query))

  override fun onItemClicked(city: CityDto) =
    store.accept(SearchStore.Intent.OnClickedCity(city))

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("onClickBack") onClickBack: () -> Unit,
      @Assisted("onClickItem") onClickItem: () -> Unit,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultSearchComponent
  }
}