package info.sergeikolinichenko.myapplication.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.CityToScreen
import info.sergeikolinichenko.myapplication.presentation.screens.details.DefaultDetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.DefaultFavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.DefaultSearchComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.OpeningOptions
import kotlinx.parcelize.Parcelize

class DefaultRootComponent @AssistedInject constructor(
    private val detailsComponentFactory: DefaultDetailsComponent.Factory,
    private val favouriteComponentFactory: DefaultFavouriteComponent.Factory,
    private val searchComponentFactory: DefaultSearchComponent.Factory,
    @Assisted("componentContext") private val componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    override val stack: Value<ChildStack<*, RootComponent.Child>>
        get() = TODO("Not yet implemented")

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        is Config.Favourite -> {
            val component = favouriteComponentFactory.create(
                componentContext = componentContext,
                onClickSearch = { /*TODO*/ },
                onClickButton = { /*TODO*/ },
                onClickCity = { /*TODO*/ }
            )
            RootComponent.Child.Favourite(component)
        }
        is Config.Details -> {
            val component = detailsComponentFactory.create(
                componentContext = componentContext,
                city = config.city,
                onClickBack = { /*TODO*/ }
            )
            RootComponent.Child.Details(component)
        }
        is Config.Search -> {
            val component = searchComponentFactory.create(
                componentContext = componentContext,
                openingOptions = config.options,
                onClickBack = { /*TODO*/ },
                onClickItem = { /*TODO*/ },
                savedToFavourite = { /*TODO*/ }
            )
            RootComponent.Child.Search(component)
        }
    }

     sealed interface Config: Parcelable {
        @Parcelize
        data object Favourite : Config
        @Parcelize
        data class Details(val city: CityToScreen) : Config
        @Parcelize
        data class Search(val options: OpeningOptions) : Config
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultRootComponent
    }
}