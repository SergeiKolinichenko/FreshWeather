package info.sergeikolinichenko.myapplication.presentation.ui.content.favourite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore

/** Created by Sergei Kolinichenko on 13.07.2024 at 17:56 (GMT+3) **/

@Composable
internal fun SearchCard(
  modifier: Modifier = Modifier,
  state: FavouriteStore.State.DropDownMenuState,
  onClickSearch: () -> Unit,
  onClickActionMenu: () -> Unit,
  onDismissRequest: () -> Unit,
  onClickSettings: () -> Unit,
  onClickEditing: () -> Unit
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(64.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Box(
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .weight(8f)
          .clickable { onClickSearch() }
      ) {
          Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = stringResource(R.string.title_few_contents_text_favourite),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground
          )
          Icon(
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .size(24.dp),
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(R.string.favourite_content_description_search_button)
          )
      }
      Box(
        modifier = Modifier
            .weight(1f)
      ) {
        DropdownMenu(
          modifier = Modifier
            .align(Alignment.BottomEnd),
          state = state,
          onDismissRequest = { onDismissRequest() },
          onClickSettings = { onClickSettings() },
          onClickEditing = { onClickEditing() }
        )
        Icon(
          modifier = Modifier
            .size(24.dp)
            .clickable { onClickActionMenu() },
          imageVector = Icons.Default.MoreVert,
          contentDescription = stringResource(R.string.favourite_content_description_menu_icon)
        )
      }
    }
  }
}