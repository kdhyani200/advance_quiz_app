package kd.dhyani.projectone.Pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kd.dhyani.projectone.ui.theme.Purple40
import kotlinx.coroutines.delay

@Composable
fun EnterQuiz(navController: NavController) {
    val questions = listOf(
        "What is the capital of France?",
        "What is 2 + 2?",
        "What is the largest ocean?",
        "What is the square root of 16?",
        "Who wrote 'Romeo and Juliet'?"
    )
    val options = listOf(
        listOf("Paris", "London", "Berlin", "Madrid"),
        listOf("3", "4", "5", "6"),
        listOf("Atlantic", "Indian", "Pacific", "Arctic"),
        listOf("2", "4", "6", "8"),
        listOf("Shakespeare", "Dickens", "Hemingway", "Frost")
    )
    val correctAnswers = listOf(0, 1, 2, 1, 0) // Indices of correct answers

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf(-1) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var timer by remember { mutableStateOf(30) }
    var quizCompleted by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Function to reset question state when moving to next question
    fun resetQuestionState() {
        selectedOptionIndex = -1
        isAnswerSubmitted = false
        timer = 30
    }

    LaunchedEffect(timer) {
        if (timer > 0 && !quizCompleted) {
            delay(1000L)
            timer -= 1
        } else if (!quizCompleted) {
            // Move to next question when time runs out
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                resetQuestionState() // Reset state for next question
            } else {
                quizCompleted = true
            }
        }
    }

    if (quizCompleted) {
        // Display score screen
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Quiz Completed!")
                Text(text = "You scored $score out of ${questions.size * 10}.")
                Button(onClick = { navController.navigate("home") }) {
                    Text(text = "Go Home")
                }
            }
        }
    } else {
        // Display current question and timer
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Time Remaining: $timer seconds")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Question ${currentQuestionIndex + 1}: ${questions[currentQuestionIndex]}")
                Spacer(modifier = Modifier.height(16.dp))

                // Display options using OutlinedButton
                options[currentQuestionIndex].forEachIndexed { index, option ->
                    OutlinedButton(
                        onClick = {
                            if (!isAnswerSubmitted) selectedOptionIndex = index
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        border = if (selectedOptionIndex == index) {
                            BorderStroke(2.dp, Purple40)
                        } else {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        }
                    ) {
                        Text(text = option)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit and Next buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Submit Button
                    Button(
                        onClick = {
                            if (selectedOptionIndex == -1) {
                                // You can show a message to the user to select an option before submitting
                            } else {
                                isAnswerSubmitted = true
                                if (selectedOptionIndex == correctAnswers[currentQuestionIndex]) {
                                    score += 10
                                }
                            }
                        },
                        enabled = !isAnswerSubmitted
                    ) {
                        Text(text = "Submit")
                    }

                    // Next Button
                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                resetQuestionState() // Reset state for next question
                            } else {
                                quizCompleted = true
                            }
                        },
                        enabled = isAnswerSubmitted // Disable Next button until answer is submitted
                    ) {
                        Text(text = "Next")
                    }
                }
                OutlinedButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Exit Quiz")
                }
            }
        }
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Quiz") },
            text = { Text("Are you sure you want to leave the quiz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.navigate("home") // Navigate to home
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false } // Close the dialog
                ) {
                    Text("No")
                }
            }
        )
    }
}