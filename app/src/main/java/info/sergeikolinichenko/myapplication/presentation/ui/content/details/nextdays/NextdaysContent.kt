package info.sergeikolinichenko.myapplication.presentation.ui.content.details.nextdays

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.HourForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.component.NextdaysComponent
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.store.NextdaysStore
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.AnimatingHourlyWeatherForecast
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.Charts
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.HumidityWindPressure
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.SunAndMoon
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.UvIndexAndCloudiness
import info.sergeikolinichenko.myapplication.utils.DividingLine
import info.sergeikolinichenko.myapplication.utils.getNumberDayOfMonth
import info.sergeikolinichenko.myapplication.utils.getTwoLettersDayOfTheWeek
import info.sergeikolinichenko.myapplication.utils.toIconId
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** Created by Sergei Kolinichenko on 11.08.2024 at 17:10 (GMT+3) **/

@Composable
fun NextdaysContent(
  component: NextdaysComponent
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surfaceBright),
  ) {

    val state by component.model.collectAsState()

    TopBar(
      modifier = Modifier,
      state = component.model.collectAsState().value
    )

    when (state.citiesState) {
      NextdaysStore.State.CitiesState.Error -> NextdaysErrorScreen(
        text = stringResource(R.string.error_loading_favourite_cities_please_inform_the_developers_about_the_problem)
      )

      NextdaysStore.State.CitiesState.Initial -> {}
      is NextdaysStore.State.CitiesState.Loaded -> {
        when (state.forecast) {
          NextdaysStore.State.ForecastState.Error -> NextdaysErrorScreen(
            text = stringResource(R.string.error_receiving_weather_forecast_please_try_again_later_if_the_error_occurs_again_please_notify_the_developers)
          )

          NextdaysStore.State.ForecastState.Initial -> {}
          is NextdaysStore.State.ForecastState.Loaded -> {

            AppearanceAnimationNextdays(
              modifier = Modifier.weight(1f),
              state = state,
              onDayClicked = { component.onDayClicked(it) },
              onCloseClicked = { component.onCloseClicked() },
              onSwipeLeft = { component.onSwipeLeft() },
              onSwipeRight = { component.onSwipeRight() }
            )
          }

          NextdaysStore.State.ForecastState.Loading -> NextdaysLoadingScreen()
        }
      }
    }
  }
}

