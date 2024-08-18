package info.sergeikolinichenko.myapplication.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.component.EditingFavouritesComponent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.component.FavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.component.NextdaysComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.component.SearchComponent
import info.sergeikolinichenko.myapplication.presentation.screens.settings.component.SettingsComponent

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:11 (GMT+3) **/

interface RootComponent {

  val stack: Value<ChildStack<*, Child>>
  sealed interface Child {
    data class Favourite(val component: FavouriteComponent) : Child
    data class Details(val component: DetailsComponent) : Child
    data class Nextdays(val component: NextdaysComponent) : Child
    data class Search(val component: SearchComponent) : Child
    data class Settings(val component: SettingsComponent) : Child
    data class EditingFavourites(val component: EditingFavouritesComponent) : Child
  }
}