package info.sergeikolinichenko.myapplication.presentation.ui.content.details

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.WEATHER_ICON_SIZE
import info.sergeikolinichenko.myapplication.utils.convertLongToCalendarWithTz
import info.sergeikolinichenko.myapplication.utils.formattedFullHour
import info.sergeikolinichenko.myapplication.utils.toIconId
import info.sergeikolinichenko.myapplication.utils.toPerCentFromFloat
import java.util.Calendar

/** Created by Sergei Kolinichenko on 23.07.2024 at 18:30 (GMT+3) **/

private const val MAXIMUM_HOURS = 23

@Composable
internal fun AnimatingHourlyWeatherForecast(
  modifier: Modifier = Modifier,
  forecast: Forecast
) {
  val state = remember {
    MutableTransitionState(false).apply { targetState = true }
  }
  AnimatedVisibility(
    visibleState = state,
    enter = fadeIn(animationSpec = tween(500))
        + slideIn(animationSpec = tween(500),
      initialOffset = { IntOffset(it.width, 0) }),
  ) {
    HourlyWeatherForecast(
      modifier = modifier,
      forecast = forecast
    )
  }
}

@Composable
internal fun HourlyWeatherForecast(
  modifier: Modifier = Modifier,
  forecast: Forecast
) {

  val firstIndex = forecast.upcomingHours.indexOfFirst {
    convertLongToCalendarWithTz(it.date, forecast.tzId) >= Calendar.getInstance()
  }

  Card(
    modifier = modifier
      .height(136.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {

    LazyRow(
      modifier = Modifier
        .fillMaxHeight()
        .padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {

      if (firstIndex <= 0) {
        item {
          Text(
            modifier = Modifier
              .fillMaxHeight()
              .width(300.dp)
              .padding(8.dp)
              .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.details_content_error_date_set_on_phone),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      } else {

        val list = forecast.upcomingHours.subList(firstIndex - 1, firstIndex + MAXIMUM_HOURS)
        val hours = list.mapIndexed { index, hour -> if (index == 0) hour.copy(date = 0L) else hour }

        items(
          items = hours,
          key = { it.date }
        ) {
          HourlyWeatherItem(
            time = if (it.date == 0L) stringResource(R.string.details_content_title_now_date)
            else convertLongToCalendarWithTz(it.date, forecast.tzId).formattedFullHour(),
            icon = it.icon,
            chanceOfPrecip = it.precipProb,
            temp = it.temp
          )
        }
      }
    }
  }
}

@Composable
private fun HourlyWeatherItem(
  modifier: Modifier = Modifier,
  time: String,
  icon: String,
  chanceOfPrecip: Float,
  temp: String
) {
  Column(
    modifier = modifier
      .fillMaxHeight()
      .sizeIn(minWidth = 58.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = time,
      style = MaterialTheme.typography.bodySmall,
      textAlign = TextAlign.Unspecified,
      color = MaterialTheme.colorScheme.onBackground
    )
    Icon(
      modifier = Modifier.size(WEATHER_ICON_SIZE.dp),
      painter = painterResource(id = icon.toIconId()), //
      tint = Color.Unspecified,
      contentDescription = null
    )
    if (chanceOfPrecip > 0f) {
      Text(
        text = chanceOfPrecip.toPerCentFromFloat(),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
    }
    Text(
      text = temp,
      style = MaterialTheme.typography.titleSmall,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}