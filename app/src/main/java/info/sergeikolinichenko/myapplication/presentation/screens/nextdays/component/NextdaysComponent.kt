package info.sergeikolinichenko.myapplication.presentation.screens.nextdays.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.store.NextdaysStore
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.store.NextdaysStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Created by Sergei Kolinichenko on 11.08.2024 at 13:53 (GMT+3) **/

interface NextdaysComponent {

  val model: StateFlow<NextdaysStore.State>

  fun onCloseClicked()
  fun onSwipeLeft()
  fun onSwipeRight()
  fun onSwipeTop()
  fun onDayClicked(index: Int)
}

class DefaultNextdaysComponent @AssistedInject constructor(
  @Assisted("onSwipedTop") private val onSwipedTop: () -> Unit,
  @Assisted("onClickedClose") private val onClickedClose:() -> Unit,
  @Assisted("id") private val id: Int,
  @Assisted("index") private val index: Int,
  @Assisted("forecast") private val forecast: ForecastFs,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: NextdaysStoreFactory
) : NextdaysComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore{ storeFactory.create(
    id = id,
    index = index,
    forecast = forecast
  ) }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect{
        when(it){
          is NextdaysStore.Label.OnSwipedTop -> onSwipedTop()
          is NextdaysStore.Label.OnClickedClose -> onClickedClose()
        }
      }
    }
  }



  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<NextdaysStore.State> = store.stateFlow

  override fun onCloseClicked() {
    store.accept(NextdaysStore.Intent.OnClickClose)
  }

  override fun onSwipeLeft() {
    store.accept(NextdaysStore.Intent.OnSwipeLeft)
  }

  override fun onSwipeRight() {
    store.accept(NextdaysStore.Intent.OnSwipeRight)
  }

  override fun onSwipeTop() {
    store.accept(NextdaysStore.Intent.OnSwipeTop)
  }

  override fun onDayClicked(index: Int) {
    store.accept(NextdaysStore.Intent.OnDayClicked(index))
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("onSwipedTop") onSwipedTop: () -> Unit,
      @Assisted("onClickedClose") onClickedClose:() -> Unit,
      @Assisted("id") id: Int,
      @Assisted("index") index: Int,
      @Assisted("forecast") forecast: ForecastFs,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultNextdaysComponent
  }
}

