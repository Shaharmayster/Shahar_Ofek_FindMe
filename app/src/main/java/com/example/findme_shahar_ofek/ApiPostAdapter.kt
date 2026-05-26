package com.example.findme_shahar_ofek

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/** RecyclerView adapter for cached REST API posts. */
class ApiPostAdapter : ListAdapter<ApiPostEntity, ApiPostAdapter.ApiPostViewHolder>(ApiPostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.api_post_item, parent, false)
        return ApiPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApiPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ApiPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.api_post_image)
        private val titleView: TextView = itemView.findViewById(R.id.api_post_title)
        private val bodyView: TextView = itemView.findViewById(R.id.api_post_body)

        fun bind(post: ApiPostEntity) {
            imageView.loadCachedImage(
                localImagePath = null,
                remoteImageUrl = post.imageUrl,
                placeholderResId = R.drawable.img_create_post_cat
            )
            titleView.text = post.title
            bodyView.text = post.body
        }
    }

    class ApiPostDiffCallback : DiffUtil.ItemCallback<ApiPostEntity>() {
        override fun areItemsTheSame(oldItem: ApiPostEntity, newItem: ApiPostEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ApiPostEntity, newItem: ApiPostEntity): Boolean =
            oldItem == newItem
    }
}
