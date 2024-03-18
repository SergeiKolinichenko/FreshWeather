package info.sergeikolinichenko.myapplication.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.myapplication.entity.CityScreen
import info.sergeikolinichenko.myapplication.presentation.screens.details.DefaultDetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.DefaultFavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.DefaultSearchComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.OpeningOptions
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

class DefaultRootComponent @AssistedInject constructor(
    private val detailsComponentFactory: DefaultDetailsComponent.Factory,
    private val favouriteComponentFactory: DefaultFavouriteComponent.Factory,
    private val searchComponentFactory: DefaultSearchComponent.Factory,
    @Assisted("componentContext") private val componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Favourite,
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        is Config.Favourite -> {
            val component = favouriteComponentFactory.create(
                componentContext = componentContext,
                onClickSearch = { navigation.push(Config.Search(OpeningOptions.ORDINARY_SEARCH)) },
                onClickButton = { navigation.push(Config.Search(OpeningOptions.ADD_TO_FAVORITES)) },
                onClickCity = { city, numberGradient ->
                    navigation.push(Config.Details(city = city, numberGradient = numberGradient))
                }
            )
            RootComponent.Child.Favourite(component)
        }
        is Config.Details -> {
            val component = detailsComponentFactory.create(
                componentContext = componentContext,
                city = config.city,
                gradient = config.numberGradient,
                onClickBack = { navigation.pop() }
            )
            RootComponent.Child.Details(component)
        }
        is Config.Search -> {
            val component = searchComponentFactory.create(
                componentContext = componentContext,
                openingOptions = config.options,
                onClickBack = { navigation.pop() },
                onClickItem = { navigation.push(Config.Details(
                    city = it,
                    numberGradient = Random.nextInt(0, 5)
                )) },
                savedToFavourite = { navigation.pop() }
            )
            RootComponent.Child.Search(component)
        }
    }

     sealed interface Config: Parcelable {
        @Parcelize
        data object Favourite : Config
        @Parcelize
        data class Details(val city: CityScreen, val numberGradient: Int) : Config
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