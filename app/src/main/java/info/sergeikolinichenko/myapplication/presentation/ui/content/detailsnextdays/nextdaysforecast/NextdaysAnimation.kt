package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.nextdaysforecast

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import info.sergeikolinichenko.myapplication.presentation.stors.nextdaysforecast.NextdaysStore
import info.sergeikolinichenko.myapplication.utils.DURATION_OF_ANIMATION

/** Created by Sergei Kolinichenko on 14.08.2024 at 10:12 (GMT+3) **/

@Composable
internal fun NextdaysAnimation(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
  onDayClicked: (Int) -> Unit,
  onCloseClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit,
  onSwipeTop: () -> Unit,
  onSwipeBottom: () -> Unit
) {

  AnimatedContent(
    targetState = state,
    transitionSpec = {

      val initialCityIndex = (initialState.citiesState as NextdaysStore.State.CitiesState.Loaded)
        .cities.indexOfFirst { it.id == (initialState.citiesState as NextdaysStore.State.CitiesState.Loaded).id }

      val targetCityIndex = (targetState.citiesState as NextdaysStore.State.CitiesState.Loaded)
        .cities.indexOfFirst { it.id == (targetState.citiesState as NextdaysStore.State.CitiesState.Loaded).id }

      if (initialCityIndex < targetCityIndex) {
        
        (fadeIn(animationSpec = tween(DURATION_OF_ANIMATION))
            + slideInHorizontally(animationSpec = tween(DURATION_OF_ANIMATION),
          initialOffsetX = { fullWidth -> fullWidth }))
          .togetherWith(
            fadeOut(animationSpec = tween(DURATION_OF_ANIMATION))
                + slideOutHorizontally(animationSpec = tween(DURATION_OF_ANIMATION),
              targetOffsetX = { fullWidth -> -fullWidth })
          )

      } else if (initialCityIndex > targetCityIndex) {

        (fadeIn(animationSpec = tween(DURATION_OF_ANIMATION))
            + slideInHorizontally(animationSpec = tween(DURATION_OF_ANIMATION),
          initialOffsetX = { fullWidth -> -fullWidth }))
          .togetherWith(
            fadeOut(animationSpec = tween(DURATION_OF_ANIMATION))
                + slideOutHorizontally(animationSpec = tween(DURATION_OF_ANIMATION),
              targetOffsetX = { fullWidth -> fullWidth })
          )

      } else if (initialState.index < targetState.index) {

        (fadeIn() + slideInVertically(animationSpec = tween(DURATION_OF_ANIMATION),
          initialOffsetY = { fullHeight -> fullHeight })).togetherWith(
          fadeOut(animationSpec = tween(DURATION_OF_ANIMATION))
        )

      } else {

        (fadeIn() + slideInVertically(animationSpec = tween(DURATION_OF_ANIMATION),
          initialOffsetY = { fullHeight -> -fullHeight })).togetherWith(
          fadeOut(animationSpec = tween(DURATION_OF_ANIMATION))
        )

      }
    }, label = "NextdaysAnimation"
  )
  { targetState ->

    MainScreen(
      modifier = modifier,
      state = targetState,
      onDayClicked = {
        onDayClicked(it)
      },
      onCloseClicked = { onCloseClicked() },
      onSwipeLeft = {
        onSwipeLeft()
      },
      onSwipeRight = {
        onSwipeRight()
      },
      onSwipeTop = {
        onSwipeTop()
      },
      onSwipeBottom = {
        if (state.forecastState is NextdaysStore.State.ForecastState.Loaded) {

          val id = (state.citiesState as NextdaysStore.State.CitiesState.Loaded).id
          val forecast = state.forecastState.forecasts.find { it.id == id }
            ?: state.forecastState.forecasts.first()

          if (state.index < forecast.upcomingDays.size - 1) {
            onSwipeBottom()
          }
        }
      }
    )
  }
}