package com.kenrube.mosaic.presentation.features.photo_list.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.kenrube.mosaic.databinding.RecyclerViewActionItemBinding
import com.kenrube.mosaic.databinding.RecyclerViewPhotoItemBinding
import com.kenrube.mosaic.utils.GlideApp

sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(uiModel: UiModel)
}

class PhotoViewHolder(
    private val binding: RecyclerViewPhotoItemBinding,
    private val onClick: (View?, UiModel) -> Unit
) : ViewHolder(binding.root) {
    override fun bind(uiModel: UiModel) {
        uiModel as UiModel.PhotoUiModel
        GlideApp.with(binding.root.context)
            .load(uiModel.uri)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.photo)
        binding.photo.transitionName = uiModel.uri.toString()
        binding.root.setOnClickListener { onClick(binding.photo, uiModel) }
    }
}

class ActionViewHolder(
    private val binding: RecyclerViewActionItemBinding,
    private val onClick: (View?, UiModel) -> Unit
) : ViewHolder(binding.root) {
    override fun bind(uiModel: UiModel) {
        uiModel as UiModel.ActionUiModel
        binding.action.setText(uiModel.title)
        binding.action.setCompoundDrawablesWithIntrinsicBounds(0, uiModel.icon, 0, 0)
        binding.root.setOnClickListener { onClick(null, uiModel) }
    }
}