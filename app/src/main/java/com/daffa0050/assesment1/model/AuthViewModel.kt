package com.daffa0050.assesment1.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private val registeredUsers = mutableStateListOf<User>()

    private var currentUser: User? = null

    fun register(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) return false
        if (registeredUsers.any { it.email == email }) return false
        registeredUsers.add(User(email, password))
        return true
    }

    fun login(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) return false
        val user = registeredUsers.find { it.email == email && it.password == password }
        return if (user != null) {
            currentUser = user
            true
        } else false
    }

}