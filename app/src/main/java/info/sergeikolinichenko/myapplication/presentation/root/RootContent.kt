package info.sergeikolinichenko.myapplication.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.current.DetailsContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.favourite.FavouriteContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.nextdays.NextdaysContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.editing.EditingContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.search.SearchContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.settings.SettingsContent
import info.sergeikolinichenko.myapplication.presentation.ui.theme.FreshWeatherTheme

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:07 (GMT+3) **/

@Composable
fun RootContent(component: RootComponent) {
  FreshWeatherTheme {
    Children(stack = component.stack) { child ->
      when (val instance = child.instance) {
        is RootComponent.Child.Details -> DetailsContent(component = instance.component)
        is RootComponent.Child.Favourite -> FavouriteContent(component = instance.component)
        is RootComponent.Child.Search -> SearchContent(component = instance.component)
        is RootComponent.Child.Settings -> SettingsContent(component = instance.component)
        is RootComponent.Child.Nextdays -> NextdaysContent(component = instance.component)
        is RootComponent.Child.EditingFavourites -> EditingContent(component = instance.component)
      }
    }
  }
}