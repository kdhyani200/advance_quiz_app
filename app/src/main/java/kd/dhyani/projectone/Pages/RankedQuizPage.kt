package kd.dhyani.projectone

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun RankedQuizPage(navController: NavController, context: Context) {
    val calendar = Calendar.getInstance()

    // Set the start time for the quiz (e.g., 5:00 PM)
    calendar.apply {
        set(Calendar.HOUR_OF_DAY, 21)
        set(Calendar.MINUTE, 5)
        set(Calendar.SECOND, 0)
    }

    var quizStartTime by remember { mutableStateOf(calendar.timeInMillis) }
    val quizEndTime by remember { mutableStateOf(quizStartTime + 30 * 60 * 1000L) } // Quiz lasts 30 minutes

    // Firebase reference for user participation status
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users/${FirebaseAuth.getInstance().currentUser?.uid}/quizParticipationStatus")

    // SharedPreferences to track participation
    val sharedPreferences = context.getSharedPreferences("QuizPreferences", Context.MODE_PRIVATE)

    // State variables
    var timeRemaining by remember { mutableStateOf(0L) }
    var isStartQuizEnabled by remember { mutableStateOf(false) }
    var currentAnimation by remember { mutableStateOf(R.raw.countdown) }
    var hasParticipated by remember { mutableStateOf(false) }

    // Fetch participation status from Firebase
    LaunchedEffect(Unit) {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hasParticipated = snapshot.getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors here
            }
        })
    }

    // Launch a coroutine to update states in real-time
    LaunchedEffect(quizStartTime) {
        while (true) {
            val currentTime = System.currentTimeMillis()

            when {
                currentTime in quizStartTime until quizEndTime -> {
                    // Quiz is active
                    timeRemaining = quizEndTime - currentTime
                    isStartQuizEnabled = !hasParticipated // Enable if not participated
                    currentAnimation = R.raw.started_quiz
                }
                currentTime < quizStartTime -> {
                    // Before quiz starts
                    timeRemaining = quizStartTime - currentTime
                    isStartQuizEnabled = false
                    currentAnimation = R.raw.countdown
                }
                currentTime >= quizEndTime -> {
                    // After quiz ends, reset for next day
                    calendar.add(Calendar.DATE, 1) // Increment date by one day
                    quizStartTime = calendar.timeInMillis // Update quiz start time
                    timeRemaining = quizStartTime - currentTime // Time until next quiz
                    isStartQuizEnabled = false
                    currentAnimation = R.raw.countdown

                    // Reset participation status for the next quiz day
                    sharedPreferences.edit().putBoolean("hasParticipated", false).apply()
                    userRef.setValue(false)
                }
            }

            delay(1000L) // Update every second
        }
    }

    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(currentAnimation))

    // UI Layout
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animation
            LottieAnimation(
                composition = lottieComposition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(300.dp)
            )

            // Countdown Timer
            Text(
                text = if (timeRemaining > 0) formatTime(timeRemaining) else "Quiz Ended",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Start Quiz Button
            Button(
                onClick = {
                    // Update participation status in Firebase and SharedPreferences
                    sharedPreferences.edit().putBoolean("hasParticipated", true).apply()
                    userRef.setValue(true)
                    navController.navigate("enterQuiz")
                },
                enabled = isStartQuizEnabled && !hasParticipated // Disable if already participated
            ) {
                Text(text = "Start Quiz")
            }
        }

        // Home Button
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.padding(16.dp)
                    .fillMaxWidth(0.6f)
            ) {
                Text(text = "Home")
            }
        }
    }
}

// Helper function to format time
fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 60000) % 60
    val hours = (milliseconds / 3600000)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
