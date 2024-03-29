package info.sergeikolinichenko.myapplication.presentation.screens.details

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

class DefaultDetailsComponent @AssistedInject constructor(
  @Assisted("onClickBack") private val onClickBack: () -> Unit,
  @Assisted("city") private val city: CityScreen,
  @Assisted("numberGradient") private val numberGradient: Int = 0,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: DetailsStoreFactory
) : DetailsComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create(city, numberGradient) }
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
      @Assisted("city") city: CityScreen,
      @Assisted("numberGradient") gradient: Int,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultDetailsComponent
  }
}