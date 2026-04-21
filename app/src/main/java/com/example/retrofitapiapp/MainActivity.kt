package com.example.retrofitapiapp

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.retrofitapiapp.ui.theme.RetrofitApiAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetrofitApiAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val isLoggedIn by viewModel.loginSuccess.observeAsState(false)
                    var showRegisterScreen by remember { mutableStateOf(false) }

                    if (isLoggedIn) {
                        NoteScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else if (showRegisterScreen) {
                        RegisterScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding),
                            onNavigateToLogin = {
                                viewModel.resetRegisterState()
                                showRegisterScreen = false
                            }
                        )
                    } else {
                        LoginScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding),
                            onNavigateToRegister = { showRegisterScreen = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: NoteViewModel, modifier: Modifier = Modifier, onNavigateToLogin: () -> Unit) {
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val registerSuccess by viewModel.registerSuccess.observeAsState(false)

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            Toast.makeText(context, "Account Created! Please Log In.", Toast.LENGTH_SHORT).show()
            onNavigateToLogin()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = {
                nameInput = it
                nameError = null
            },
            label = { Text("Name") },
            isError = nameError != null,
            supportingText = {if(nameError != null) Text(nameError!!)},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = {
                emailInput = it
                emailError = null
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = {if(emailError != null) Text(emailError!!)},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = {
                passwordInput = it
                passwordError = null
            },
            label = { Text("Password") },
            isError = passwordError != null,
            supportingText = {if(passwordError != null) Text(passwordError!!)},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                nameError = null
                emailError = null
                passwordError = null

                var isValid = true

                if(nameInput.trim().isEmpty()){
                    nameError = "Name cannot be empty"
                    isValid = false
                }

                if (emailInput.trim().isEmpty()) {
                    emailError = "Email cannot be empty"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    emailError = "Please enter a valid email address"
                    isValid = false
                }

                if (passwordInput.isEmpty()) {
                    passwordError = "Password cannot be empty"
                    isValid = false
                } else if (passwordInput.length < 6) {
                    passwordError = "Password must be at least 6 characters"
                    isValid = false
                }

                if (isValid) {
                    viewModel.registerUser(nameInput, emailInput, passwordInput)
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Log In")
        }
    }
}

@Composable
fun LoginScreen(viewModel: NoteViewModel, modifier: Modifier = Modifier, onNavigateToRegister: () -> Unit) {
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = {
                emailInput = it
                emailError = null
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = {if(emailError != null) Text(emailError!!)},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = {
                passwordInput = it
                passwordError = null
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = {if(passwordError != null) Text(passwordError!!)},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                emailError = null
                passwordError = null
                var isValid = true

                if (emailInput.trim().isEmpty()) {
                    emailError = "Email cannot be empty"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    emailError = "Please enter a valid email address"
                    isValid = false
                }

                if (passwordInput.isEmpty()) {
                    passwordError = "Password cannot be empty"
                    isValid = false
                }

                if (isValid) {
                    viewModel.loginUser(emailInput, passwordInput)
                    Toast.makeText(context, "Login Successfull!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Sign Up")
        }
    }
}

@Composable
fun NoteScreen(viewModel: NoteViewModel, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val notes by viewModel.notes.observeAsState(emptyList())

    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My Notes", style = MaterialTheme.typography.headlineMedium)

            Button(
                onClick = {
                    viewModel.logoutUser()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = titleInput,
            onValueChange = { newText -> titleInput = newText },
            label = { Text("Note Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contentInput,
            onValueChange = { newText -> contentInput = newText },
            label = { Text("Note Content") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (titleInput.isNotBlank() && contentInput.isNotBlank()) {
                    viewModel.addNote(titleInput, contentInput) // Updated method call

                    titleInput = ""
                    contentInput = ""

                    Toast.makeText(context, "Note Created Successfully!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Note")
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider() // Replaced deprecated Divider()
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(notes) { note ->
                var showEditDialog by remember { mutableStateOf(false) }
                var editTitle by remember { mutableStateOf(note.title) }
                var editContent by remember { mutableStateOf(note.content) }

                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                        }

                        // Grouped buttons so they don't overlap
                        Row {
                            Button(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Edit")
                            }
                            Button(
                                onClick = {
                                    viewModel.deleteNotes(note.id) // Updated method call
                                    Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }

                if (showEditDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showEditDialog = false // Fixed bug: it should set to false to close
                        },
                        title = { Text("Edit Note") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = editTitle,
                                    onValueChange = { editTitle = it },
                                    label = { Text("Title") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = editContent,
                                    onValueChange = { editContent = it },
                                    label = { Text("Content") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    if (editTitle.isNotBlank() && editContent.isNotBlank()) {
                                        viewModel.updateNote(note.id, editTitle, editContent)
                                        showEditDialog = false
                                        Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Text("Update")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    // Revert changes if canceled
                                    editTitle = note.title
                                    editContent = note.content
                                    showEditDialog = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}