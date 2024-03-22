package info.sergeikolinichenko.myapplication.presentation.screens.favourite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.CityScreen
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardDarkGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardLightGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.toRoundToIntString
import kotlin.random.Random

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:06 (GMT+3) **/
@Composable
fun FavouriteContent(component: FavouriteComponent) {

  val state = component.model.collectAsState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    FavoriteVerticalGrid(
      modifier = Modifier.align(Alignment.TopCenter),
      state = state,
      onClickSearch = { component.onSearchClicked() },
      onClickToCity = { city, numberGradient ->
        component.onItemClicked(city = city, numberGradient = numberGradient)
      }
    )
    AddFavouriteFloatingActionButton(
      modifier = Modifier.align(Alignment.BottomCenter),
      onClickFloatingActionButton = { component.onButtonClicked() }
    )

  }
}

@Composable
private fun FavoriteVerticalGrid(
  modifier: Modifier = Modifier,
  state: State<FavouriteStore.State>,
  onClickSearch: () -> Unit,
  onClickToCity: (CityScreen, Int) -> Unit
) {
  LazyVerticalGrid(
    modifier = modifier.fillMaxSize(),
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item(
      span = { GridItemSpan(2) }
    ) {
      SearchCard(
        onClickSearch = {
          onClickSearch()
        }
      )
    }
    itemsIndexed(
      items = state.value.cityItems,
      key = { _, item -> item.city.id }
    ) { index, item ->
      val numberGradient = index % 5
      CityCard(
        item = item,
        numberGradient = numberGradient,
        onClickToCity = { onClickToCity(item.city, numberGradient) }
      )
    }
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CityCard(
  modifier: Modifier = Modifier,
  item: FavouriteStore.State.CityItem,
  numberGradient: Int = 0,
  onClickToCity: () -> Unit
) {

  val gradient = getGradient(numberGradient)

  Card(
    modifier = modifier
      .fillMaxSize()
      .shadow(
        elevation = 16.dp,
        spotColor = gradient.shadowColor,
        shape = MaterialTheme.shapes.extraLarge
      ),
    shape = MaterialTheme.shapes.extraLarge
  ) {
    Box(
      modifier = modifier
        .background(gradient.primaryGradient)
        .sizeIn(minHeight = 196.dp, minWidth = 100.dp)
        .drawBehind {
          drawCircle(
            brush = gradient.secondaryGradient,
            center = Offset(
              x = center.x - size.width / 15,
              y = center.y + size.height / 3
            ),
            radius = size.maxDimension / 2
          )
        }
        .padding(24.dp)
        .clickable { onClickToCity() }
    ) {

      Column(
        modifier = Modifier
          .fillMaxSize()
          .align(Alignment.BottomStart),
        verticalArrangement = Arrangement.SpaceEvenly
      ) {

        when (val weatherState = item.weatherState) {
          FavouriteStore.State.WeatherState.Error -> {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stringResource(R.string.favourite_content_error_weather_for_city),
                style = MaterialTheme.typography.bodyLarge
              )
            }
          }

          FavouriteStore.State.WeatherState.Initial -> {}
          is FavouriteStore.State.WeatherState.LoadedWeather -> {
            GlideImage(
              modifier = Modifier
                .align(Alignment.End)
                .size(60.dp),
              model = weatherState.iconUrl,
              contentDescription = stringResource(R.string.content_icon_description_weather_icon)
            )
            Text(
              modifier = Modifier.align(Alignment.Start),
              style = MaterialTheme.typography.bodyLarge.copy(fontSize = 40.sp),
              text = weatherState.temperature.toRoundToIntString()
            )
          }

          FavouriteStore.State.WeatherState.Loading -> {
            CircularProgressIndicator(
              modifier = Modifier
                .align(Alignment.CenterHorizontally)
            )
          }
        }
        ResponsiveText(
          text = item.city.name,
          modifier = Modifier.align(Alignment.Start),
          textStyle = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground
        )
        ResponsiveText(
          text = item.city.country,
          modifier = Modifier.align(Alignment.Start),
          textStyle = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
    }
  }
}

@Composable
private fun getGradient(numberGradient: Int): Gradient {
  val gradients = if (isSystemInDarkTheme()) CardDarkGradients.gradients
    else CardLightGradients.gradients

  return gradients[numberGradient]
}

@Composable
private fun AddFavouriteFloatingActionButton(
  modifier: Modifier = Modifier,
  onClickFloatingActionButton: () -> Unit
) {
  ExtendedFloatingActionButton(
    modifier = modifier
      .padding(16.dp),
    onClick = { onClickFloatingActionButton() },
    icon = {
      Icon(
        Icons.Filled.Add,
        "Add Favourite"
      )
    },
    text = {
      Text(
        text = stringResource(R.string.ext_floating_action_button_title),
        style = MaterialTheme.typography.titleMedium
      )
    },
  )
}

@Composable
private fun SearchCard(
  modifier: Modifier = Modifier,
  onClickSearch: () -> Unit
) {
  val gradient = getGradient(Random.nextInt(0, 5))
  Card(
    modifier = modifier
      .fillMaxWidth(),
    shape = CircleShape,
  ) {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .background(gradient.primaryGradient)
        .clickable { onClickSearch() },
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 8.dp),
        imageVector = Icons.Default.Search,
        contentDescription = "Search"
      )
      Text(
        modifier = Modifier.padding(end = 16.dp),
        text = stringResource(R.string.favourite_content_text_search)
      )
    }
  }
}