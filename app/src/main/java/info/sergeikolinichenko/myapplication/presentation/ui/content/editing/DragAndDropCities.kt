package info.sergeikolinichenko.myapplication.presentation.ui.content.editing

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import info.sergeikolinichenko.myapplication.presentation.stores.editing.EditingStore
import kotlinx.coroutines.Job

/** Created by Sergei Kolinichenko on 15.08.2024 at 15:51 (GMT+3) **/

// extension for getting item info
fun LazyListState.getVisibleItemInfoFor(absolut: Int): LazyListItemInfo? {
  return this.layoutInfo.visibleItemsInfo.getOrNull(absolut - this.layoutInfo.visibleItemsInfo.first().index)
}

// extension for getting offsetEnd
val LazyListItemInfo.offsetEnd: Int
  get() = this.offset + this.size

// extension for moving item in list
fun <T> MutableList<T>.move(from: Int, to: Int) {
  if (from == to) return
  val element = removeAt(from) ?: return
  this.add(to, element)
}

// remember state for drag and drop item
@Composable
fun rememberDragDropListState(
  lazyListState: LazyListState = rememberLazyListState(),
  cityItems: MutableList<EditingStore.State.CityItem>, // Add cityItems parameter
  onMove: (Int, Int, MutableList<EditingStore.State.CityItem>) -> Unit
): DragAndDropState {
  return remember(cityItems) { DragAndDropState(lazyListState = lazyListState, onMove = { from, to ->
    onMove(from, to, cityItems) // Pass cityItems to onMove
  }) }
}

// state for drag and drop item
class DragAndDropState(
  val lazyListState: LazyListState,
  private val onMove: (Int, Int) -> Unit
) {

  private var draggedDistance by mutableFloatStateOf(0f)
  private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
  var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)

  private val initialOffsets: Pair<Int, Int>?
    get() = initiallyDraggedElement?.let { Pair(it.offset, it.offsetEnd) }

  val elementDisplacement: Float?
    get() = currentIndexOfDraggedItem?.let {
      lazyListState.getVisibleItemInfoFor(absolut = it)
    }?.let { item ->
      (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
    }

  private val currentElement: LazyListItemInfo?
    get() = currentIndexOfDraggedItem?.let {
      lazyListState.getVisibleItemInfoFor(absolut = it)
    }

  private var overScrollJob by mutableStateOf<Job?>(null)

  fun onDragStart(
    offset: Offset,
    lazyColumnCoords: LayoutCoordinates,
    iconCoordinates: LayoutCoordinates
  ) {
    val iconWindowPos = iconCoordinates.localToWindow(offset)
    val lazyColumnWindowPos = lazyColumnCoords.positionInWindow()
    val y = iconWindowPos.y - lazyColumnWindowPos.y - lazyListState.firstVisibleItemScrollOffset

    lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
      y.toInt() in item.offset..(item.offsetEnd)
    }?.also {
      currentIndexOfDraggedItem = it.index
      initiallyDraggedElement = it
    }
  }

  fun onDragInterrupted() {
    draggedDistance = 0f
    currentIndexOfDraggedItem = null
    initiallyDraggedElement = null
    overScrollJob?.cancel()
  }

  fun onDrag(offset: Offset) {
    draggedDistance += offset.y
    initialOffsets?.let { (topOffset, bottomOffset) ->
      val startOffset = topOffset + draggedDistance
      val endOffset = bottomOffset + draggedDistance

      currentElement?.let { hovered ->
        lazyListState.layoutInfo.visibleItemsInfo.filterNot { item ->
          item.offsetEnd < startOffset || item.offset > endOffset || hovered.index == item.index
        }.firstOrNull { item ->
          val delta = startOffset - hovered.offset
          when {
            delta > 0 -> (endOffset > item.offsetEnd)
            else -> (startOffset < item.offset)
          }
        }?.also { item ->
          currentIndexOfDraggedItem?.let { current ->
            onMove.invoke(current, item.index)
          }
          currentIndexOfDraggedItem = item.index
        }
      }
    }
  }

  fun checkForOverScroll(): Float {
    return initiallyDraggedElement?.let {
      val startOffset = it.offset + draggedDistance
      val endOffset = it.offsetEnd + draggedDistance

      return@let when {
        draggedDistance > 0 ->

          (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }

        draggedDistance < 0 ->
          (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }

        else -> null
      }
    } ?: 0f
  }
}