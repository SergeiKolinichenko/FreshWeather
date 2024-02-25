package info.sergeikolinichenko.myapplication.presentation.screens.details

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

class DefaultDetailsComponent @Inject constructor(
    private val onClickBack: () -> Unit,
    private val city: City,
    private val storeFactory: DetailsStoreFactory,
    private val componentContext: ComponentContext
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
}