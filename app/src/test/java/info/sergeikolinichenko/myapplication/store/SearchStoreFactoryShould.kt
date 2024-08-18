package info.sergeikolinichenko.myapplication.store

import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStore
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStoreFactory
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.utils.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

/** Created by Sergei Kolinichenko on 22.07.2024 at 21:48 (GMT+3) **/

@OptIn(ExperimentalCoroutinesApi::class)
class SearchStoreFactoryShould: BaseUnitTestsRules() {

  private val mockSearchCitiesUseCase = mock<SearchCitiesUseCase>()
  private val mockChangeFavouriteStateUseCase = mock<ChangeFavouriteStateUseCase>()
  private val mockCity = mock<City>()
  private val mockCities = listOf(mockCity)

    val SUT = SearchStoreFactory(
      storeFactory = DefaultStoreFactory(),
      searchCities = mockSearchCitiesUseCase,
      changeFavouriteState = mockChangeFavouriteStateUseCase
    ).create()

  @Test
  fun `test for changing a query containing 1 letter`() = runTest {
    // Arrange
    // Act
    SUT.accept(SearchStore.Intent.OnQueryChanged(query = "t"))
    // Assert
    assert(SUT.state.state == SearchStore.State.SearchState.NotEnoughLetters)
  }

  @Test
  fun `test for changing a query containing 2 letters`() = runTest {
    // Arrange
    // Act
    SUT.accept(SearchStore.Intent.OnQueryChanged(query = "te"))
    // Assert
    assert(SUT.state.state == SearchStore.State.SearchState.NotEnoughLetters)
  }

  @Test
  fun `test for changing a query containing 3 letters`() = runTest {
    // Arrange
    `when`(mockSearchCitiesUseCase.invoke(any())).thenReturn(mockCities)
    // Act
    SUT.accept(SearchStore.Intent.OnQueryChanged(query = "tes"))
    val result = SUT.state.state as SearchStore.State.SearchState.SuccessLoaded
    // Assert
    assert(SUT.state.state == SearchStore.State.SearchState.SuccessLoaded(mockCities))
    assert(result.cities == mockCities)
  }

  @Test
  fun `test on query changed with empty result`() = runTest {
    // Arrange
    `when`(mockSearchCitiesUseCase.invoke(any())).thenReturn(emptyList())
    // Act
    SUT.accept(SearchStore.Intent.OnQueryChanged(query = "test"))
    // Assert
    assert(SUT.state.state == SearchStore.State.SearchState.Empty)
  }

  @Test
  fun `test on query changed with error`() = runTest {
    // Arrange
    `when`(SUT.accept(SearchStore.Intent.OnQueryChanged(query = "test"))).thenThrow()
    // Act
    SUT.accept(SearchStore.Intent.OnQueryChanged(query = "test"))
    // Assert
    assert(SUT.state.state == SearchStore.State.SearchState.Error)
  }

  @Test
  fun `test on clicked back`() = runTest {
    // Arrange
    // Act
    val result = SUT.labels.test()
    SUT.accept(SearchStore.Intent.OnClickedBack)
    // Assert
    assert(result == listOf(SearchStore.Label.ClickedBack))
  }

  @Test
  fun `test on clicked clear line`() = runTest {
    // Arrange
    // Act
    SUT.accept(SearchStore.Intent.OnClickedClearLine)
    val result = SUT.state.state as SearchStore.State.SearchState.SuccessLoaded
    // Assert
    assert(SUT.state.query == "")
    assert(result.cities == emptyList<City>())
  }

  @Test
  fun `test on clicked city`() = runTest {
    // Arrange
    val city = City(1, "Sofia", "Sofia region", "Bulgaria", 1.0, 1.0)
    // Act
    val result = SUT.labels.test()
    SUT.accept(SearchStore.Intent.OnClickedCity(city.toTestCityForScreen()))
    // Assert
//    assert(result == listOf(SearchStore.Label.SavedToFavorite))
  }
}

private fun City.toTestCityForScreen() = CityFs(
  id = id,
  name = name,
  region = region,
  country = country,
  lat = lat,
  lon = lon
)