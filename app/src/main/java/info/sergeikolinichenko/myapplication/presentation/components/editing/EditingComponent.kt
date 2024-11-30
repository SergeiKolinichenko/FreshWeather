package info.sergeikolinichenko.myapplication.presentation.components.editing

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.presentation.stores.editing.EditingStore
import info.sergeikolinichenko.myapplication.presentation.stores.editing.EditingStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Created by Sergei Kolinichenko on 14.08.2024 at 11:34 (GMT+3) **/

interface EditingComponent {
  val model: StateFlow<EditingStore.State>

  fun listOfCitiesChanged(items: List<EditingStore.State.CityItem>)
  fun removeItemFromListOfCities(id: Int)
  fun onBackClicked()
  fun onDoneClicked()
}

class DefaultEditingComponent @AssistedInject constructor(
  @Assisted("componentContext") private val componentContext: ComponentContext,
  @Assisted("onClickedBack") private val onClickedBack: () -> Unit,
  @Assisted("onClickedDone") private val onClickedDone: () -> Unit,
  private val storeFactory: EditingStoreFactory
) : EditingComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create() }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect {
        when (it) {
          EditingStore.Label.OnBackClicked -> onClickedBack()
          is EditingStore.Label.OnDoneClicked -> {
            onClickedDone()
          }
        }
      }
    }
  }


  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<EditingStore.State> = store.stateFlow

  override fun listOfCitiesChanged(items: List<EditingStore.State.CityItem>) {
    store.accept(EditingStore.Intent.ListOfCitiesChanged(cityItems = items))
  }

  override fun removeItemFromListOfCities(id: Int) {
    store.accept(EditingStore.Intent.RemoveItemFromListOfCities(id = id))
  }

  override fun onBackClicked() {
    store.accept(EditingStore.Intent.OnBackClicked)
  }

  override fun onDoneClicked() {
    store.accept(EditingStore.Intent.OnDoneClicked)
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("componentContext") componentContext: ComponentContext,
      @Assisted("onClickedBack") onBackClicked: () -> Unit,
      @Assisted("onClickedDone") onClickedDone: () -> Unit,
    ): DefaultEditingComponent
  }
}