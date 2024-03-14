package info.sergeikolinichenko.myapplication.presentation.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.toCityScreen

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:06 (GMT+3) **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(component: SearchComponent) {

  val state by component.model.collectAsState()
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(key1 = null){ focusRequester.requestFocus() }

  SearchBar(
    modifier = Modifier.focusRequester(focusRequester = focusRequester),
    placeholder = {
      Text(
        modifier = Modifier.padding(end = 16.dp),
        text = stringResource(R.string.favourite_content_text_search),
        color = MaterialTheme.colorScheme.onBackground
      )
    },
    query = state.query,
    onQueryChange = { component.onQueryChanged(it) },
    onSearch = { component.onSearchClicked() },
    active = true,
    leadingIcon = {
      IconButton(onClick = { component.onBackClicked() }) {
        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
      }
    },
    trailingIcon = {
      IconButton(onClick = { component.onSearchClicked() }) {
        Icon(imageVector = Icons.Default.Search, contentDescription = null)
      }
    },
    onActiveChange = {}
  ) {
    when(val weatherState = state.state) {
      SearchStore.State.SearchState.Empty -> {
        Text(
          modifier = Modifier.padding(16.dp),
          text = stringResource(R.string.search_content_message_text_nothing_found_on_your_request)
        )
      }
      SearchStore.State.SearchState.Error -> {
        Text(
          modifier = Modifier.padding(16.dp),
          text = stringResource(R.string.search_content_message_text_error_something_gone_wrong)
        )
      }
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
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(16.dp)
        ) {
          items(
            items = weatherState.cities,
            key = { it.id }
          ) { item ->
            CityCard(
              city = item,
              onCityClicked = { component.onItemClicked(city = it.toCityScreen()) }
            )
          }
        }
      }
    }
  }
}
@Composable
private fun CityCard(
  modifier: Modifier = Modifier,
  city: City,
  onCityClicked: (City) -> Unit
) {
  Card(
    modifier = modifier.fillMaxSize()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clickable { onCityClicked(city) }
    ) {
      Text(text = city.name, style = MaterialTheme.typography.titleMedium)
      Spacer(modifier = Modifier.padding(8.dp))
      Text(text = city.region)
      Spacer(modifier = Modifier.padding(8.dp))
      Text(text = city.country)

    }
  }
}