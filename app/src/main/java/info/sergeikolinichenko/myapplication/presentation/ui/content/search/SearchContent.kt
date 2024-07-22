package info.sergeikolinichenko.myapplication.presentation.ui.content.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.search.component.SearchComponent
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStore
import info.sergeikolinichenko.myapplication.utils.getGradient
import info.sergeikolinichenko.myapplication.utils.toCityScreen

const val TEST_SEARCHBAR_TAG = "test_searchbar_tag"

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:06 (GMT+3) **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(component: SearchComponent) {

  val state by component.model.collectAsState()

  val focusRequester = remember { FocusRequester() }
  LaunchedEffect(key1 = null) { focusRequester.requestFocus() }

  SearchBar(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .focusRequester(focusRequester = focusRequester)
      .testTag(TEST_SEARCHBAR_TAG),
    placeholder = {
      Text(
        modifier = Modifier
          .padding(end = 16.dp),
        text = stringResource(R.string.search_content_text_into_placeholder),
        color = MaterialTheme.colorScheme.onBackground
      )
    },
    query = state.query,
    onQueryChange = { component.onQueryChanged(it) },
    onSearch = { component.onSearchClicked() },
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
      IconButton(onClick = { component.onSearchClicked() }) {
        Icon(
          modifier = Modifier.size(24.dp),
          imageVector = Icons.Default.Search,
          contentDescription = stringResource(R.string.content_description_text_search_button)
        )
      }
    },
    onActiveChange = {
      if (!it) component.onBackClicked()
    }
  ) {
    when (val weatherState = state.state) {
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
          itemsIndexed(
            items = weatherState.cities,
            key = { _, item -> item.id }
          ) { index, item ->

            val numberGradient = index % 5

            CityCard(
              city = item,
              numberGradient = numberGradient,
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
  numberGradient: Int = 0,
  onCityClicked: (City) -> Unit
) {

  val gradient = getGradient(numberGradient)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = 16.dp,
        spotColor = gradient.shadowColor,
        shape = MaterialTheme.shapes.extraLarge
      ),
    shape = MaterialTheme.shapes.extraLarge
  ) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(gradient.primaryGradient)
        .padding(14.dp)
        .clickable { onCityClicked(city) }
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .align(Alignment.TopStart)
      ) {
        Row(
          horizontalArrangement = Arrangement.Start
        ) {
          Text(
            text = stringResource(R.string.search_content_title_town),
            style = MaterialTheme.typography.titleMedium
          )
          Spacer(modifier = Modifier.padding(4.dp))
          Text(
            text = city.name,
            style = MaterialTheme.typography.titleMedium.copy(
              fontStyle = FontStyle.Italic,
              fontWeight = FontWeight.W600
            )
          )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
          horizontalArrangement = Arrangement.Start
        ) {
          Text(
            text = stringResource(R.string.search_content_title_region),
            style = MaterialTheme.typography.titleSmall
          )
          Spacer(modifier = Modifier.padding(4.dp))
          Text(
            text = city.region, style = MaterialTheme.typography.titleSmall.copy(
              fontStyle = FontStyle.Italic,
              fontWeight = FontWeight.W600
            )
          )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
          horizontalArrangement = Arrangement.Start
        ) {
          Text(
            text = stringResource(R.string.search_content_title_country),
            style = MaterialTheme.typography.titleSmall
          )
          Spacer(modifier = Modifier.padding(4.dp))
          Text(
            text = city.country,
            style = MaterialTheme.typography.titleSmall.copy(
              fontStyle = FontStyle.Italic,
              fontWeight = FontWeight.W600
            )
          )
        }
      }
    }
  }
}