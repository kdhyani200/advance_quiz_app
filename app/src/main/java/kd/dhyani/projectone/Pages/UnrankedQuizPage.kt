package kd.dhyani.projectone.Pages

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kd.dhyani.projectone.R
import kd.dhyani.projectone.ui.theme.GreenJC
import kd.dhyani.projectone.ui.theme.Purple40

@Composable
fun UnrankedQuizPage(
    navController: NavController
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf(-1) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var isQuizCompleted by remember { mutableStateOf(false) }

    // List of questions
    val questions = listOf(
        Question(
            questionText = "The Maha Kumbh held after how many years?",
            options = listOf("8 Years", "6 Years", "10 Years", "12 Years"),
            correctAnswerIndex = 3
        ),
        Question(
            questionText = "Which planet is known as the Red Planet?",
            options = listOf("Earth", "Mars", "Jupiter", "Venus"),
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Tax on imports considered as an example of ?",
            options = listOf("Collateral", "Trade barriers", "Foreign trade", "Terms of trade"),
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "How many Shlokas are there in the Valmiki Ramayana?",
            options = listOf("12000", "10800", "24000", "36000"),
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "What is India's fastest train?",
            options = listOf("Delhi–Meerut RRTS", "Shatabdi Express", "Vande Bharat Express", "Rajdhani Express"),
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "How many Union Territories are there in India?",
            options = listOf("8", "10", "12", "9"),
            correctAnswerIndex = 0
        ),
        Question(
            questionText = "Of which international group is India not a member?",
            options = listOf("BRICS", "ASEAN", "G20", "SEO"),
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "How many medals did India win at the Paris 2024 Olympics?",
            options = listOf("4", "6", "8", "10"),
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Which of the following countries is not a member of the World Health Organization (WHO)?",
            options = listOf("Canada", "India", "Taiwan", "United States"),
            correctAnswerIndex = 3
        ),
        Question(
            questionText = "How many members are in G20?",
            options = listOf("19", "20", "21", "22"),
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "What is the name of India's biggest dam?",
            options = listOf("Bhakra Nangal Dam", "Nagarjuna Sagar Dam", "Sardar Sarovar Dam", "Tehri Dam"),
            correctAnswerIndex = 0
        )
        // Add the rest of the questions here...
    )

    val context = LocalContext.current

    if (isQuizCompleted) {
        // Display Lottie Animation and Message
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.unrank_quiz_last_page_animation)
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
//                text = "No more questions left,\n        Adding soon!",“You’ve reached the end! Keep an eye out for more questions coming soon.”
                text = "You reached the end! Keep an eye out for more questions coming soon.",
                fontSize = 15.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    } else {
        // Display Quiz Questions
        val currentQuestion = questions[currentQuestionIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the question
            Text(
                text = currentQuestion.questionText,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display the options
            currentQuestion.options.forEachIndexed { index, option ->
                val borderColor = when {
                    index == selectedOptionIndex && !isAnswerSubmitted -> Purple40 // Black boundary for selected option before submission
                    isAnswerSubmitted && index == selectedOptionIndex && index == currentQuestion.correctAnswerIndex -> MaterialTheme.colorScheme.primary // Correct choice
                    isAnswerSubmitted && index == selectedOptionIndex -> MaterialTheme.colorScheme.error // Wrong choice
                    isAnswerSubmitted && index == currentQuestion.correctAnswerIndex -> MaterialTheme.colorScheme.primary // Correct answer highlighted
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) // Default
                }

                OutlinedButton(
                    onClick = { if (!isAnswerSubmitted) selectedOptionIndex = index },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    border = BorderStroke(2.dp, borderColor)
                ) {
                    Text(text = option)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (selectedOptionIndex == -1) {
                        Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
                    } else {
                        isAnswerSubmitted = true
                        if (selectedOptionIndex == currentQuestion.correctAnswerIndex) {
                            Toast.makeText(context, "Right answer!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Wrong answer!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isAnswerSubmitted
            ) {
                Text(text = "Submit")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row for Previous, Submit, and Next Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button
                IconButton(
                    onClick = {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                            selectedOptionIndex = -1
                            isAnswerSubmitted = false
                        }
                    },
                    enabled = currentQuestionIndex > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                        contentDescription = "Previous",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Next Button
                IconButton(
                    onClick = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                            selectedOptionIndex = -1
                            isAnswerSubmitted = false
                        } else {
                            isQuizCompleted = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowRight,
                        contentDescription = "Next",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // Home Button to go to the home screen
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.6f) // Button takes 60% of screen width
        ) {
            Text(text = "Home")
        }
    }
}

data class Question(
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)
