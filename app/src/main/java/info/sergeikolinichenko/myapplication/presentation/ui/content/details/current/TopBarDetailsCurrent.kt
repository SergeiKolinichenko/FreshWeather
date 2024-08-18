package info.sergeikolinichenko.myapplication.presentation.ui.content.details.current

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore
import info.sergeikolinichenko.myapplication.utils.SYS_ICON_SIZE_24

/** Created by Sergei Kolinichenko on 18.08.2024 at 13:11 (GMT+3) **/

@Composable
internal fun TopBar(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
  onBackButtonClick: () -> Unit,
  onSettingsClicked: () -> Unit
) {

  Box(
    modifier = modifier
      .fillMaxWidth(),
  ) {
    // Back button
    Icon(
      modifier = Modifier
        .size(24.dp)
        .align(Alignment.CenterStart)
        .clickable { onBackButtonClick() },
      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
      contentDescription = stringResource(R.string.details_content_description_text_back_button)
    )

    if (state.citiesState is DetailsStore.State.CitiesState.Loaded) {


      if (state.forecastState is DetailsStore.State.ForecastState.Loaded) {
        Row(
          modifier = modifier.align(Alignment.Center),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (number in state.citiesState.cities) {
            if (number.id == state.citiesState.id) {
              Icon(
                modifier = Modifier.size(8.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
                tint = Color.Black,
                contentDescription = null
              )
            } else {
              Icon(
                modifier = Modifier.size(8.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
                tint = Color.White,
                contentDescription = null
              )
            }
          }
        }
      }
    }
    Icon(
      modifier = Modifier
        .size(SYS_ICON_SIZE_24.dp)
        .align(Alignment.CenterEnd)
        .clickable { onSettingsClicked() },
      imageVector = Icons.Default.Settings,
      contentDescription = "Icon Settings"
    )
  }
}