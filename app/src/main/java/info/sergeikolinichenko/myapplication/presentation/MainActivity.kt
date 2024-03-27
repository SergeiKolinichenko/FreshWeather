package info.sergeikolinichenko.myapplication.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import info.sergeikolinichenko.myapplication.di.WeatherApp
import info.sergeikolinichenko.myapplication.presentation.root.DefaultRootComponent
import info.sergeikolinichenko.myapplication.presentation.root.RootContent
import javax.inject.Inject

class MainActivity : ComponentActivity() {

  @Inject
  lateinit var rootComponent: DefaultRootComponent.Factory

  override fun onCreate(savedInstanceState: Bundle?) {
    (applicationContext as WeatherApp).appComponent.inject(this)
    super.onCreate(savedInstanceState)

    installSplashScreen().apply {
      setOnExitAnimationListener { splashScreenViewProvider ->
        splashScreenViewProvider.view.animate().alpha(0f).withEndAction {
          splashScreenViewProvider.remove()
        }
      }
    }

    setContent {
      RootContent(component = rootComponent.create(defaultComponentContext()))
    }
  }
}