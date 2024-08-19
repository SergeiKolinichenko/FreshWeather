package info.sergeikolinichenko.myapplication.presentation.ui.content.editing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.presentation.screens.editing.component.EditingFavouritesComponent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore
import info.sergeikolinichenko.myapplication.utils.SYS_ICON_SIZE_24
import info.sergeikolinichenko.myapplication.utils.toIconId
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Created by Sergei Kolinichenko on 14.08.2024 at 18:00 (GMT+3) **/

@Composable
internal fun EditingContent(component: EditingFavouritesComponent) {

  AnimatedEditingContent(
    modifier = Modifier.fillMaxSize(),
    component = component,
  )

}

@Composable
internal fun EditingScreen(
  modifier: Modifier = Modifier,
  state: EditingFavouritesStore.State,
  onCloseClicked: () -> Unit,
  onDoneClicked: (cities: List<CityFs>) -> Unit,
  onSwipeRight: () -> Unit
) {

  var listCity: List<CityFs>? = null

  var swipeRight by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .pointerInput(Unit) {
        detectHorizontalDragGestures { change, dragAmount ->
          if (dragAmount < 0) {
            swipeRight = true
          }
          change.consume()
        }
      }
  ) {
    TopBar(
      modifier = Modifier,
      onCloseClicked = { onCloseClicked() },
      onDoneClicked = { listCity?.let { onDoneClicked(it) } }
    )
    MainScreen(
      modifier = Modifier.padding(16.dp),
      state = state,
      listCityFs = { cities ->  listCity = cities }
    )
  }
  if (swipeRight) onSwipeRight()
}

@Composable
private fun MainScreen(
  modifier: Modifier = Modifier,
  state: EditingFavouritesStore.State,
  listCityFs: (List<CityFs>) -> Unit,
) {

  when (state.cities) {
    EditingFavouritesStore.State.CitiesStatus.CitiesInitial -> {}
    EditingFavouritesStore.State.CitiesStatus.CitiesLoadingError -> {
      CitiesLoadingError()
    }

    is EditingFavouritesStore.State.CitiesStatus.CitiesLoaded -> {

      val cityItems = state.cityItems
      val cities = state.cities.cities.toMutableStateList()


      val dragDropListState = rememberDragDropListState(onMove = { from, to ->
        cities.move(from, to)
        listCityFs(cities)
      })

      val lazyColumnCoordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }

      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .onGloballyPositioned { coordinates ->
            lazyColumnCoordinates.value = coordinates
          }
        ,
        state = dragDropListState.lazyListState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        itemsIndexed(
          items = cities,
          key = { _, city -> city.id }
        ) { index, city ->

          CityItem(
            modifier = Modifier
              .graphicsLayer {
                translationY = dragDropListState.elementDisplacement.takeIf {
                  index == dragDropListState.currentIndexOfDraggedItem
                } ?: 0f
              },
            city = city,
            temp = cityItems.first { it.id == city.id }.temp,
            icon = cityItems.first { it.id == city.id }.icon,
            onClickIconDelete = { id ->
              cities.remove(cities.find { it.id == id })
              listCityFs(cities)
            },
            dragDropListState = dragDropListState,
            lazyColumnCoordinates = lazyColumnCoordinates
          )
        }
      }
    }
  }
}

@Composable
private fun CityItem(
  modifier: Modifier = Modifier,
  city: CityFs,
  temp: String,
  icon: String,
  onClickIconDelete: (id: Int) -> Unit,
  dragDropListState: DragAndDropState,
  lazyColumnCoordinates: MutableState<LayoutCoordinates?>
) {

  val scope = rememberCoroutineScope()
  var overScrollJob by remember { mutableStateOf<Job?>(null) }
  val alpha = remember { mutableFloatStateOf(1f) }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp)
      .alpha(alpha.floatValue),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier
        .size(36.dp)
        .padding(end = 8.dp)
        .clickable { onClickIconDelete(city.id) },
      imageVector = ImageVector.vectorResource(id = R.drawable.remove_item),
      tint = Color.Unspecified,
      contentDescription = "Item deletion icon"
    )
    Card(
      modifier = Modifier
        .padding(end = 8.dp)
        .weight(1f),
      shape = MaterialTheme.shapes.medium,
      colors = MaterialTheme.colorScheme.run {
        CardDefaults.cardColors(
          containerColor = surface,
          contentColor = onBackground
        )
      }
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier,
          horizontalAlignment = Alignment.Start,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = city.name,
            textAlign = TextAlign.Start,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = temp,
            textAlign = TextAlign.Start,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
        Icon(
          modifier = Modifier.size(44.dp),
          painter = painterResource(id = icon.toIconId()),
          tint = Color.Unspecified,
          contentDescription = "Icon of weather state"
        )
      }
    }

    var iconCoordinates: LayoutCoordinates? = null

    Icon(
      modifier = Modifier
        .size(SYS_ICON_SIZE_24.dp)
        .onGloballyPositioned { coordinates ->
          iconCoordinates = coordinates // Store the coordinates
        }
        .pointerInput(Unit) {
          detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
              alpha.floatValue = 0.7f
              lazyColumnCoordinates.value?.let { lazyColumnCoords ->
                iconCoordinates?.let { iconCoords ->
                  dragDropListState.onDragStart(
                    offset = offset,
                    lazyColumnCoords = lazyColumnCoords,
                    iconCoordinates = iconCoords
                  )
                }
              }
            },
            onDragEnd = {
              alpha.floatValue = 1f
              dragDropListState.onDragInterrupted()
            },
            onDragCancel = {
              alpha.floatValue = 1f
              dragDropListState.onDragInterrupted()
            },
            onDrag = { change, offset ->
              change.consume()
              dragDropListState.onDrag(offset = offset)
              if (overScrollJob?.isActive != true) return@detectDragGesturesAfterLongPress

              dragDropListState
                .checkForOverScroll()
                .takeIf {
                  it != 0f
                }
                ?.let {
                  overScrollJob = scope.launch {
                    dragDropListState.lazyListState.scrollBy(it)
                  }
                } ?: run {
                overScrollJob?.cancel()
              }
            }
          )
        }
      ,
      imageVector = Icons.Default.Menu,
      contentDescription = "Menu icon editing order items"
    )
  }
}


@Composable
private fun CitiesLoadingError(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = stringResource(R.string.failed_to_load_the_list_of_favourite_cities),
      textAlign = TextAlign.Start,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.W500,
      fontSize = 22.sp,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
internal fun TopBar(
  modifier: Modifier = Modifier,
  onCloseClicked: () -> Unit,
  onDoneClicked: () -> Unit
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 20.dp),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier
        .padding(end = 12.dp)
        .size(SYS_ICON_SIZE_24.dp)
        .clickable { onCloseClicked() },
      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
      contentDescription = "Close editing favourites screen"
    )
    Row(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = stringResource(R.string.title_few_contents_text_favourite),
        textAlign = TextAlign.Start,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        modifier = Modifier
          .clickable { onDoneClicked() },
        text = stringResource(id = R.string.many_place_title_button_done),
        textAlign = TextAlign.Start,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}