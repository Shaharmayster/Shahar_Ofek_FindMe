package com.example.findme_shahar_ofek

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/** UI state holder for current user's posts with edit/delete operations. */
class MyPostsViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = RepositoryProvider.authRepository()
    private val postRepository = RepositoryProvider.postRepository(application)

    val currentUserId: String? = authRepository.currentUserId()

    private val _emptyPosts = MutableLiveData<List<PostEntity>>(emptyList())
    val posts: LiveData<List<PostEntity>> =
        currentUserId?.let { postRepository.observePostsByUser(it).asLiveData() } ?: _emptyPosts

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private fun text(resId: Int, vararg args: Any): String =
        getApplication<Application>().getString(resId, *args)

    private fun reason(e: Exception): String =
        e.message?.takeIf { it.isNotBlank() } ?: text(R.string.error_unknown_reason)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                postRepository.syncPosts()
            } catch (e: Exception) {
                _error.value = text(R.string.error_offline_mode, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePost(post: PostEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                postRepository.deletePost(post)
            } catch (e: Exception) {
                _error.value = text(R.string.error_delete_post, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
