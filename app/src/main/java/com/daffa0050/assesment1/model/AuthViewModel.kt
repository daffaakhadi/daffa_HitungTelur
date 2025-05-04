package com.daffa0050.assesment1.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private val registeredUsers = mutableStateListOf<User>()

    private var currentUser: User? = null

    fun register(username: String, password: String): Boolean {
        if (username.isBlank() || password.isBlank()) return false
        if (registeredUsers.any { it.username == username }) return false
        registeredUsers.add(User(username, password))
        return true
    }

    fun login(username: String, password: String): Boolean {
        if (username.isBlank() || password.isBlank()) return false
        val user = registeredUsers.find { it.username == username && it.password == password }
        return if (user != null) {
            currentUser = user
            true
        } else false
    }
}
