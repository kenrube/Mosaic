package com.kenrube.mosaic.presentation.features.photo_list.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhotoItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager
        if (layoutManager !is GridLayoutManager) {
            return super.getItemOffsets(outRect, view, parent, state)
        }

        val itemCount = state.itemCount
        val columnCount = layoutManager.spanCount
        val rowCount = (itemCount - 1) / columnCount + 1
        val position = parent.getChildViewHolder(view).adapterPosition

        setOffset(outRect, columnCount, rowCount, position)
    }

    private fun setOffset(outRect: Rect, columnCount: Int, rowCount: Int, position: Int) {
        outRect.left = spacing
        outRect.top = if (position / columnCount == 0) 0 else spacing
        outRect.right = spacing
        outRect.bottom = if (position / columnCount == rowCount - 1) 0 else spacing
    }
}