package com.example.findme_shahar_ofek

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/** UI state holder for REST API posts screen with cache fallback. */
class ApiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RepositoryProvider.apiRepository(application)

    val posts: LiveData<List<ApiPostEntity>> = repository.observePosts().asLiveData()

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
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshPosts()
            } catch (e: Exception) {
                _error.value = text(R.string.error_network_cached, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
