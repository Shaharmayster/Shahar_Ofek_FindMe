package com.example.findme_shahar_ofek

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/** Handles authentication operations through FirebaseAuth. */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentUserId(): String? = auth.currentUser?.uid

    fun currentUserEmail(): String? = auth.currentUser?.email

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    fun logout() {
        auth.signOut()
    }
}
