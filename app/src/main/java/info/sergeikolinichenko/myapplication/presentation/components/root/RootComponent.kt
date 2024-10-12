package info.sergeikolinichenko.myapplication.presentation.components.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import info.sergeikolinichenko.myapplication.presentation.components.details.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.components.editing.EditingComponent
import info.sergeikolinichenko.myapplication.presentation.components.favourite.FavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.components.nextdays.NextdaysComponent
import info.sergeikolinichenko.myapplication.presentation.components.search.SearchComponent
import info.sergeikolinichenko.myapplication.presentation.components.settings.SettingsComponent

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:11 (GMT+3) **/

interface RootComponent {

  val childStack: Value<ChildStack<*, Child>>

  sealed interface Child {
    data class Favourite(val component: FavouriteComponent) : Child
    data class Details(val component: DetailsComponent) : Child
    data class Nextdays(val component: NextdaysComponent) : Child
    data class Search(val component: SearchComponent) : Child
    data class Settings(val component: SettingsComponent) : Child
    data class EditingFavourites(val component: EditingComponent) : Child
  }
}