package info.sergeikolinichenko.myapplication.presentation.ui.content.favourite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore
import info.sergeikolinichenko.myapplication.utils.toIconId

/** Created by Sergei Kolinichenko on 13.07.2024 at 17:54 (GMT+3) **/

@Composable
internal fun CityCard(
  modifier: Modifier = Modifier,
  item: FavouriteStore.State.CityItem,
  onItemClicked: () -> Unit
) {

  Card(
    modifier = modifier
      .fillMaxWidth()
      .sizeIn(minHeight = 136.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    when (val weatherState = item.weatherLoadingState) {
      is FavouriteStore.State.WeatherLoadingState.Error -> {

        // Error message

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
              weatherState.codeError,
              weatherState.cityName
            ),
            style = MaterialTheme.typography.titleMedium
          )
        }
      }

      FavouriteStore.State.WeatherLoadingState.Initial -> {}

      is FavouriteStore.State.WeatherLoadingState.LoadedWeatherLoading -> {

        // Weather in city

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
              Text(
                text = item.city.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = weatherState.temp,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
              )
            }
            Icon(
              modifier = Modifier.size(80.dp),
              painter = painterResource(id = weatherState.icon.toIconId()),
              tint = Color.Unspecified,
              contentDescription = null
            )
          }

          // Description, max and min temperature

          Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {

            // description

            BottomText(
              modifier = Modifier
                .weight(2f),
              text = weatherState.description
            )

            // max and min temperature

            Column(
              modifier = Modifier
                .weight(1f),
              horizontalAlignment = Alignment.End,
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
                  text = weatherState.maxTemp
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
                  text = weatherState.minTemp
                )
              }
            }
          }
        }
      }

      FavouriteStore.State.WeatherLoadingState.Loading -> {
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
  text: String
) {
  Text(
    modifier = modifier,
    text = text,
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    textAlign = TextAlign.Start,
    lineHeight = 16.sp,
    color = MaterialTheme.colorScheme.onBackground
  )
}