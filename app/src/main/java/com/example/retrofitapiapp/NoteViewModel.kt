package com.example.retrofitapiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel: ViewModel() {
    private val _notes = MutableLiveData<List<Note>>(emptyList())
    val notes: LiveData<List<Note>> = _notes

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    fun registerUser(name: String, email: String, pass: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.register(RegisterRequest(name, email, pass))
                if (response.isSuccessful) {
                    _registerSuccess.value = true
                    println("Registration success!")
                } else {
                    _registerSuccess.value = false
                    println("Registration failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _registerSuccess.value = false
                println("Network Error: ${e.message}")
            }
        }
    }

    fun loginUser(email: String, pass: String){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, pass))
                if(response.isSuccessful){
                    val authData = response.body()

                    RetrofitClient.currentUserId = authData?.userId
                    println("Login success! User ID: ${RetrofitClient.currentUserId}")

                    _loginSuccess.value = true

                    fetchNotes()
                }
                else{
                    _loginSuccess.value = false
                    println("Login failed: ${response.errorBody()?.string()}")
                }
            }
            catch (e: Exception)
            {
                _loginSuccess.value = false
                println("Network Error: ${e.message}")
            }
        }
    }

    fun fetchNotes() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getNotes()
                if(response.isSuccessful){
                    val noteList = response.body() ?: emptyList()
                    _notes.value = noteList
                    println("Successfully fetched ${noteList.size} notes!")
                }
                else{
                    println("Error fetching notes: ${response.errorBody()?.string()}")
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            try {
                val newNote = NoteRequest(title, content)
                val response = RetrofitClient.apiService.createNote(newNote)

                if (response.isSuccessful) {
                    fetchNotes()
                } else {
                    println("Failed to add note: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateNote(id: String, newTitle: String, newContent: String) {
        viewModelScope.launch {
            try {
                val updatedNote = NoteRequest(newTitle, newContent)
                val response = RetrofitClient.apiService.updateNote(id, updatedNote)

                if (response.isSuccessful) {
                    fetchNotes()
                } else {
                    println("Failed to update note: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteNotes(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteNote(id)

                if(response.isSuccessful){
                    fetchNotes()
                }
                else
                {
                    println("Failed to delete note: ${response.errorBody()?.toString()}")
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetRegisterState() {
        _registerSuccess.value = false
    }

    fun logoutUser() {
        RetrofitClient.currentUserId = null

        _notes.value = emptyList()

        _loginSuccess.value = false
    }
}