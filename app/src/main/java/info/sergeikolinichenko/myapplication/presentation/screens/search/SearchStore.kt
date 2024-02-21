package info.sergeikolinichenko.myapplication.presentation.screens.search

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:55 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.myapplication.presentation.screens.search.SearchStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.search.SearchStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.search.SearchStore.State

internal interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {
    }

    data class State(val todo: Unit = Unit)

    sealed interface Label {
    }
}

internal class SearchStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): SearchStore =
        object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
    }

    private sealed interface Msg {
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
        }

        override fun executeAction(action: Action, getState: () -> State) {
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State = State()
    }
}
