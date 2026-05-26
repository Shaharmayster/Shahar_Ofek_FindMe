package com.example.findme_shahar_ofek

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/** UI state holder for profile display, update, and logout actions. */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = RepositoryProvider.authRepository()
    private val profileRepository = RepositoryProvider.profileRepository(application)

    val currentUserId: String? = authRepository.currentUserId()

    private val _emptyProfile = MutableLiveData<UserProfileEntity?>(null)
    val profile: LiveData<UserProfileEntity?> =
        currentUserId?.let { profileRepository.observeProfile(it).asLiveData() } ?: _emptyProfile

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private fun text(resId: Int, vararg args: Any): String =
        getApplication<Application>().getString(resId, *args)

    private fun reason(e: Exception): String =
        e.message?.takeIf { it.isNotBlank() } ?: text(R.string.error_unknown_reason)

    init {
        refresh()
    }

    fun refresh() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                profileRepository.syncProfile(userId)
            } catch (e: Exception) {
                _error.value = text(R.string.error_load_profile, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun save(displayName: String, imageUri: Uri?) {
        val userId = currentUserId ?: return
        val trimmedName = displayName.trim()
        if (trimmedName.isBlank()) {
            _error.value = text(R.string.error_display_name_required)
            return
        }
        if (trimmedName.length > MAX_DISPLAY_NAME_LENGTH) {
            _error.value = text(R.string.error_display_name_too_long, MAX_DISPLAY_NAME_LENGTH)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false
            try {
                profileRepository.saveProfile(userId, trimmedName, imageUri)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = text(R.string.error_save_profile, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSaveSuccess() {
        _saveSuccess.value = false
    }

    fun logout() {
        authRepository.logout()
    }

    private companion object {
        const val MAX_DISPLAY_NAME_LENGTH = 80
    }
}
