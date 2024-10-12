package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.currentweather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.utils.DividingLine
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.TITLE_ICON_SIZE_16
import info.sergeikolinichenko.myapplication.utils.WEATHER_ICON_SIZE_36
import info.sergeikolinichenko.myapplication.utils.convertLongToCalendarWithTz
import info.sergeikolinichenko.myapplication.utils.getDayAndMonthName
import info.sergeikolinichenko.myapplication.utils.getDayOfWeekName
import info.sergeikolinichenko.myapplication.utils.precipitationToColour
import info.sergeikolinichenko.myapplication.utils.toIconId
import info.sergeikolinichenko.myapplication.utils.toPerCentFromFloat
import info.sergeikolinichenko.myapplication.utils.toPrecipitationTypeString
import java.util.Calendar

/** Created by Sergei Kolinichenko on 24.07.2024 at 17:09 (GMT+3) **/

private const val NUMBER_OF_SHOWN_DAYS = 7

@Composable
internal fun DailyWeatherForecast(
  modifier: Modifier = Modifier,
  forecast: ForecastFs,
  onDayClicked: (Int) -> Unit
) {

  val maxItemHeight = remember { mutableStateOf(0.dp) }

  Card(
    modifier = modifier
      .fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {

    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.Start
    ) {

      TittleDailyWeatherForecast(forecast = forecast)

      DividingLine()

      LazyColumn(
        modifier = Modifier
          .height(maxItemHeight.value * NUMBER_OF_SHOWN_DAYS),
        horizontalAlignment = Alignment.Start
      ) {
        itemsIndexed(
          items = forecast.upcomingDays,
          key = { _, item -> item.date }
        ) { index, _ ->
          DailyWeatherItem(
            modifier = Modifier,
            forecast = forecast,
            numberOfDay = index,
            onDayClicked = { onDayClicked(it) },
            maxItemHeight = maxItemHeight
          )
          DividingLine()
        }
      }
    }
  }
}

@Composable
private fun TittleDailyWeatherForecast(
  modifier: Modifier = Modifier,
  forecast: ForecastFs,
) {
  Row(
    modifier = modifier
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start
  ) {
    Icon(
      modifier = Modifier
        .padding(end = 4.dp)
        .size(TITLE_ICON_SIZE_16.dp),
      imageVector = ImageVector.vectorResource(id = R.drawable.calendar),
      contentDescription = stringResource(R.string.details_content_description_icon_calendar),
      tint = Color.Unspecified
    )
    ResponsiveText(
      text = stringResource(
        R.string.details_content_title_text_next_days_forecast,
        forecast.upcomingDays.size - 1
      ),
      targetTextSizeHeight = 12.sp,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      maxLines = 1
    )
  }
}

@Composable
private fun DailyWeatherItem(
  modifier: Modifier = Modifier,
  forecast: ForecastFs,
  numberOfDay: Int,
  onDayClicked: (Int) -> Unit,
  maxItemHeight: MutableState<Dp>
) {

  val day = forecast.upcomingDays[numberOfDay]
  val density = LocalDensity.current

  val weekday = when (numberOfDay) {
    0 -> stringResource(R.string.details_content_daily_forecast_text_today)
    1 -> stringResource(R.string.details_content_daily_forecast_text_tomorrow)
    else -> getDayOfWeekName(day.date, forecast.tzId)
  }

  val icon = day.icon
  val date = getDayAndMonthName(day.date, forecast.tzId)
  val minTemp = day.tempMin
  val maxTemp = day.tempMax
  val chanceOfPrecip = day.precipProb
  val precipType = toPrecipitationTypeString(
    context = LocalContext.current,
    list = day.precipType
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable { if (!seeIfToday(day.date, forecast.tzId)) onDayClicked(numberOfDay) }
      .onGloballyPositioned { coords ->
        val itemHeight = with(density) { coords.size.height.toDp() }
        if (itemHeight > maxItemHeight.value) {
          maxItemHeight.value = itemHeight
        }
      },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {

    Column(
      modifier = Modifier,
      horizontalAlignment = Alignment.Start
    ) {
      ResponsiveText(
        text = weekday,
        textStyle = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1
      )
      ResponsiveText(
        text = date,
        textStyle = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.secondary,
        maxLines = 1
      )
    }
    Column(
      modifier = Modifier,
      verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        modifier = Modifier.size(WEATHER_ICON_SIZE_36.dp),
        painter = painterResource(id = icon.toIconId()),
        tint = Color.Unspecified,
        contentDescription = stringResource(R.string.details_content_text_description_weather_condition)
      )
      if (chanceOfPrecip > 0f && precipType != null) {
        ResponsiveText(
          text = precipType,
          targetTextSizeHeight = 10.sp,
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center,
          lineHeight = 10.sp,
          color = chanceOfPrecip.precipitationToColour(),
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
    }
    Row(
      modifier = Modifier,
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.End
    ) {

      ResponsiveText(
        text = maxTemp,
        textStyle = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1
      )
      ResponsiveText(
        text = "/",
        textStyle = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        maxLines = 1
      )
      ResponsiveText(
        text = minTemp,
        textStyle = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        maxLines = 1
      )
    }
  }
}

private fun seeIfToday(date: Long, tz: String): Boolean {
  val calendar = convertLongToCalendarWithTz(date, tz)
  val today = Calendar.getInstance()
  return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}

