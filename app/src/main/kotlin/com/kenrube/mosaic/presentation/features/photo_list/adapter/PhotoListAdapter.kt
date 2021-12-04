package com.kenrube.mosaic.presentation.features.photo_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kenrube.mosaic.databinding.RecyclerViewActionItemBinding
import com.kenrube.mosaic.databinding.RecyclerViewPhotoItemBinding

class PhotoListAdapter(private val onClick: (View?, UiModel) -> Unit) :
    ListAdapter<UiModel, ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (ItemViewType.values()[viewType]) {
            ItemViewType.PHOTO -> {
                PhotoViewHolder(
                    RecyclerViewPhotoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClick
                )
            }
            ItemViewType.ACTION -> {
                ActionViewHolder(
                    RecyclerViewActionItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClick
                )
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is UiModel.PhotoUiModel -> ItemViewType.PHOTO.ordinal
            is UiModel.ActionUiModel -> ItemViewType.ACTION.ordinal
        }

    private object DiffCallback : DiffUtil.ItemCallback<UiModel>() {
        override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
            oldItem == newItem
    }
}
