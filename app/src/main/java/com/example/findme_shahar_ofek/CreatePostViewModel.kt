package com.example.findme_shahar_ofek

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/** UI state holder for creating or editing a post. */
class CreatePostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RepositoryProvider.postRepository(application)

    private val _editingPost = MutableLiveData<PostEntity?>(null)
    val editingPost: LiveData<PostEntity?> = _editingPost

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _savedPostId = MutableLiveData<String?>(null)
    val savedPostId: LiveData<String?> = _savedPostId

    private fun text(resId: Int, vararg args: Any): String =
        getApplication<Application>().getString(resId, *args)

    private fun reason(e: Exception): String =
        e.message?.takeIf { it.isNotBlank() } ?: text(R.string.error_unknown_reason)

    val categories: List<String> = listOf(
        text(R.string.category_lost),
        text(R.string.category_found)
    )

    fun loadPost(postId: String?) {
        if (postId.isNullOrBlank()) {
            _editingPost.value = null
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _editingPost.value = repository.getPostById(postId)
            } catch (e: Exception) {
                _error.value = text(R.string.error_load_post, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitPost(postId: String?, title: String, category: String, imageUri: Uri?) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            _error.value = text(R.string.error_text_required)
            return
        }
        if (trimmedTitle.length > MAX_POST_TEXT_LENGTH) {
            _error.value = text(R.string.error_post_text_too_long, MAX_POST_TEXT_LENGTH)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val saved = repository.createOrUpdatePost(
                    postId = postId,
                    title = trimmedTitle,
                    category = category,
                    imageUri = imageUri
                )
                _savedPostId.value = saved.id
            } catch (e: Exception) {
                _error.value = text(R.string.error_save_post, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSaveEvent() {
        _savedPostId.value = null
    }

    private companion object {
        const val MAX_POST_TEXT_LENGTH = 280
    }
}
