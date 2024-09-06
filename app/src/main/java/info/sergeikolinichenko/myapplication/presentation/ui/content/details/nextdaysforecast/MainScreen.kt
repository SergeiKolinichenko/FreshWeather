package info.sergeikolinichenko.myapplication.presentation.ui.content.details.nextdaysforecast

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.store.NextdaysStore
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.AnimatingHourlyWeatherForecast
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.Charts
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.HumidityWindPressure
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.SunAndMoon
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.UvIndexAndCloudiness
import info.sergeikolinichenko.myapplication.utils.DividingLine

/** Created by Sergei Kolinichenko on 06.09.2024 at 10:26 (GMT+3) **/

@Composable
internal fun MainScreen(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
  onDayClicked: (Int) -> Unit,
  onCloseClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit,
  onSwipeTop: () -> Unit,
  onSwipeBottom: () -> Unit
) {

  if (state.citiesState is NextdaysStore.State.CitiesState.Loaded) {
    if (state.forecastState is NextdaysStore.State.ForecastState.Loaded) {

      val forecast = state.forecastState.forecasts.first { it.id == state.citiesState.id }

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

      LaunchedEffect(overScrollTop, overScrollBottom) {
        if (overScrollTop) {
          onSwipeTop()
        }
        if (overScrollBottom) {
          onSwipeBottom()
        }
      }

      LaunchedEffect(swipeLeft, swipeRight) {
        if (swipeLeft) {
          onSwipeLeft()
        }
        if (swipeRight) {
          onSwipeRight()
        }
      }


      Column(
        modifier = modifier
          .fillMaxWidth()
          .clip(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
          .background(MaterialTheme.colorScheme.background)
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
          },
      ) {

        ControlPanel(
          forecast = forecast,
          index = state.index,
          onDayClicked = { onDayClicked(it) },
          onCloseClicked = { onCloseClicked() }
        )

        DividingLine()

        ThisDayWeather(state = state)

        val dayForecast = forecast.upcomingDays[state.index]

        HumidityWindPressure(
          modifier = Modifier
            .padding(
              top = 24.dp,
              start = 16.dp,
              end = 16.dp
            ),
          humidity = dayForecast.humidity,
          windSpeed = dayForecast.windSpeed,
          windDir = dayForecast.windDir,
          pressure = dayForecast.pressure
        )

        val list = getHoursForDay(
          hours = forecast.upcomingHours,
          dayEpoch = forecast.upcomingDays[state.index].date,
          tz = forecast.tzId
        )

        AnimatingHourlyWeatherForecast(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          list = list,
          tzId = forecast.tzId
        )

        UvIndexAndCloudiness(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          uvIndex = dayForecast.uvIndex,
          cloudCover = dayForecast.cloudCover,
          precipitation = if (dayForecast.precipProb > 0) dayForecast.precip else null
        )

        SunAndMoon(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          sunrise = dayForecast.sunrise,
          sunset = dayForecast.sunset,
          moonrise = dayForecast.moonrise,
          moonset = dayForecast.moonset,
          moonPhase = dayForecast.moonPhase,
          tzId = forecast.tzId
        )

        Charts(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          list = list,
          tzId = forecast.tzId
        )
      }
    }
  }
}