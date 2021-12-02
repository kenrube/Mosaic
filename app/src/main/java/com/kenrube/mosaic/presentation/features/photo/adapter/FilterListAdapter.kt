package com.kenrube.mosaic.presentation.features.photo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenrube.mosaic.databinding.RecyclerViewFilterItemBinding

class FilterListAdapter(private val onClick: (UiFilter, Boolean) -> Unit) :
    ListAdapter<UiFilter, FilterListAdapter.FilterViewHolder>(DiffCallback) {

    private var selectedItemIndex = -1
        set(value) {
            notifyItemChanged(field)
            notifyItemChanged(value)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder =
        FilterViewHolder(
            RecyclerViewFilterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick
        )

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(position, getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<UiFilter>() {
        override fun areItemsTheSame(oldItem: UiFilter, newItem: UiFilter): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UiFilter, newItem: UiFilter): Boolean =
            oldItem == newItem
    }

    inner class FilterViewHolder(
        private val binding: RecyclerViewFilterItemBinding,
        private val onClick: (UiFilter, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, uiFilter: UiFilter) {
            binding.image.setImageResource(uiFilter.imageRes)
            binding.title.setText(uiFilter.titleRes)
            binding.selected.isVisible = position == selectedItemIndex
            binding.root.setOnClickListener {
                onClick(uiFilter, position != selectedItemIndex)
                selectedItemIndex = position
            }
        }
    }
}
