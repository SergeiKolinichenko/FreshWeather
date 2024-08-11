package info.sergeikolinichenko.myapplication.presentation.ui.content.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.LinearGradient.gradientDailyTemp
import info.sergeikolinichenko.myapplication.utils.TITLE_ICON_SIZE
import info.sergeikolinichenko.myapplication.utils.WEATHER_ICON_SIZE
import info.sergeikolinichenko.myapplication.utils.convertLongToCalendarWithTz
import info.sergeikolinichenko.myapplication.utils.formattedDateOfWeek
import info.sergeikolinichenko.myapplication.utils.formattedDayOfWeek
import info.sergeikolinichenko.myapplication.utils.toIconId
import info.sergeikolinichenko.myapplication.utils.toPerCentFromFloat
import info.sergeikolinichenko.myapplication.utils.toPrecipitationTypeString
import java.util.Calendar

/** Created by Sergei Kolinichenko on 24.07.2024 at 17:09 (GMT+3) **/

@Composable
internal fun DailyWeatherForecast(
  modifier: Modifier = Modifier,
  forecast: Forecast
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

      TittleDailyWeatherForecast()

      DividingLine()

      for (day in forecast.upcomingDays.indices) {

        DailyWeatherItem(
          modifier = Modifier,
          forecast = forecast,
          numberOfDay = day
        )

        DividingLine()
      }
    }
  }
}

@Composable
private fun DividingLine(
  modifier: Modifier = Modifier
) {
  Spacer(
    modifier = modifier
      .fillMaxWidth()
      .height(1.dp)
      .background(MaterialTheme.colorScheme.surfaceBright)
  )
}

@Composable
private fun TittleDailyWeatherForecast(
  modifier: Modifier = Modifier
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
        .size(TITLE_ICON_SIZE.dp),
      imageVector = ImageVector.vectorResource(id = R.drawable.calendar),
      contentDescription = stringResource(R.string.details_content_description_icon_calendar)
    )
    Text(
      text = stringResource(R.string.details_content_title_text_7_day_forecast),
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
  forecast: Forecast,
  numberOfDay: Int
) {

  val day = forecast.upcomingDays[numberOfDay]

  val weekday = when (numberOfDay) {
    0 -> stringResource(R.string.details_content_daily_forecast_text_today)
    1 -> stringResource(R.string.details_content_daily_forecast_text_tomorrow)
    else -> convertLongToCalendarWithTz(
      day.date,
      forecast.tzId
    ).formattedDayOfWeek() }

  val icon = day.icon
  val date = convertLongToCalendarWithTz(day.date, forecast.tzId).formattedDateOfWeek()
  val minTemp = day.tempMin
  val maxTemp = day.tempMax
  val chanceOfPrecip = day.precipProb
  val precipType = toPrecipitationTypeString(
    context = LocalContext.current,
    list = day.precipType
  )
  val iconArrow = if (forecast.temperatureDirectionDetection()) Icons.Default.ArrowUpward
   else Icons.Default.ArrowDownward

  Row(
    modifier = modifier
      .fillMaxWidth(),
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
      modifier = Modifier.size(WEATHER_ICON_SIZE.dp),
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
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .wrapContentWidth()
          .padding(end = 4.dp)
      ) {
        Icon(
          modifier = Modifier
            .size(20.dp)
            .align(Alignment.Center),
          imageVector = iconArrow,
          contentDescription = null)
      }
      Text(
        modifier = modifier,
        text = minTemp,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
      )
    }
  }
}

private fun Forecast.temperatureDirectionDetection(): Boolean {
  val firstIndex = this.upcomingHours.indexOfFirst {
    convertLongToCalendarWithTz(it.date, this.tzId) >= Calendar.getInstance()
  }
  return this.upcomingHours[firstIndex].temp > this.upcomingHours[firstIndex - 1].temp
}

// may be it is not needed
@Composable
fun CustomGradientLine(
  modifier: Modifier = Modifier,
  minTemp: String,
  maxTemp: String,
  currentTemp: String? = null
) {

  val colorOutline = MaterialTheme.colorScheme.outline

  val minTempFloat = minTemp.substringBefore("°").toFloat()
  val maxTempFloat = maxTemp.substringBefore("°").toFloat()

  val startX = (maxTempFloat + 50) / 100
  val endX = (maxTempFloat + 50) / 100

  Canvas(modifier = modifier) {

    drawPartialGradientLine(startX, endX)

    currentTemp?.let {

      val currentTempFloat = currentTemp.substringBefore("°").toFloat()
      val indexLength =
        (size.width / (maxTempFloat - minTempFloat)) * (currentTempFloat - minTempFloat)

      drawCircle(
        color = Color.White,
        radius = 6.dp.toPx(),
        center = Offset(indexLength, 0f)
      )
      drawCircle(
        color = colorOutline,
        style = Stroke(width = 1.dp.toPx()),
        radius = 6.dp.toPx(),
        center = Offset(indexLength, 0f)
      )
    }
  }
}

fun DrawScope.drawPartialGradientLine(startX: Float, endX: Float) {

  val partialGradient = Brush.horizontalGradient(

    colorStops = gradientDailyTemp.filter { (offset, _) ->

      startX < 0.25f && offset == 0f ||
          startX >= 0.25f && startX < 0.5f && offset == 0.25f ||
          startX >= 0.5f && startX < 0.75f && offset == 0.5f ||
          startX >= 0.75f && offset == 0.75f ||
          endX < 0.25f && offset == 0.25f ||
          endX > 0.25f && endX < 0.5f && offset == 0.5f ||
          endX >= 0.5f && endX < 0.75f && offset == 0.75f ||
          endX >= 0.75f && offset == 1f

    }.map { (offset, color) ->
      ((offset - startX) / (endX - startX)) to color
    }.toTypedArray()
  )

  drawLine(
    brush = partialGradient,
    start = Offset(0f, 0f),
    end = Offset(size.width, 0f),
    strokeWidth = 4.dp.toPx()
  )
}

