package com.daffa0050.assesment1.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private val registeredUsers = mutableStateListOf<User>()

    var currentUser: User? = null
        private set

    fun register(username: String, password: String): Boolean {
        if (registeredUsers.any { it.username == username }) return false
        registeredUsers.add(User(username, password))
        return true
    }

    fun login(username: String, password: String): Boolean {
        val user = registeredUsers.find { it.username == username && it.password == password }
        return if (user != null) {
            currentUser = user
            true
        } else false
    }
}
