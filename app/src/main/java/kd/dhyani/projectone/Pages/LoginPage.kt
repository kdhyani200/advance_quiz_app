package kd.dhyani.projectone.Pages

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kd.dhyani.projectone.AuthState
import kd.dhyani.projectone.AuthViewModel
import kd.dhyani.projectone.Components.ForgotPasswordDialog

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Authencitated -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
                authViewModel.checkAuthStatus() // Reset state to avoid unnecessary navigation
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                authViewModel.resetAuthState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { keyboardController?.hide() }
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email")},
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text(text = "Email")
            }
        )

        OutlinedTextField(
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password")},
            value = password,
            onValueChange = { newPassword -> password = newPassword },
            label = { Text(text = "Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.login(email.trim(), password) },
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("signup") }) {
            Text(text = "Don't have an account? Signup")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { showForgotPasswordDialog = true }) {
            Text(text = "Forgot Password?")
        }
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotPasswordDialog = false },
            onSendEmail = { enteredEmail ->
                authViewModel.auth.sendPasswordResetEmail(enteredEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Reset email sent to $enteredEmail",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to send reset email: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        showForgotPasswordDialog = false
                    }
            }
        )
    }
}
