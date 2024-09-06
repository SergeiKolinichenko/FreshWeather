package info.sergeikolinichenko.myapplication.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.currentweather.DetailsContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.nextdaysforecast.NextdaysContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.editing.EditingContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.favourite.FavouriteContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.search.SearchContent
import info.sergeikolinichenko.myapplication.presentation.ui.content.settings.SettingsContent
import info.sergeikolinichenko.myapplication.presentation.ui.theme.FreshWeatherTheme

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:07 (GMT+3) **/

@Composable
fun RootContent(component: RootComponent) {

  FreshWeatherTheme {
    Children(stack = component.stack) { child ->

//      AnimatedContent(
//        targetState = child.instance,
//        transitionSpec = {
//          if (targetState is RootComponent.Child.Editing) {
//            Log.d("TAG", "RootContent: ${child.instance}")
//            slideInHorizontally(
//              initialOffsetX = { fullWidth -> fullWidth },
//              animationSpec = tween(5000)
//            ) togetherWith slideOutHorizontally(
//              targetOffsetX = { fullWidth -> -fullWidth },
//              animationSpec = tween(5000))
//          } else {
//            slideInHorizontally(
//              initialOffsetX = { fullWidth -> -fullWidth },
//              animationSpec = tween(5000)
//            ) togetherWith slideOutHorizontally(
//              targetOffsetX = { fullWidth -> fullWidth },
//              animationSpec = tween(5000))
//          }.using(
//            // Disable clipping since the faded slide-in/out should
//            // be displayed out of bounds.
//            SizeTransform(clip = false))
//        }, label = "AnimatedContent"
//      ) { stack ->
//        when (stack) {
//          is RootComponent.Child.Details -> DetailsContent(component = stack.component)
//          is RootComponent.Child.Favourite -> FavouriteContent(component = stack.component)
//          is RootComponent.Child.Search -> SearchContent(component = stack.component)
//          is RootComponent.Child.Settings -> SettingsContent(component = stack.component)
//          is RootComponent.Child.Nextdays -> NextdaysContent(component = stack.component)
//          is RootComponent.Child.Editing -> EditingContent(component = stack.component)
//
//        }
//      }

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