@Composable
private fun NextdaysErrorScreen(
  modifier: Modifier = Modifier,
  text: String
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Text(
      modifier = Modifier.align(Alignment.Center),
      text = text,
      style = MaterialTheme.typography.titleLarge,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
private fun NextdaysLoadingScreen(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    CircularProgressIndicator(
      modifier = Modifier.align(Alignment.Center)
    )
  }
}

@Composable
internal fun NextdaysScreen(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
  onDayClicked: (Int) -> Unit,
  onCloseClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit,
  animatedDirection: (AnimatedDirection) -> Unit
) {

  val forecast = (state.forecast as NextdaysStore.State.ForecastState.Loaded).forecast

  var overScrollTop by remember { mutableStateOf(false) }
  var overScrollBottom by remember { mutableStateOf(false) }
  var swipeLeft by remember { mutableStateOf(false) }
  var swipeRight by remember { mutableStateOf(false) }
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

  if (overScrollTop) {
    if (state.index > 1) {
      animatedDirection(AnimatedDirection.Bottom)
      onDayClicked(state.index - 1)
    }
  }
  if (overScrollBottom) {
    if (state.index < forecast.upcomingDays.size - 1) {
      animatedDirection(AnimatedDirection.Top)
      onDayClicked(state.index + 1)
    }
  }

  if (swipeLeft) {
    animatedDirection(AnimatedDirection.Right)
    onSwipeLeft()
  }

  if (swipeRight) {
    animatedDirection(AnimatedDirection.Left)
    onSwipeRight()
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      .background(MaterialTheme.colorScheme.background)
      .nestedScroll(nestedScrollConnection)
      .verticalScroll(scrollState)
      .pointerInput(Unit) {
        detectHorizontalDragGestures { change, dragAmount ->
          if (dragAmount > 0) {
            swipeLeft = true
            swipeRight = false
          } else if (dragAmount < 0) {
            swipeRight = true
            swipeLeft = false
          }
          change.consume()
        }
      },
  ) {

    ControlPanel(
      state = state,
      onDayClicked = { onDayClicked(it) },
      onCloseClicked = { onCloseClicked() }
    )

    DividingLine()

    DailyWeather(state = state)

    val dayForecast =
      forecast.upcomingDays[state.index] //forecast.upcomingDays.first { it.date == state.index }

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

@Composable
private fun ControlPanel(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
  onDayClicked: (Int) -> Unit,
  onCloseClicked: () -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 12.dp)
  ) {
    Text(
      modifier = Modifier.align(Alignment.Center),
      text = stringResource(R.string.nextdays_title_text_weather_conditions),
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      fontSize = 22.sp,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )
    Icon(
      modifier = Modifier
        .size(24.dp)
        .align(Alignment.CenterEnd)
        .clickable { onCloseClicked() },
      imageVector = Icons.Default.Close,
      contentDescription = "Close nextdays screen"
    )
  }
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {

    val forecast = (state.forecast as NextdaysStore.State.ForecastState.Loaded).forecast

    val days = forecast.upcomingDays

    days.drop(1).forEach { day ->

      val color =
        if (forecast.upcomingDays.indexOf(day) == state.index) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.background

      DayOfWeek(
        modifier = Modifier
          .weight(1f),
        titleTopText = getTwoLettersDayOfTheWeek(day.date, forecast.tzId),
        titleBottomText = getNumberDayOfMonth(day.date, forecast.tzId),
        backgroundColor = color,
        index = forecast.upcomingDays.indexOf(day),
        onDayClicked = { onDayClicked(it) }
      )
    }
  }
}

@Composable
private fun DailyWeather(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
) {

  val forecast = (state.forecast as NextdaysStore.State.ForecastState.Loaded).forecast

  val thisDayWeather =
    forecast.upcomingDays[state.index] //forecast.upcomingDays.first { it.date == state.index }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 16.dp),
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Row(
      modifier = modifier
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {

        if (state.citiesState is NextdaysStore.State.CitiesState.Loaded) {

          val city = state.citiesState.cities.first { it.id == state.citiesState.id }

          Text(
            modifier = Modifier,
            text = city.name,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground
          )
        }

        Row(
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.CenterVertically
        ) {
          DailyWeatherTempText(
            modifier = Modifier.padding(end = 4.dp),
            text = stringResource(R.string.caption_max)
          )
          DailyWeatherTempText(
            modifier = Modifier.padding(end = 8.dp),
            text = thisDayWeather.tempMax
          )
          DailyWeatherTempText(
            modifier = Modifier.padding(end = 4.dp),
            text = stringResource(R.string.caption_min)
          )
          DailyWeatherTempText(text = thisDayWeather.tempMin)
        }
      }
      Icon(
        modifier = Modifier
          .size(80.dp),
        painter = painterResource(id = thisDayWeather.icon.toIconId()),
        tint = Color.Unspecified,
        contentDescription = null
      )
    }
    Text(
      modifier = Modifier,
      text = thisDayWeather.description,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      fontSize = 20.sp,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
private fun DailyWeatherTempText(
  modifier: Modifier = Modifier,
  text: String
) {
  Text(
    modifier = modifier,
    text = text,
    style = MaterialTheme.typography.titleSmall,
    textAlign = TextAlign.Start,
    color = MaterialTheme.colorScheme.onBackground
  )
}

@Composable
private fun DayOfWeek(
  modifier: Modifier = Modifier,
  titleTopText: String,
  titleBottomText: String,
  backgroundColor: Color,
  index: Int,
  onDayClicked: (Int) -> Unit
) {
  Column(
    modifier = modifier.clickable { onDayClicked(index) },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    Text(
      modifier = Modifier,
      text = titleTopText,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      fontSize = 12.sp,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(shape = RoundedCornerShape(16.dp))
        .background(backgroundColor),
    ) {
      Text(
        modifier = Modifier.align(Alignment.Center),
        text = titleBottomText,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}

@Composable
private fun TopBar(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 20.dp),
  ) {
    Row(
      modifier = modifier
        .align(Alignment.Center),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (state.citiesState is NextdaysStore.State.CitiesState.Loaded) {
        for (number in state.citiesState.cities) {
          if (number.id == state.citiesState.id) {

            Icon(
              modifier = Modifier
                .size(8.dp),
              imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
              tint = Color.Black,
              contentDescription = null
            )
          } else {
            Icon(
              modifier = Modifier
                .size(8.dp),
              imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
              tint = Color.White,
              contentDescription = null
            )
          }
        }
      }
    }
  }
}

fun getHoursForDay(hours: List<HourForecastFs>, dayEpoch: Long, tz: String): List<HourForecastFs> {
  val zoneId = ZoneId.of(tz)
  val dayStart = Instant.ofEpochSecond(dayEpoch).atZone(zoneId).toLocalDate().atStartOfDay(zoneId)

  val dayEnd = dayStart.plus(1, ChronoUnit.DAYS)

  return hours.filter { hour ->
    val hourInstant = Instant.ofEpochSecond(hour.date).atZone(zoneId)
    hourInstant.isAfter(dayStart) && hourInstant.isBefore(dayEnd)
  }
}
