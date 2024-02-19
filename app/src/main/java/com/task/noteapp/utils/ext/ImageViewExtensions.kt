package com.task.noteapp.utils.ext

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation

@BindingAdapter(
    "app:image_url",
    "app:image_placeholder",
    "app:image_error",
    "app:image_corner_radius",
    requireAll = false
)
fun loadImage(
    imageView: ImageView,
    url: String?,
    placeholder: Drawable?,
    error: Drawable?,
    radius: Float?
) {
    if (url == null) {
        imageView.setImageDrawable(placeholder)
        return
    }

    imageView.load(url) {
        placeholder(placeholder)
        error(error)
        radius?.let { transformations(RoundedCornersTransformation(it)) }
        crossfade(true)
    }
}
