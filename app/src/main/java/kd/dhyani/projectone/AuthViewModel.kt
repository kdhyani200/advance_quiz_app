package kd.dhyani.projectone

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel(context: Context) : ViewModel() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()  // Check if the user is logged in when the app starts
    }

    fun checkAuthStatus() {
        // Check if the user is authenticated and persist the state in SharedPreferences
        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)
        if (auth.currentUser == null || !isLoggedIn) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authencitated
        }
    }

    fun signup(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String,
        age: String,
        sex: String
    ) {
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || age.isEmpty() || sex.isEmpty()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val existingMethods = task.result?.signInMethods
                    if (existingMethods.isNullOrEmpty()) {
                        createAccount(email, password, firstName, lastName, username, age, sex)
                    } else {
                        _authState.value = AuthState.Error("An account with this email already exists. Please login.")
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    private fun createAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String,
        age: String,
        sex: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        val userMap = mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "username" to username,
                            "email" to email,
                            "age" to age,
                            "sex" to sex
                        )
                        database.reference.child("users").child(userId).setValue(userMap)
                    }

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                // Save login state in SharedPreferences
                                sharedPrefs.edit().putBoolean("isLoggedIn", true).apply()
                                _authState.value = AuthState.Authencitated
                            } else {
                                _authState.value = AuthState.Error("Failed to send verification email: ${emailTask.exception?.message}")
                            }
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        sharedPrefs.edit().putBoolean("isLoggedIn", true).apply()
                        _authState.value = AuthState.Authencitated
                    } else {
                        _authState.value = AuthState.Error("Please verify your email before logging in.")
                    }
                } else {
                    // Log the exception message for debugging
                    println("Login Error: ${task.exception?.message}")

                    val errorMessage = when {
                        task.exception?.message?.contains("password is invalid", true) == true -> "Wrong password"
                        task.exception?.message?.contains("no user record", true) == true -> "Email not registered"
                        else -> "Something went wrong"
                    }
                    _authState.value = AuthState.Error(errorMessage)

                }
            }
    }

    fun signout() {
        auth.signOut()
        sharedPrefs.edit().putBoolean("isLoggedIn", false).apply()  // Clear login status from SharedPreferences
        _authState.value = AuthState.Unauthenticated  // Trigger state change to unauthenticated
    }

    fun resetAuthState() {
        _authState.value = null
    }
}

sealed class AuthState {
    object Authencitated : AuthState()
    object Unauthenticated : AuthState() // This state represents unauthenticated users
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
