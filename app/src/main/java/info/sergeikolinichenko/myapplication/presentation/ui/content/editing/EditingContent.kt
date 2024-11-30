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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import info.sergeikolinichenko.myapplication.presentation.components.editing.EditingComponent
import info.sergeikolinichenko.myapplication.presentation.stores.editing.EditingStore
import info.sergeikolinichenko.myapplication.presentation.stores.editing.EditingStore.State.CityItem
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.SYS_ICON_SIZE_24
import info.sergeikolinichenko.myapplication.utils.toIconId
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Created by Sergei Kolinichenko on 14.08.2024 at 18:00 (GMT+3) **/

@Composable
internal fun EditingContent(component: EditingComponent) {

  val state by component.model.collectAsState()

  Box(
    modifier = Modifier
      .background(MaterialTheme.colorScheme.background)
      .fillMaxSize()
  ) {
    EditingScreen(
      state = state,
      onCloseClicked = { component.onBackClicked() },
      onSwipeRight = { component.onBackClicked() },
      onDoneClicked = { component.onDoneClicked() },
      changedListCities = { component.listOfCitiesChanged(it) },
      removeItemFromListOfCities = { component.removeItemFromListOfCities(it) }
    )
  }
}

@Composable
internal fun EditingScreen(
  modifier: Modifier = Modifier,
  state: EditingStore.State,
  changedListCities: (cities: List<CityItem>) -> Unit,
  removeItemFromListOfCities: (id: Int) -> Unit,
  onCloseClicked: () -> Unit,
  onDoneClicked: () -> Unit,
  onSwipeRight: () -> Unit
) {

  var swipeRight by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(top = 32.dp, bottom = 16.dp)
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
      onDoneClicked = { onDoneClicked() }
    )
    MainScreen(
      modifier = Modifier.padding(16.dp),
      state = state,
      changedListCities = { changedListCities(it) },
      removeItemFromListOfCities = { id -> removeItemFromListOfCities(id) }
    )
  }
  if (swipeRight) onSwipeRight()
}

@Composable
private fun MainScreen(
  modifier: Modifier = Modifier,
  state: EditingStore.State,
  changedListCities: (List<CityItem>) -> Unit,
  removeItemFromListOfCities: (id: Int) -> Unit
) {

  val cityItems = state.cityItems.toMutableList()

  val dragDropListState = rememberDragDropListState(
    cityItems = cityItems, // Pass cityItems
    onMove = { from, to, currentCityItems ->
    if (currentCityItems.isNotEmpty() && from in 0 until currentCityItems.size) {
      currentCityItems.move(from, to)
      changedListCities(currentCityItems)
    }
  })

  val lazyColumnCoordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .onGloballyPositioned { coordinates ->
        lazyColumnCoordinates.value = coordinates
      },
    state = dragDropListState.lazyListState,
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    itemsIndexed(
      items = cityItems,
      key = { _, city -> city.id }
    ) { index, cityItem ->

      CityItem(
        modifier = Modifier
          .graphicsLayer {
            translationY = dragDropListState.elementDisplacement.takeIf {
              index == dragDropListState.currentIndexOfDraggedItem
            } ?: 0f
          },
        item = cityItem,
        onClickIconDelete = { id -> removeItemFromListOfCities(id)},
        dragDropListState = dragDropListState,
        lazyColumnCoordinates = lazyColumnCoordinates
      )
    }
  }
}

@Composable
private fun CityItem(
  modifier: Modifier = Modifier,
  item: CityItem,
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
        .clickable { onClickIconDelete(item.id) },
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
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.Start,
          verticalArrangement = Arrangement.Center
        ) {
          ResponsiveText(
            text = item.name,
            textAlign = TextAlign.Start,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            targetTextSizeHeight = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
          )
          ResponsiveText(
            text = item.temp,
            textAlign = TextAlign.Start,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            targetTextSizeHeight = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
          )
        }
        Icon(
          modifier = Modifier.size(44.dp),
          painter = painterResource(id = item.icon.toIconId()),
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
                .takeIf { it != 0f }
                ?.let {
                  overScrollJob = scope.launch {
                    dragDropListState.lazyListState.scrollBy(it)
                  }
                } ?: run { overScrollJob?.cancel() }
            }
          )
        },
      imageVector = Icons.Default.Menu,
      tint = MaterialTheme.colorScheme.surfaceTint,
      contentDescription = "Menu icon editing order items"
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
      .padding(start = 16.dp, end = 16.dp, top = 16.dp),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier
        .padding(end = 12.dp)
        .size(SYS_ICON_SIZE_24.dp)
        .clickable { onCloseClicked() },
      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
      tint = MaterialTheme.colorScheme.surfaceTint,
      contentDescription = "Close editing favourites screen"
    )
    Row(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      ResponsiveText(
        text = stringResource(R.string.title_few_contents_text_favourite),
        textAlign = TextAlign.Start,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        targetTextSizeHeight = 22.sp,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1
      )

      ResponsiveText(
        modifier = Modifier
          .clickable { onDoneClicked() },
        text = stringResource(id = R.string.many_place_title_button_done),
        textAlign = TextAlign.Start,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        targetTextSizeHeight = 22.sp,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1
      )
    }
  }
}