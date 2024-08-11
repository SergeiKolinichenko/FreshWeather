package info.sergeikolinichenko.myapplication

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.nhaarman.mockitokotlin2.mock
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetWeatherUseCase
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.component.DefaultFavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStoreFactory
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DefaultFavouriteComponentShould: BaseUnitTestsRules(
  dispatcher = StandardTestDispatcher()
) {

//  @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
//  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  // region constants
  private val lifecycle = LifecycleRegistry()
  private val getFavouriteCities = mock<GetFavouriteCitiesUseCase>()
  private val getWeatherUseCase = mock<GetWeatherUseCase>()

  private val componentContext = DefaultComponentContext(lifecycle)

  private val storeFactory = FavouriteStoreFactory(
    storeFactory = DefaultStoreFactory(),
    getFavouriteCities = getFavouriteCities,
    getWeatherUseCase = getWeatherUseCase
  )
//private val store = componentContext.instanceKeeper.getStore { storeFactory.create() }
  // endregion constants

  private val component = DefaultFavouriteComponent(
//    componentContext = DefaultComponentContext(
//      lifecycle = lifecycle,
//      stateKeeper = StateKeeperDispatcher(),
//      instanceKeeper = InstanceKeeperDispatcher()
//    ),
    componentContext = componentContext,
    storeFactory = storeFactory,
    onSearchClicked = {},
    onClickItemMenuSettings = {},
    onItemClicked = { _, _ -> }
  )

  @Before
  fun before() {
    isAssertOnMainThreadEnabled = false
  }

  @After
  fun after() {
    isAssertOnMainThreadEnabled = true
  }

  @Test
  fun `initial state`() = runTest {
    // Arrange
    // Act
      component.onSearchClicked()
    // Assert
//    verify(store.accept(FavouriteStore.Intent.SearchClicked), times(1))
  }

  //region test methods
  // region helper methods
  interface FavouriteStoreForTest : FavouriteStore

  class FavouriteStoreFactoryForTest {

//    private val storeFactory = mock<StoreFactory>()
//    fun create(): FavouriteStore = object : FavouriteStore,
//      Store<FavouriteStore.Intent, FavouriteStore.State, FavouriteStore.Label> by storeFactory.create(
//        name = "test",
//        initialState = FavouriteStore.State(),
//        bootstrapper = null,
//        executorFactory = null,
//        reducer = null
//      ){}

    private inner class BootstrapperImpl

  }
  // endregion helper methods


}

