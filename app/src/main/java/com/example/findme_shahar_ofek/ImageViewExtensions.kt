package com.example.findme_shahar_ofek

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun ImageView.loadCachedImage(
    localImagePath: String?,
    remoteImageUrl: String?,
    placeholderResId: Int
) {
    val imageSource = ImageCache.existingFileOrNull(localImagePath) ?: remoteImageUrl

    if (imageSource == null || imageSource.toString().isBlank()) {
        Glide.with(context).clear(this)
        setImageResource(placeholderResId)
        return
    }

    Glide.with(context)
        .load(imageSource)
        .placeholder(placeholderResId)
        .error(placeholderResId)
        .transition(DrawableTransitionOptions.withCrossFade())
        .centerCrop()
        .into(this)
}
