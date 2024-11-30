package info.sergeikolinichenko.myapplication.presentation.ui.content.root

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.FaultyDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import info.sergeikolinichenko.myapplication.presentation.components.root.RootComponent
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.currentweather.CurrentWeatherContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.nextdaysforecast.NextdaysContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.editing.EditingContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.favourite.FavouriteContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.search.SearchContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.settings.SettingsContent
import info.sergeikolinichenko.myapplication.presentation.ui.theme.FreshWeatherTheme

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:07 (GMT+3) **/

@OptIn(FaultyDecomposeApi::class)
@Composable
fun RootContent(component: RootComponent) {

  FreshWeatherTheme {

    Children(
      modifier = Modifier.fillMaxSize(),
      stack = component.childStack,
      animation = stackAnimation { child, otherChild, _ ->
        when {
          child.instance is RootComponent.Child.Favourite &&
              otherChild.instance is RootComponent.Child.EditingFavourites -> slide().invert()

          child.instance is RootComponent.Child.EditingFavourites &&
              otherChild.instance is RootComponent.Child.Favourite -> slide().invert()

          child.instance is RootComponent.Child.Favourite &&
              otherChild.instance is RootComponent.Child.Details -> slide()

          child.instance is RootComponent.Child.Details &&
              otherChild.instance is RootComponent.Child.Favourite -> slide()

          child.instance is RootComponent.Child.Favourite &&
              otherChild.instance is RootComponent.Child.Settings -> slide(orientation = Orientation.Vertical)

          child.instance is RootComponent.Child.Settings &&
              otherChild.instance is RootComponent.Child.Favourite -> slide(orientation = Orientation.Vertical)

          child.instance is RootComponent.Child.Details &&
              otherChild.instance is RootComponent.Child.Nextdays -> slide(orientation = Orientation.Vertical)

          child.instance is RootComponent.Child.Nextdays &&
              otherChild.instance is RootComponent.Child.Details -> slide(orientation = Orientation.Vertical)

          else -> slide(orientation = Orientation.Vertical).invert()
        }
      }
    ) { child ->

      when (val instance = child.instance) {
        is RootComponent.Child.Details -> CurrentWeatherContent(component = instance.component)
        is RootComponent.Child.Favourite -> FavouriteContent(component = instance.component)
        is RootComponent.Child.Search -> SearchContent(component = instance.component)
        is RootComponent.Child.Settings -> SettingsContent(component = instance.component)
        is RootComponent.Child.Nextdays -> NextdaysContent(component = instance.component)
        is RootComponent.Child.EditingFavourites -> EditingContent(component = instance.component)
      }
    }
    Box(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .background(MaterialTheme.colorScheme.primary)
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.systemBars)
      )
    }
  }
}


fun StackAnimator.invert(): StackAnimator =
  StackAnimator { direction, isInitial, onFinished, content ->
    this(
      direction = direction.invert(),
      isInitial = isInitial,
      onFinished = onFinished,
      content = content,
    )
  }

private fun Direction.invert(): Direction =
  when (this) {
    Direction.ENTER_FRONT -> Direction.ENTER_BACK
    Direction.EXIT_FRONT -> Direction.EXIT_BACK
    Direction.ENTER_BACK -> Direction.ENTER_FRONT
    Direction.EXIT_BACK -> Direction.EXIT_FRONT
  }
