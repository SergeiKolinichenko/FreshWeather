package info.sergeikolinichenko.myapplication.presentation.screens.details

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

class DefaultDetailsComponent @AssistedInject constructor(
  @Assisted("onClickBack") private val onClickBack: () -> Unit,
  @Assisted("city") private val city: CityToScreen,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: DetailsStoreFactory
) : DetailsComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create(city) }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect { label ->
        when (label) {
          DetailsStore.Label.OnBackClicked -> onClickBack()
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<DetailsStore.State> = store.stateFlow
  override fun onBackClicked() = store.accept(DetailsStore.Intent.OnBackClicked)
  override fun onChangeFavouriteStatusClicked() =
    store.accept(DetailsStore.Intent.ChangeFavouriteStatusClicked)
  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("onClickBack") onClickBack: () -> Unit,
      @Assisted("city") city: CityToScreen,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultDetailsComponent
  }
}