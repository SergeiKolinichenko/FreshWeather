package info.sergeikolinichenko.myapplication.presentation.ui.content.details.nextdaysforecast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.getNumberDayOfMonth
import info.sergeikolinichenko.myapplication.utils.getTwoLettersDayOfTheWeek

/** Created by Sergei Kolinichenko on 18.08.2024 at 10:54 (GMT+3) **/

@Composable
internal fun ControlPanel(
  modifier: Modifier = Modifier,
  index: Int,
  forecast: ForecastFs,
  onDayClicked: (Int) -> Unit,
  onCloseClicked: () -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 12.dp)
  ) {
    ResponsiveText(
      modifier = Modifier
        .align(Alignment.Center),
      text = stringResource(R.string.nextdays_title_text_weather_conditions),
      targetTextSizeHeight = 22.sp,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground,
      maxLines = 1
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

  if (forecast.upcomingDays.drop(1).size <= DAYS_ROR_ONE_LINE) {

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {

      forecast.upcomingDays.drop(1).forEachIndexed { ind, day ->

        val color =
          if (ind == index - 1) MaterialTheme.colorScheme.tertiary
          else MaterialTheme.colorScheme.background

        DayForOneLine(
          modifier = Modifier
            .weight(1f),
          titleTopText = getTwoLettersDayOfTheWeek(day.date, forecast.tzId),
          titleBottomText = getNumberDayOfMonth(day.date, forecast.tzId),
          backgroundColor = color,
          index = ind,
          onDayClicked = { onDayClicked(it + 1) }
        )
      }
    }
  } else {

    val sizeTopLine =
      forecast.upcomingDays.drop(1).size / 2 + forecast.upcomingDays.drop(1).size % 2
    val topLine = forecast.upcomingDays.drop(1).take(sizeTopLine)
    val bottomLine = forecast.upcomingDays.drop(1).drop(sizeTopLine)


    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 18.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {

      topLine.forEachIndexed { ind, day ->

        val color =
          if (ind == index - 1) MaterialTheme.colorScheme.tertiary
          else MaterialTheme.colorScheme.background

        DayForTwoLines(
          modifier = Modifier
            .weight(1f),
          titleBottomText = getNumberDayOfMonth(day.date, forecast.tzId),
          backgroundColor = color,
          index = ind,
          onDayClicked = { onDayClicked(it + 1) }
        )
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, bottom = 18.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {

      bottomLine.forEachIndexed { ind, day ->

        val color =
          if (ind + sizeTopLine == index - 1) MaterialTheme.colorScheme.tertiary
          else MaterialTheme.colorScheme.background

        DayForTwoLines(
          modifier = Modifier
            .weight(1f),
          titleBottomText = getNumberDayOfMonth(day.date, forecast.tzId),
          backgroundColor = color,
          index = ind,
          onDayClicked = { onDayClicked(it + sizeTopLine + 1) }
        )
      }
    }
  }
}

@Composable
private fun DayForOneLine(
  modifier: Modifier = Modifier,
  titleTopText: String,
  titleBottomText: String,
  backgroundColor: Color,
  index: Int,
  onDayClicked: (Int) -> Unit
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    Text(
      modifier = Modifier,
      text = titleTopText,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      fontSize = 16.sp,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )
    Box(
      modifier = Modifier
        .size(50.dp)
        .clip(shape = CircleShape)
        .background(backgroundColor)
        .clickable { onDayClicked(index) },
    ) {
      Text(
        modifier = Modifier.align(Alignment.Center),
        text = titleBottomText,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}

@Composable
private fun DayForTwoLines(
  modifier: Modifier = Modifier,
  titleBottomText: String,
  backgroundColor: Color,
  index: Int,
  onDayClicked: (Int) -> Unit
) {
  Box(
    modifier = modifier
      .size(50.dp)
      .clip(shape = CircleShape)
      .background(backgroundColor)
      .clickable { onDayClicked(index) },
  ) {
      Text(
        modifier = Modifier.align(Alignment.Center),
        text = titleBottomText,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
  }


}

private const val DAYS_ROR_ONE_LINE = 7