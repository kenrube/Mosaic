package com.kenrube.mosaic.presentation.features.photo.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FilterItemDecoration(
    private val outerSpacing: Int,
    private val innerSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemCount = state.itemCount
        val position = parent.getChildViewHolder(view).adapterPosition

        setOffset(outRect, itemCount, position)
    }

    private fun setOffset(outRect: Rect, itemCount: Int, position: Int) {
        outRect.left = if (position == 0) outerSpacing else innerSpacing
        outRect.top = outerSpacing
        outRect.right = if (position == itemCount - 1) outerSpacing else innerSpacing
        outRect.bottom = outerSpacing
    }
}