package info.sergeikolinichenko.myapplication.store

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import kotlinx.coroutines.ExperimentalCoroutinesApi

/** Created by Sergei Kolinichenko on 22.07.2024 at 21:48 (GMT+3) **/

@OptIn(ExperimentalCoroutinesApi::class)
class SearchStoreFactoryShould: BaseUnitTestsRules() {

//  private val mockSearchCitiesUseCase = mock<SearchCitiesUseCase>()
//  private val mockChangeFavouriteStateUseCase = mock<ChangeFavouriteStateUseCase>()
//  private val mockCity = mock<City>()
//  private val mockCities = listOf(mockCity)
//
//    private val systemUnderTest = SearchStoreFactory(
//      storeFactory = DefaultStoreFactory(),
//      searchCities = mockSearchCitiesUseCase,
//      changeFavouriteState = mockChangeFavouriteStateUseCase
//    ).create()
//
//
//
//  @Test
//  fun `test for changing a query containing 3 letters`() = runTest {
//    // Arrange
//    `when`(mockSearchCitiesUseCase.invoke(any())).thenReturn(mockCities)
//    // Act
//    systemUnderTest.accept(SearchStore.Intent.OnQueryChanged(query = "tes"))
//    val result = systemUnderTest.state.state as SearchStore.State.SearchState.SuccessLoaded
//    // Assert
//    assert(systemUnderTest.state.state == SearchStore.State.SearchState.SuccessLoaded(mockCities))
//    assert(result.cities == mockCities)
//  }
//
//  @Test
//  fun `test on query changed with empty result`() = runTest {
//    // Arrange
//    `when`(mockSearchCitiesUseCase.invoke(any())).thenReturn(emptyList())
//    // Act
//    systemUnderTest.accept(SearchStore.Intent.OnQueryChanged(query = "test"))
//    // Assert
//    assert(systemUnderTest.state.state == SearchStore.State.SearchState.Empty)
//  }
//
//  @Test
//  fun `test on query changed with error`() = runTest {
//    // Arrange
//    `when`(systemUnderTest.accept(SearchStore.Intent.OnQueryChanged(query = "test"))).thenThrow()
//    // Act
//    systemUnderTest.accept(SearchStore.Intent.OnQueryChanged(query = "test"))
//    // Assert
//    assert(systemUnderTest.state.state == SearchStore.State.SearchState.Error)
//  }
//
//  @Test
//  fun `test on clicked back`() = runTest {
//    // Arrange
//    // Act
//    val result = systemUnderTest.labels.test()
//    systemUnderTest.accept(SearchStore.Intent.OnClickedBack)
//    // Assert
//    assert(result == listOf(SearchStore.Label.ClickedBack))
//  }
//
//  @Test
//  fun `test on clicked clear line`() = runTest {
//    // Arrange
//    // Act
//    systemUnderTest.accept(SearchStore.Intent.OnClickedClearLine)
//    val result = systemUnderTest.state.state as SearchStore.State.SearchState.SuccessLoaded
//    // Assert
//    assert(systemUnderTest.state.query == "")
//    assert(result.cities == emptyList<City>())
//  }
//
//  @Test
//  fun `test on clicked city`() = runTest {
//    // Arrange
//    val city = City(1, "Sofia", "Sofia region", "Bulgaria", 1.0, 1.0)
//    // Act
//    val result = systemUnderTest.labels.test()
//    systemUnderTest.accept(SearchStore.Intent.OnClickedCity(city.toTestCityForScreen()))
//    // Assert
////    assert(result == listOf(SearchStore.Label.SavedToFavorite))
//  }
}

private fun City.toTestCityForScreen() = CityFs(
  id = id,
  name = name,
  region = region,
  country = country,
  lat = lat,
  lon = lon
)