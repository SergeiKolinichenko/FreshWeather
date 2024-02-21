package info.sergeikolinichenko.myapplication.presentation.screens.favourite

import com.arkivanov.decompose.ComponentContext
class DefaultFavouriteComponent(
    private val componentContext: ComponentContext
) : FavouriteComponent, ComponentContext by componentContext {
}