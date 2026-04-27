package com.example.findme_shahar_ofek

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/** UI state holder for login flow. */
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = RepositoryProvider.authRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private fun text(resId: Int, vararg args: Any): String =
        getApplication<Application>().getString(resId, *args)

    private fun reason(e: Exception): String =
        e.message?.takeIf { it.isNotBlank() } ?: text(R.string.error_unknown_reason)

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = text(R.string.error_empty_credentials)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                authRepository.login(email.trim(), password)
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _error.value = text(R.string.error_auth_failed, reason(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLoggedInEvent() {
        _isLoggedIn.value = false
    }
}
