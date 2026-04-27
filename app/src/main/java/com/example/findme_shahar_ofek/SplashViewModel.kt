package com.example.findme_shahar_ofek

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/** Decides start route based on persisted auth session. */
class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = RepositoryProvider.authRepository()

    fun isUserLoggedIn(): Boolean = authRepository.isLoggedIn()
}
