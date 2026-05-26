package com.example.findme_shahar_ofek

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/** RecyclerView adapter for app posts with optional owner actions. */
class PostAdapter(
    private val onEditClick: ((PostEntity) -> Unit)? = null,
    private val onDeleteClick: ((PostEntity) -> Unit)? = null,
    private val currentUserId: String? = null
) : ListAdapter<PostEntity, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), onEditClick, onDeleteClick, currentUserId)
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.post_title)
        private val categoryView: TextView = itemView.findViewById(R.id.post_category)
        private val imageView: ImageView = itemView.findViewById(R.id.post_image)
        private val editButton: Button = itemView.findViewById(R.id.edit_button)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_button)

        fun bind(
            post: PostEntity,
            onEditClick: ((PostEntity) -> Unit)?,
            onDeleteClick: ((PostEntity) -> Unit)?,
            currentUserId: String?
        ) {
            titleView.text = post.title
            categoryView.text = post.category.ifBlank { PostEntity.DEFAULT_CATEGORY }

            val hasLocalImage = ImageCache.existingFileOrNull(post.localImagePath) != null
            val hasRemoteImage = !post.imageUrl.isNullOrBlank()
            if (!hasLocalImage && !hasRemoteImage) {
                imageView.isVisible = false
            } else {
                imageView.isVisible = true
                imageView.loadCachedImage(
                    localImagePath = post.localImagePath,
                    remoteImageUrl = post.imageUrl,
                    placeholderResId = R.drawable.bg_media_placeholder
                )
            }

            val isOwner = !currentUserId.isNullOrBlank() && post.userId == currentUserId
            editButton.isVisible = isOwner && onEditClick != null
            deleteButton.isVisible = isOwner && onDeleteClick != null

            editButton.setOnClickListener { onEditClick?.invoke(post) }
            deleteButton.setOnClickListener { onDeleteClick?.invoke(post) }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<PostEntity>() {
        override fun areItemsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean =
            oldItem == newItem
    }
}
