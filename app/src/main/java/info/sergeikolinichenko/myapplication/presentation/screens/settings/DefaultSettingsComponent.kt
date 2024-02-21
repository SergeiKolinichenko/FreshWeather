package info.sergeikolinichenko.myapplication.presentation.screens.settings

import com.arkivanov.decompose.ComponentContext

class DefaultSettingsComponent(
    private val componentContext: ComponentContext
) : SettingsComponent, ComponentContext by componentContext {
}