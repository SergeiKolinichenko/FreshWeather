package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.nextdaysforecast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
      tint = MaterialTheme.colorScheme.surfaceTint,
      contentDescription = "Close nextdays screen"
    )
  }

  val listState = rememberLazyListState()

  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    state = listState,
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {

    itemsIndexed(
      items = forecast.upcomingDays.drop(1),
      key = { _, day -> day.date }
    ) { ind, day ->

      val color =
        if (ind == index - 1) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.background

      DayOfDailyForecast(
        modifier = Modifier,
        titleTopText = getTwoLettersDayOfTheWeek(day.date, forecast.tzId),
        titleBottomText = getNumberDayOfMonth(day.date, forecast.tzId),
        backgroundColor = color,
        onDayClicked = {
          onDayClicked(ind + 1)
        }
      )
    }
  }

  LaunchedEffect(index) {
    if (!isItemFullyVisible(listState, index - 1))
      listState.animateScrollToItem(index - 1)
  }
}

@Composable
private fun DayOfDailyForecast(
  modifier: Modifier = Modifier,
  titleTopText: String,
  titleBottomText: String,
  backgroundColor: Color,
  onDayClicked: () -> Unit
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
        .clickable { onDayClicked() },
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

private fun isItemFullyVisible(lazyListState: LazyListState, itemIndex: Int): Boolean {
  with(lazyListState.layoutInfo) {
    val editingTagItemVisibleInfo = visibleItemsInfo.find { it.index == itemIndex }
    return if (editingTagItemVisibleInfo == null) {
      false
    } else {
      viewportEndOffset - editingTagItemVisibleInfo.offset >= editingTagItemVisibleInfo.size
    }
  }
}