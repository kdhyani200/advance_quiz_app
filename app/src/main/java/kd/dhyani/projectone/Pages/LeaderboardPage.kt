package kd.dhyani.projectone.Pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import kd.dhyani.projectone.R

@Composable
fun LeaderboardPage(navController: NavController) {
    // Load the Lottie animation
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.comming_soon)) // Replace with your JSON resource

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Center the Lottie animation
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = lottieComposition,
                iterations = LottieConstants.IterateForever, // Infinite animation loop
                modifier = Modifier.size(300.dp) // Adjust size as needed
            )
        }

        // Place the Home button at the bottom center
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
}
