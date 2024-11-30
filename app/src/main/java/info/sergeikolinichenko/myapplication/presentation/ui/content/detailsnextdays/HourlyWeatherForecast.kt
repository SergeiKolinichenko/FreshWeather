package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.HourForecastFs
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.WEATHER_ICON_SIZE_36
import info.sergeikolinichenko.myapplication.utils.getTime
import info.sergeikolinichenko.myapplication.utils.precipitationToColour
import info.sergeikolinichenko.myapplication.utils.toIconId
import info.sergeikolinichenko.myapplication.utils.toPerCentFromFloat
import info.sergeikolinichenko.myapplication.utils.toPrecipitationTypeString
import java.time.Instant
import java.time.temporal.ChronoUnit

/** Created by Sergei Kolinichenko on 23.07.2024 at 18:30 (GMT+3) **/

@Composable
internal fun AnimatingHourlyWeatherForecast(
  modifier: Modifier = Modifier,
  list: List<HourForecastFs>?,
  tzId: String
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

    list?.let {
      HourlyWeatherForecast(
        modifier = modifier,
        list = list,
        tzId = tzId
      )
    } ?: run {
      Text(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
          .background(MaterialTheme.colorScheme.surface),
        text = stringResource(R.string.details_content_error_date_set_on_phone),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}

@Composable
internal fun HourlyWeatherForecast(
  modifier: Modifier = Modifier,
  list: List<HourForecastFs>,
  tzId: String
) {

  Card(
    modifier = modifier.height(160.dp),
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

        items(
          items = list,
          key = { it.date }
        ) {
          HourlyWeatherItem(
            modifier = Modifier.fillMaxHeight(),
            time = if (it.date.isCurrentHour()) stringResource(R.string.details_content_title_now_date)
            else getTime(it.date, tzId),
            icon = it.icon,
            chanceOfPrecip = it.precipProb,
            precipType = toPrecipitationTypeString(
              context = LocalContext.current,
              list = it.precipType
            ),
            temp = it.temp
          )
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
  precipType: String?,
  temp: String
) {
  Column(
    modifier = modifier
      .width(IntrinsicSize.Max),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    ResponsiveText(
      text = time,
      textStyle = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground,
      maxLines = 1
    )

    Icon(
      modifier = Modifier
        .size(WEATHER_ICON_SIZE_36.dp),
      painter = painterResource(id = icon.toIconId()),
      tint = Color.Unspecified,
      contentDescription = null
    )

    if (chanceOfPrecip > 0f && precipType != null) {
      ResponsiveText(
        text = precipType,
        targetTextSizeHeight = 10.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        lineHeight = 10.sp,
        color = chanceOfPrecip.precipitationToColour()
      )
      ResponsiveText(
        text = chanceOfPrecip.toPerCentFromFloat(),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        targetTextSizeHeight = 12.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        color = chanceOfPrecip.precipitationToColour(),
        maxLines = 1
      )
    }
    ResponsiveText(
      text = temp,
      targetTextSizeHeight = 16.sp,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center,
      lineHeight = 16.sp,
      color = MaterialTheme.colorScheme.onBackground,
      maxLines = 1
    )
  }
}

fun Long.isCurrentHour(): Boolean {
  val now = Instant.now()
  val hourStart = now.truncatedTo(ChronoUnit.HOURS).minus(1, ChronoUnit.HOURS)
  val hourEnd = hourStart.plus(2, ChronoUnit.HOURS)
  val targetTime = Instant.ofEpochSecond(this)
  return  targetTime.isAfter(hourStart) && targetTime.isBefore(hourEnd)}