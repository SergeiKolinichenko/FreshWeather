package info.sergeikolinichenko.myapplication.presentation.ui.content.details.current

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.entity.HourForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.AnimatingHourlyWeatherForecast
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.Charts
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.HumidityWindPressure
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.SunAndMoon
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.UvIndexAndCloudiness
import info.sergeikolinichenko.myapplication.utils.convertLongToCalendarWithTz
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:57 (GMT+3) **/

@Composable
fun DetailsContent(
  component: DetailsComponent
) {

  Box( // it's for background
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    AnimatedDetailsContent(
      modifier = Modifier,
      component = component
    )
  }
}

@Composable
fun AnimatedDetailsContent(
  modifier: Modifier = Modifier,
  component: DetailsComponent
) {

  val state = component.model.collectAsState()

  val animState = remember { MutableTransitionState(false) }

  animState.targetState = state.value.forecastState is DetailsStore.State.ForecastState.Loaded

  AnimatedVisibility(
    visibleState = animState,
    enter = fadeIn(animationSpec = tween(300)) +
        slideIn(animationSpec = tween(300),
          initialOffset = { IntOffset(it.width, 0) }),

    exit = fadeOut(
      animationSpec = tween(300)
    ) + slideOut(
      animationSpec = tween(300),
      targetOffset = { IntOffset(-it.width, 0) }),
  ) {
    DetailsScreen(
      modifier = modifier,
      state = state,
      onBackClicked = { component.onBackClicked() },
      onChangeFavouriteStatusClicked = { component.onSettingsClicked() },
      onDayClicked = { id, index, forecast ->
        component.onDayClicked(
          id = id,
          index = index,
          forecast = forecast
        )
      }
    )
  }
}

@Composable
private fun DetailsScreen(
  modifier: Modifier = Modifier,
  state: State<DetailsStore.State>,
  onBackClicked: () -> Unit,
  onChangeFavouriteStatusClicked: () -> Unit,
  onDayClicked: (Int, Int, ForecastFs) -> Unit
) {

  Column(
    modifier = modifier
      .padding(16.dp)
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {

    TopBar(
      modifier = Modifier,
      state = state.value,
      onBackButtonClick = {
        onBackClicked()
      },
      onSettingsClicked = {
        onChangeFavouriteStatusClicked()
      }
    )

    MainScreen(
      modifier = Modifier,
      state = state.value,
      onDayClicked = { id, index, forecast ->
        onDayClicked(id, index, forecast)
      }
    )
  }
}

@Composable
private fun MainScreen(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
  onDayClicked: (Int, Int, ForecastFs) -> Unit
) {
  if (state.citiesState is DetailsStore.State.CitiesState.Loaded) {

    val city = state.citiesState.cities.first { it.id == state.citiesState.id }

    when (val forecastState = state.forecastState) {
      DetailsStore.State.ForecastState.Error -> Error()
      DetailsStore.State.ForecastState.Initial -> Initial()
      DetailsStore.State.ForecastState.Loading -> Loading()

      is DetailsStore.State.ForecastState.Loaded -> {

        val forecast = forecastState.forecast

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
          onDayClicked = { index ->
            onDayClicked(city.id, index, forecast)
          }
        )

        SunAndMoon(
          modifier = Modifier
            .padding(top = 16.dp),
          sunrise = forecast.upcomingDays.first().sunrise,
          sunset = forecast.upcomingDays.first().sunset,
          moonrise = forecast.upcomingDays.first().moonrise,
          moonset = forecast.upcomingDays.first().moonset,
          moonPhase = forecast.upcomingDays.first().moonPhase,
          tzId = forecast.tzId
        )

        Charts(
          modifier = Modifier
            .padding(top = 16.dp,),
          list = forecast.getSublistForecastHourly(),
          tzId = forecast.tzId
        )
      }
    }
  }
}


@Composable
private fun Initial() {
  Loading()
}

@Composable
private fun Loading() {
  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    CircularProgressIndicator(
      modifier = Modifier.align(Alignment.Center),
      color = MaterialTheme.colorScheme.background
    )
  }
}

@Composable
private fun Error() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      modifier = Modifier.padding(16.dp),
      text = stringResource(R.string.favourite_content_error_weather_for_city),
      style = MaterialTheme.typography.bodyLarge
    )
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
private const val MAXIMUM_HOURS_CHART = 25L
private const val WRONG_INDEX_OF_FORECAST = -1