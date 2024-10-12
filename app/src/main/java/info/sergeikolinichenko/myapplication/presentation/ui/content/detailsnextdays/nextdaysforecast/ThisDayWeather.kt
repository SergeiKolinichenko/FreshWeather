package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.nextdaysforecast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.stors.nextdaysforecast.NextdaysStore
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.toIconId

/** Created by Sergei Kolinichenko on 06.09.2024 at 10:32 (GMT+3) **/

@Composable
internal fun ThisDayWeather(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
) {

  val forecasts = (state.forecastState as NextdaysStore.State.ForecastState.Loaded).forecasts
  val citiesState = (state.citiesState as NextdaysStore.State.CitiesState.Loaded)
  val forecast = forecasts.first { it.id == citiesState.id }
  val thisDayWeather = forecast.upcomingDays[state.index]

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

        val city = state.citiesState.cities.first { it.id == state.citiesState.id }

        ResponsiveText(
          modifier = Modifier
            .padding(bottom = 8.dp),
          text = city.name,
          textStyle = MaterialTheme.typography.headlineLarge,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1
        )

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
    ResponsiveText(
      text = thisDayWeather.description,
      textStyle = MaterialTheme.typography.bodyMedium,
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
  ResponsiveText(
    modifier = modifier,
    text = text,
    textStyle = MaterialTheme.typography.titleSmall,
    textAlign = TextAlign.Start,
    color = MaterialTheme.colorScheme.onBackground,
    maxLines = 1
  )
}