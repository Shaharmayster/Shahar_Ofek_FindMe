package com.example.findme_shahar_ofek

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/** UI state holder for registration flow. */
class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = RepositoryProvider.authRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _isRegistered = MutableLiveData(false)
    val isRegistered: LiveData<Boolean> = _isRegistered

    private fun text(resId: Int, vararg args: Any): String =
        getApplication<Application>().getString(resId, *args)

    private fun reason(e: Exception): String =
        e.message?.takeIf { it.isNotBlank() } ?: text(R.string.error_unknown_reason)

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = text(R.string.error_empty_credentials)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                authRepository.register(email.trim(), password)
                _isRegistered.value = true
            } catch (e: Exception) {
                _error.value = text(R.string.error_auth_failed, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearRegisteredEvent() {
        _isRegistered.value = false
    }
}
