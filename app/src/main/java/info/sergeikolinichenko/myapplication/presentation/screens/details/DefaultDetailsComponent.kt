package info.sergeikolinichenko.myapplication.presentation.screens.details

import com.arkivanov.decompose.ComponentContext

class DefaultDetailsComponent(
    private val componentContext: ComponentContext
) : DetailsComponent, ComponentContext by componentContext {
}