package info.sergeikolinichenko.myapplication.presentation.screens.details.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.details.SourceOfOpening
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:07 (GMT+3) **/

interface DetailsComponent {

  val model: StateFlow<DetailsStore.State>
  fun onBackClicked()
  fun onDayClicked(id: Int, index: Int)
  fun onSettingsClicked()
  fun reloadWeather()
  fun onSwipeLeft()
  fun onSwipeRight()
}

class DefaultDetailsComponent @AssistedInject constructor(
  @Assisted("onBackClicked") private val onClickedBack: () -> Unit,
  @Assisted("onClickedSettings") private val onClickedSettings: (sourceOfOpening: SourceOfOpening) -> Unit,
  @Assisted("OnDayClicked") private val onClickedDay: (Int, Int, List<ForecastFs>) -> Unit,
  @Assisted("id") private val id: Int,
  @Assisted("forecasts") forecasts: List<ForecastFs>,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: DetailsStoreFactory
) : DetailsComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create(id = id, forecasts = forecasts) }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect { label ->
        when (label) {
          DetailsStore.Label.OnBackClicked -> onClickedBack()
          DetailsStore.Label.OnSettingsClicked -> onClickedSettings(SourceOfOpening.OpenFromDetails)
          is DetailsStore.Label.OnDayClicked -> onClickedDay(label.id, label.index, label.forecasts)
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<DetailsStore.State> = store.stateFlow

  override fun onBackClicked() = store.accept(DetailsStore.Intent.OnBackClicked)
  override fun onDayClicked(id: Int, index: Int) {
    store.accept(DetailsStore.Intent.OnDayClicked(id = id, index = index))
  }

  override fun onSettingsClicked() {
    store.accept(DetailsStore.Intent.OnSettingsClicked)
  }

  override fun reloadWeather() {
    store.accept(DetailsStore.Intent.ReloadWeather)
  }

  override fun onSwipeLeft() {
    store.accept(DetailsStore.Intent.OnSwipeLeft)
  }

  override fun onSwipeRight() {
    store.accept(DetailsStore.Intent.OnSwipeRight)
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("id") id: Int,
      @Assisted("forecasts") forecasts: List<ForecastFs>,
      @Assisted("onBackClicked") onClickedBack: () -> Unit,
      @Assisted("onClickedSettings") onClickedSettings: (sourceOfOpening: SourceOfOpening) -> Unit,
      @Assisted("OnDayClicked") onClickedDay: (Int, Int, List<ForecastFs>) -> Unit,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultDetailsComponent
  }
}