package info.sergeikolinichenko.myapplication.presentation.screens.editing.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Created by Sergei Kolinichenko on 14.08.2024 at 11:34 (GMT+3) **/

interface EditingFavouritesComponent {
  val model: StateFlow<EditingFavouritesStore.State>

  fun onBackClicked()
  fun onDoneClicked(cites: List<CityFs>)
}

class DefaultEditingFavouritesComponent @AssistedInject constructor(
  @Assisted("componentContext") private val componentContext: ComponentContext,
  @Assisted("cities") private val cities: List<EditingFavouritesStore.State.CityItem>,
  @Assisted("onClickedBack") private val onClickedBack: () -> Unit,
  @Assisted("onClickedDone") private val onClickedDone: (orderChanged: Boolean) -> Unit,
  private val storeFactory: EditingFavouritesStoreFactory
) : EditingFavouritesComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { storeFactory.create(cities) }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect {
        when (it) {
          EditingFavouritesStore.Label.OnBackClicked -> onClickedBack()
          is EditingFavouritesStore.Label.OnDoneClicked -> onClickedDone(it.orderChanged)
        }
      }
    }
  }


  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<EditingFavouritesStore.State> = store.stateFlow
  override fun onBackClicked() {
    store.accept(EditingFavouritesStore.Intent.OnBackClicked)
  }

  override fun onDoneClicked(cites: List<CityFs>) {
    store.accept(EditingFavouritesStore.Intent.OnDoneClicked(cites))
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("componentContext") componentContext: ComponentContext,
      @Assisted("cities") cities: List<EditingFavouritesStore.State.CityItem>,
      @Assisted("onClickedBack") onBackClicked: () -> Unit,
      @Assisted("onClickedDone") onClickedDone: (orderChanged: Boolean) -> Unit,
    ): DefaultEditingFavouritesComponent
  }
}