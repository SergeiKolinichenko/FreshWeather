package info.sergeikolinichenko.myapplication.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import info.sergeikolinichenko.myapplication.presentation.screens.details.content.DetailsContent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.content.FavouriteContent
import info.sergeikolinichenko.myapplication.presentation.screens.search.SearchContent
import info.sergeikolinichenko.myapplication.presentation.ui.theme.FreshWeatherTheme

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:07 (GMT+3) **/

@Composable
fun RootContent(component: RootComponent) {
  FreshWeatherTheme {
    Children(stack = component.stack) { child ->
      when (val instance = child.instance) {
        is RootComponent.Child.Details -> DetailsContent(instance.component)
        is RootComponent.Child.Favourite -> FavouriteContent(instance.component)
        is RootComponent.Child.Search -> SearchContent(instance.component)
      }
    }
  }
}