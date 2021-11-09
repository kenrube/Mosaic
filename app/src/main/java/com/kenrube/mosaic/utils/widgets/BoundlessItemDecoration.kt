package com.kenrube.mosaic.utils.widgets

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kenrube.mosaic.utils.dpToPx
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BoundlessItemDecoration @Inject constructor(
    @ApplicationContext context: Context
) : RecyclerView.ItemDecoration() {

    private val m2 = context.dpToPx(2)
    private val offsets = arrayOf(0, m2, m2 * 2)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).adapterPosition
        setOffset(outRect, position, state.itemCount)
    }

    private fun setOffset(outRect: Rect, position: Int, itemCount: Int) {
        val columnCount = 3
        val rowCount = itemCount / columnCount

        outRect.left = offsets[position % columnCount]
        outRect.top = if (position / columnCount == 0) 0 else offsets[1]
        outRect.right = offsets[columnCount - 1 - position % columnCount]
        outRect.bottom = if (position / columnCount == rowCount) 0 else offsets[2]
    }
}