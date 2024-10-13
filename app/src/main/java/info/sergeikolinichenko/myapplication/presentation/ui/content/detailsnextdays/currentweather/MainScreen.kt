package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.currentweather

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.entity.HourForecastFs
import info.sergeikolinichenko.myapplication.presentation.stores.details.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.AnimatingHourlyWeatherForecast
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.Charts
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.HumidityWindPressure
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.SunAndMoon
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.UvIndexAndCloudiness
import info.sergeikolinichenko.myapplication.utils.convertLongToCalendarWithTz
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

/** Created by Sergei Kolinichenko on 20.08.2024 at 17:31 (GMT+3) **/

@Composable
internal fun MainScreen(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
  onDayClicked: (Int) -> Unit,
  onBackClicked: () -> Unit,
  onSettingsClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit
) {

  var overScrollTop by remember { mutableStateOf(false) }
  var overScrollBottom by remember { mutableStateOf(false) }
  val scrollState = rememberScrollState()
  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        if (delta > 0 && scrollState.value == 0) {
          overScrollTop = true
        } else if (delta < 0 && scrollState.value == scrollState.maxValue) {
          overScrollBottom = true
        }
        return Offset.Zero
      }

      override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        overScrollTop = false
        overScrollBottom = false
        return super.onPostFling(consumed, available)
      }
    }
  }


  var swipeLeft by remember { mutableStateOf(false) }
  var swipeRight by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .background(MaterialTheme.colorScheme.surfaceBright)
      .padding(16.dp)
      .fillMaxSize()
      .nestedScroll(nestedScrollConnection)
      .verticalScroll(scrollState)
      .pointerInput(Unit) {
        detectHorizontalDragGestures(
          onDragEnd = {
            swipeLeft = false
            swipeRight = false
          },
          onHorizontalDrag = { change, dragAmount ->
            if (dragAmount > 0) {
              swipeLeft = true
            } else if (dragAmount < 0) {
              swipeRight = true
            }
            change.consume()
          }
        )
      }
  ) {

    if (state.citiesState is DetailsStore.State.CitiesState.Loaded) {

      val city = state.citiesState.cities.first { it.id == state.citiesState.id }

      if (state.forecastState is DetailsStore.State.ForecastsState.Loaded) {

        val forecast = state.forecastState.forecasts.first { it.id == city.id }

        TopBar(
          modifier = Modifier,
          state = state,
          onBackButtonClick = {
            onBackClicked()
          },
          onSettingsClicked = {
            onSettingsClicked()
          }
        )

        CurrentWeather(
          modifier = modifier
            .padding(top = 24.dp),
          forecast = forecast,
          city = city
        )

        // Humidity, wind, pressure
        HumidityWindPressure(
          modifier = Modifier
            .padding(top = 16.dp),
          humidity = forecast.currentForecast.humidity,
          windDir = forecast.currentForecast.windDir,
          windSpeed = forecast.currentForecast.windSpeed,
          pressure = forecast.currentForecast.pressure
        )

        // UV index and cloudiness
        UvIndexAndCloudiness(
          modifier = Modifier
            .padding(top = 24.dp),
          uvIndex = forecast.currentForecast.uvIndex,
          cloudCover = forecast.currentForecast.cloudCover,
          precipitation = if (forecast.currentForecast.precipProb > 0) forecast.currentForecast.precip else null
        )

        // Hourly weather forecast
        val firstIndex = forecast.upcomingHours.indexOfFirst {
          convertLongToCalendarWithTz(it.date, forecast.tzId) >= Calendar.getInstance()
        }

        val list = if (firstIndex - 1 == WRONG_INDEX_OF_FORECAST) null
        else forecast.upcomingHours.subList(
          firstIndex - 1,
          firstIndex + MAXIMUM_HOURS_HOURLY_WEATHER
        )

        AnimatingHourlyWeatherForecast(
          modifier = Modifier
            .padding(top = 16.dp),
          list = list,
          tzId = forecast.tzId
        )

        DailyWeatherForecast(
          modifier = Modifier
            .padding(top = 16.dp),
          forecast = forecast,
          onDayClicked = { onDayClicked(it) }
        )

        SunAndMoon(
          modifier = Modifier
            .padding(top = 16.dp),
          sunrise = forecast.upcomingDays.first().sunrise,
          sunset = forecast.upcomingDays.first().sunset,
          moonrise = forecast.upcomingDays.first().moonrise,
          moonset = forecast.upcomingDays[1].moonset,
          moonPhase = forecast.upcomingDays.first().moonPhase,
          tzId = forecast.tzId
        )

        Charts(
          modifier = Modifier
            .padding(top = 16.dp),
          list = forecast.getSublistForecastHourly(),
          tzId = forecast.tzId
        )
      }
    }
  }

  if (overScrollBottom) {
    onDayClicked(INDEX_OF_TOMORROW)
  }

  LaunchedEffect(swipeLeft, swipeRight) {

    if (swipeLeft) {
      onSwipeLeft()
    }
    if (swipeRight) {
      onSwipeRight()
    }
  }

}

private fun ForecastFs.getSublistForecastHourly(): List<HourForecastFs> {

  val now = LocalDateTime.now(ZoneId.of(this.tzId))

  return this.upcomingHours.filter { item ->
    val itemHour = LocalDateTime.ofInstant(
      Instant.ofEpochSecond(item.date),
      ZoneId.of(this.tzId)
    )
    itemHour > now.minusHours(2) && itemHour < now.plusHours(MAXIMUM_HOURS_CHART)
  }
}

private const val MAXIMUM_HOURS_HOURLY_WEATHER = 23
private const val WRONG_INDEX_OF_FORECAST = -1
private const val MAXIMUM_HOURS_CHART = 25L
private const val INDEX_OF_TOMORROW = 1