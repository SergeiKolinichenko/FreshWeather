package info.sergeikolinichenko.myapplication.presentation.ui.content.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.search.component.SearchComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStore
import info.sergeikolinichenko.myapplication.utils.toCityScreen

const val TEST_SEARCHBAR_TAG = "test_searchbar_tag"
const val TEST_SEARCH_TEXT_TAG = "test_search_text_tag"

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:06 (GMT+3) **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(component: SearchComponent) {

  val state by component.model.collectAsState()

  val focusRequester = remember { FocusRequester() }
  LaunchedEffect(key1 = null) { focusRequester.requestFocus() }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    SearchBar(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp)
        .focusRequester(focusRequester = focusRequester)
        .testTag(TEST_SEARCHBAR_TAG),

      placeholder = {
        Text(
          modifier = Modifier
            .padding(end = 16.dp),
          text = stringResource(R.string.search_content_text_into_placeholder),
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Normal,
          fontSize = 16.sp,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground
        )
      },

      query = state.query,

      onQueryChange = { component.onQueryChanged(it) },
      onSearch = { component.onQueryChanged(it) },
      active = true,
      leadingIcon = {
        IconButton(onClick = { component.onBackClicked() }) {
          Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.content_description_text_back_button)
          )
        }
      },
      trailingIcon = {
        IconButton(onClick = { component.onClickedClearLine() }) {
          Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.content_description_text_clear_line)
          )
        }
      },
      onActiveChange = {
        if (!it) component.onBackClicked()
      },
      colors = SearchBarDefaults.colors(
        containerColor = Color.Transparent
      )
    ) {

      when (val searchState = state.state) {

        SearchStore.State.SearchState.Empty ->
          ErrorMessage(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.search_content_message_text_nothing_found_on_your_request)
          )


        SearchStore.State.SearchState.Error ->
          ErrorMessage(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.search_content_message_text_error_something_gone_wrong)
          )

        SearchStore.State.SearchState.Initial -> {}
        SearchStore.State.SearchState.Loading -> {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(
              color = MaterialTheme.colorScheme.onBackground
            )
          }
        }

        is SearchStore.State.SearchState.SuccessLoaded -> {
          LazyColumn(
            modifier = Modifier
              .padding(top = 16.dp)
              .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            contentPadding = PaddingValues()
          ) {

            items(
              items = searchState.cities,
              key = { item -> item.id }
            ) { item ->

              Text(
                modifier = Modifier
                  .fillMaxWidth()
                  .align(Alignment.Start)
                  .clickable { component.onItemClicked(city = item.toCityScreen()) }
                  .testTag(TEST_SEARCH_TEXT_TAG),
                text = item.name + ", " + item.region + ", " + item.country,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground
              )
            }
          }
        }

        SearchStore.State.SearchState.NotEnoughLetters ->
          ErrorMessage(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.search_content_message_enter_more_than_3_letters_to_start_the_search)
          )
      }
    }
  }
}


@Composable
private fun ErrorMessage(
  modifier: Modifier = Modifier,
  text: String
) {
    Text(
      modifier = modifier.padding(top = 16.dp),
      text = text,
      textAlign = TextAlign.Start,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      fontSize = 16.sp,
      color = MaterialTheme.colorScheme.onBackground
    )
}