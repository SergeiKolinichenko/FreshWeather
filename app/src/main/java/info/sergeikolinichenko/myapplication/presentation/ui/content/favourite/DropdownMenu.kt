package info.sergeikolinichenko.myapplication.presentation.ui.content.favourite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore

/** Created by Sergei Kolinichenko on 14.07.2024 at 12:58 (GMT+3) **/

@Composable
fun DropdownMenu(
  modifier: Modifier = Modifier,
  state: FavouriteStore.State.DropDownMenuState,
  onDismissRequest: () -> Unit,
  onClickSettings: () -> Unit,
  onClickEditing: () -> Unit
) {

  DropdownMenu(
    modifier = modifier
      .background(MaterialTheme.colorScheme.surfaceVariant),
    expanded = state == FavouriteStore.State.DropDownMenuState.OpenMenu,
    onDismissRequest = { onDismissRequest() },
  ) {
    DropdownMenuItem(
      modifier = modifier
        .wrapContentWidth()
        .padding(bottom = 16.dp),
      contentPadding = PaddingValues(horizontal = 16.dp),
      leadingIcon = {
        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
      },
      text = {
        Text(
          text = stringResource(R.string.dropdown_menu_title_screen_editing),
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Normal,
          fontSize = 16.sp,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground
        )
      },
      onClick = { onClickEditing() }
    )
    DropdownMenuItem(
      modifier = modifier
        .wrapContentWidth(),
      contentPadding = PaddingValues(horizontal = 16.dp),
      leadingIcon = {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
      },
      text = {
        Text(
          text = stringResource(R.string.dropdown_menu_title_screen_settings),
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Normal,
          fontSize = 16.sp,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground
        )
      },
      onClick = { onClickSettings() }
    )
  }
}