package info.sergeikolinichenko.myapplication.presentation.ui.content.favourite

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore

/** Created by Sergei Kolinichenko on 13.07.2024 at 17:54 (GMT+3) **/

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun CityCard(
  modifier: Modifier = Modifier,
  item: FavouriteStore.State.CityItem,
  onItemClicked: () -> Unit
) {

  Card(
    modifier = modifier
      .fillMaxWidth()
      .sizeIn(minWidth = 348.dp, minHeight = 136.dp),
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
      when (val weatherState = item.weatherLoadingState) {
        FavouriteStore.State.WeatherLoadingState.Error -> {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = stringResource(R.string.favourite_content_error_weather_for_city),
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W600)
            )
          }
        }

        FavouriteStore.State.WeatherLoadingState.Initial -> {}

        is FavouriteStore.State.WeatherLoadingState.LoadedWeatherLoading -> {
          Column(
            modifier = Modifier
              .clickable { onItemClicked() }
              .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                .sizeIn(minWidth = 348.dp, minHeight = 80.dp)
                .alpha(90f),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = item.city.name,
                  style = MaterialTheme.typography.titleLarge.copy(),
                  color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                  text = weatherState.temp,
                  style = MaterialTheme.typography.headlineMedium,
                  color = MaterialTheme.colorScheme.onBackground
                )
              }
              GlideImage(
                modifier = Modifier
                  .size(80.dp),
                model = weatherState.iconUrl,
                contentDescription = stringResource(R.string.favourite_content_description_weather_icon)
              )
            }
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
                .sizeIn(minWidth = 348.dp, minHeight = 16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              BottomText(
                modifier = Modifier
                  .weight(1f),
                text = weatherState.description
              )
              Box(
                modifier = Modifier
                  .padding(start = 34.dp)
                  .weight(2f)
              ) {
                Row(
                  modifier = Modifier.align(Alignment.BottomStart),
                  horizontalArrangement = Arrangement.SpaceAround
                ) {
                  BottomText(
                    text = "Max: "
                  )
                  BottomText(
                    text = weatherState.maxTemp
                  )
                }
                Row(
                  modifier = Modifier.align(Alignment.BottomEnd),
                  horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                  BottomText(
                    text = "Min: "
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
    fontSize = 12.sp,
    textAlign = TextAlign.Start,
    lineHeight = 16.sp,
    color = MaterialTheme.colorScheme.onBackground
  )
}