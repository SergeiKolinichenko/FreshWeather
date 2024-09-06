package info.sergeikolinichenko.myapplication.presentation.ui.content.favourite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.toIconId

/** Created by Sergei Kolinichenko on 13.07.2024 at 17:54 (GMT+3) **/

@Composable
internal fun CityCard(
  modifier: Modifier = Modifier,
  city: CityFs,
  forecastState: FavouriteStore.State.ForecastState,
  onItemClicked: () -> Unit
) {

  if (forecastState is FavouriteStore.State.ForecastState.Initial) return

  Card(
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = 136.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {

    when (forecastState) {

      FavouriteStore.State.ForecastState.Initial -> {}

      is FavouriteStore.State.ForecastState.Error -> {
        // Failed message
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            modifier = Modifier
              .fillMaxSize()
              .padding(8.dp)
              .align(Alignment.Center),
            text = stringResource(
              R.string.favourite_content_error_weather_for_city,
              forecastState.errorMessage,
              city.name
            ),
            style = MaterialTheme.typography.titleMedium
          )
        }
      }

      is FavouriteStore.State.ForecastState.Loaded -> {
        // Weather in city

        val forecast = forecastState.listForecast.first { it.id == city.id }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            .clickable { onItemClicked() },
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

          // City name, temperature and icon

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {

            Column(
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              ResponsiveText(
                text = city.name,
                textStyle = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
              )

              ResponsiveText(
                text = forecast.currentForecast.temp,
                textStyle = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
              )

            }
            Icon(
              modifier = Modifier.size(80.dp),
              painter = painterResource(
                id = forecast.currentForecast.icon.toIconId()
              ),
              tint = Color.Unspecified,
              contentDescription = null
            )
          }

          // Description, max and min temperature

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(intrinsicSize = IntrinsicSize.Max),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {

            // description

            BottomText(
              modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
              text = forecast.currentForecast.conditions,
              softWrap = true
            )

            // max and min temperature
            Column(
              modifier = Modifier,
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                BottomText(
                  text = stringResource(R.string.caption_max)
                )
                BottomText(
                  text = forecast.upcomingDays.first().tempMax
                )
              }
              Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                BottomText(
                  text = stringResource(R.string.caption_min)
                )
                BottomText(
                  text = forecast.upcomingDays.first().tempMin
                )
              }
            }
          }
        }
      }

      FavouriteStore.State.ForecastState.Loading -> {
        Box(modifier = Modifier.fillMaxSize()) {
          CircularProgressIndicator(
            modifier = Modifier
              .size(48.dp)
              .padding(top = 60.dp)
              .align(Alignment.Center)
          )
        }
      }
    }
  }
}

@Composable
private fun BottomText(
  modifier: Modifier = Modifier,
  text: String,
  softWrap: Boolean = false
) {
  ResponsiveText(
    modifier = modifier,
    text = text,
    targetTextSizeHeight = 12.sp,
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    textAlign = TextAlign.Start,
    lineHeight = 14.sp,
    color = MaterialTheme.colorScheme.onBackground,
    softWrap = softWrap
  )
}