package com.example.retrofitapiapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("/notes")
    suspend fun getNotes(): Response<List<Note>>

    @POST("/notes")
    suspend fun createNote(@Body request: NoteRequest): Response<Note>

    @PUT("/notes/{id}")
    suspend fun updateNote(@Path("id") noteId: String, @Body request: NoteRequest): Response<Note>

    @DELETE("/notes/{id}")
    suspend fun deleteNote(@Path("id") noteId: String): Response<MessageResponse>
}