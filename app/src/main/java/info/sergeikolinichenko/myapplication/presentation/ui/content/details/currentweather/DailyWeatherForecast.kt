package info.sergeikolinichenko.myapplication.presentation.ui.content.details.currentweather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.utils.DividingLine
import info.sergeikolinichenko.myapplication.utils.TITLE_ICON_SIZE_16
import info.sergeikolinichenko.myapplication.utils.WEATHER_ICON_SIZE_36
import info.sergeikolinichenko.myapplication.utils.convertLongToCalendarWithTz
import info.sergeikolinichenko.myapplication.utils.getDayAndMonthName
import info.sergeikolinichenko.myapplication.utils.getDayOfWeekName
import info.sergeikolinichenko.myapplication.utils.toIconId
import info.sergeikolinichenko.myapplication.utils.toPerCentFromFloat
import info.sergeikolinichenko.myapplication.utils.toPrecipitationTypeString
import java.util.Calendar

/** Created by Sergei Kolinichenko on 24.07.2024 at 17:09 (GMT+3) **/

@Composable
internal fun DailyWeatherForecast(
  modifier: Modifier = Modifier,
  forecast: ForecastFs,
  onDayClicked: (Int) -> Unit
) {

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

      forecast.upcomingDays.fastForEachIndexed { index, _ ->

        DailyWeatherItem(
          modifier = Modifier,
          forecast = forecast,
          numberOfDay = index,
          onDayClicked = { onDayClicked(it) }
        )
        DividingLine()
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
      contentDescription = stringResource(R.string.details_content_description_icon_calendar)
    )
    Text(
      text = stringResource(R.string.details_content_title_text_next_days_forecast, forecast.upcomingDays.size -1),
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      fontSize = 12.sp,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
private fun DailyWeatherItem(
  modifier: Modifier = Modifier,
  forecast: ForecastFs,
  numberOfDay: Int,
  onDayClicked: (Int) -> Unit
) {

  val day = forecast.upcomingDays[numberOfDay]

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
      .clickable { if (!seeIfToday(day.date, forecast.tzId)) onDayClicked(numberOfDay) },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {

    Column(
      modifier = Modifier,
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = weekday,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = date,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    Icon(
      modifier = Modifier.size(WEATHER_ICON_SIZE_36.dp),
      painter = painterResource(id = icon.toIconId()),
      tint = Color.Unspecified,
      contentDescription = stringResource(R.string.details_content_text_description_weather_condition)
    )

    if (chanceOfPrecip > 0f && precipType != null) {
      Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        Text(
          text = precipType,
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.W500,
          fontSize = 10.sp,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = chanceOfPrecip.toPerCentFromFloat(),
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Medium,
          fontSize = 12.sp,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
    }

    Row(
      modifier = Modifier,
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.End
    ) {

      Text(
        modifier = Modifier.padding(end = 4.dp),
        text = maxTemp,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
      )

      Text(
        modifier = modifier,
        text = minTemp,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
      )
    }
  }
}

private fun seeIfToday(date: Long, tz: String): Boolean {
  val calendar = convertLongToCalendarWithTz(date, tz)
  val today = Calendar.getInstance()
  return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}

