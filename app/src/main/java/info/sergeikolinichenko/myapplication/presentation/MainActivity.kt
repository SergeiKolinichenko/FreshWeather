package info.sergeikolinichenko.myapplication.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import info.sergeikolinichenko.myapplication.di.WeatherApp
import info.sergeikolinichenko.myapplication.presentation.components.root.DefaultRootComponent
import info.sergeikolinichenko.myapplication.presentation.ui.content.root.RootContent
import javax.inject.Inject

class MainActivity : ComponentActivity() {

  @Inject
  lateinit var rootComponent: DefaultRootComponent.Factory

  override fun onCreate(savedInstanceState: Bundle?) {
    (applicationContext as WeatherApp).appComponent.inject(this)
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()

    setContent {
      RootContent(component = rootComponent.create(defaultComponentContext()))
    }
  }

}