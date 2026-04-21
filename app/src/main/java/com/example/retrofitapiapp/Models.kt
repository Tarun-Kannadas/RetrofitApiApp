package com.example.retrofitapiapp

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
data class LoginRequest(
    val email: String,
    val password: String
)
data class NoteRequest(
    val title: String,
    val content: String
)

data class AuthResponse(
    val message: String,
    val userId: String? = null
)

data class Note(
    @SerializedName("_id") val id: String,
    val title: String,
    val content: String,
    val userId: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class MessageResponse(val message: String)
