package com.kenrube.mosaic.presentation.features.photo_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenrube.mosaic.databinding.RecyclerViewPhotoItemBinding
import com.kenrube.mosaic.presentation.model.UiPhoto
import com.kenrube.mosaic.utils.setImage

class PhotoListAdapter : ListAdapter<UiPhoto, PhotoListAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: UiPhoto = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<UiPhoto>() {
            override fun areItemsTheSame(oldItem: UiPhoto, newItem: UiPhoto): Boolean = oldItem.uri == newItem.uri

            override fun areContentsTheSame(oldItem: UiPhoto, newItem: UiPhoto): Boolean = oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: RecyclerViewPhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UiPhoto) {
            binding.photo.setImage(item.uri)
        }
    }
}
