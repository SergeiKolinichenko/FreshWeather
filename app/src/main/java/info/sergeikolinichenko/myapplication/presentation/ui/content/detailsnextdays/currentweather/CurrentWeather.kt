package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.currentweather

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.toIconId

/** Created by Sergei Kolinichenko on 04.08.2024 at 10:51 (GMT+3) **/

@Composable
internal fun CurrentWeather(
  modifier: Modifier = Modifier,
  forecast: ForecastFs,
  city: CityFs
) {

  Column(
    modifier = modifier
      .fillMaxWidth(),
    horizontalAlignment = Alignment.Start
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {

        ResponsiveText(
          modifier = Modifier
            .padding(bottom = 8.dp),
          text =city.name,
          textStyle = MaterialTheme.typography.headlineLarge,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

          ResponsiveText(
            text = forecast.currentForecast.temp,
            targetTextSizeHeight = 80.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
          )
        }

        ResponsiveText(
          text = stringResource(
            R.string.details_content_current_weather_text_feels_like,
            forecast.currentForecast.feelsLike
          ),
          textStyle = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1
        )

      }
      Icon(
        modifier = Modifier.size(120.dp),
        painter = painterResource(id = forecast.currentForecast.icon.toIconId()),
        tint = Color.Unspecified,
        contentDescription = null
      )
    }

    ResponsiveText(
      text = forecast.upcomingDays.first().description,
      textStyle = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}