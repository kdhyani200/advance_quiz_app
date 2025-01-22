package kd.dhyani.projectone.Pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kd.dhyani.projectone.AuthState
import kd.dhyani.projectone.AuthViewModel
import kd.dhyani.projectone.R

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val userId = auth.currentUser?.uid

    // State variables for user data
    var firstName by remember { mutableStateOf("Loading...") }
    var lastName by remember { mutableStateOf("Loading...") }
    var username by remember { mutableStateOf("Loading...") }
    var userSex by remember { mutableStateOf("Loading...") }
    var userAge by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }

    // Fetch user data
    LaunchedEffect(userId) {
        if (userId == null) {
            // Navigate back to login if the userId is null
            navController.navigate("login")
        } else {
            val userRef = database.getReference("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        Log.d("Profile", "Snapshot: ${snapshot.value}")
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            firstName = user.firstName.ifBlank { "Not Provided" }
                            lastName = user.lastName.ifBlank { "Not Provided" }
                            username = user.username.ifBlank { "Not Provided" }
                            userSex = user.sex.ifBlank { "Not Provided" }
                            userAge = user.age.ifBlank { "Not Provided" }
                            email = user.email.ifBlank { "Not Provided" }
                        } else {
                            firstName = "Not Found"
                            lastName = "Not Found"
                            username = "Not Found"
                            userSex = "Not Found"
                            userAge = "Not Found"
                            email = "Not Found"
                        }
                    } catch (e: Exception) {
                        Log.e("Profile", "Error parsing user data: ${e.message}")
                        firstName = "Error"
                        lastName = "Error"
                        username = "Error"
                        userSex = "Error"
                        userAge = "Error"
                        email = "Error"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Profile", "Database error: ${error.message}") // Log database errors
                }
            })
        }
    }

    // Observe changes in authentication state and handle navigation accordingly
    val authState by authViewModel.authState.observeAsState(AuthState.Unauthenticated)

    // UI Layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.padding(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "First Name: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = firstName,
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }

            // Last Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Last Name: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = lastName,
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }

            // Username
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Username: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = username,
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }

            // Email
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Email: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = email,
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }

            // Sex
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Sex: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = userSex,
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }

            // Age
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Age: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = userAge,
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    authViewModel.signout()
                    navController.navigate("login") // Navigate after sign-out
                },
                modifier = Modifier
                    .weight(2f)
                    .height(56.dp)
            ) {
                Text(text = "Logout")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text(text = "Home")
            }
        }
    }

    // Handle navigation to login if the user is unauthenticated
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true } // Pop the home route from the stack
            }
        }
    }
}

// Data class for user details
data class User(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val sex: String = "",
    val age: String = "",
    val email: String = ""
)
