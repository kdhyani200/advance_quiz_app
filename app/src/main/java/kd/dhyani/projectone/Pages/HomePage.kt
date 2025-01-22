import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kd.dhyani.projectone.AuthState
import kd.dhyani.projectone.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()

    // Redirect to login if unauthenticated
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {

        // Middle Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ranked Quiz Button
            Button(
                onClick = { navController.navigate("rankedQuiz") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                interactionSource = MutableInteractionSource() // Add this line
            ) {
                Text(
                    text = "Ranked Quiz",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unranked Quiz Button
            Button(
                onClick = { navController.navigate("unrankedQuiz") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                interactionSource = MutableInteractionSource() // Add this line
            ) {
                Text(
                    text = "Unranked Quiz",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bottom Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("leaderboard") },
                modifier = Modifier.weight(1f).height(56.dp),
                interactionSource = MutableInteractionSource() // Add this line
            ) {
                Text(text = "Leaderboard")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.weight(1f).height(56.dp),
                interactionSource = MutableInteractionSource() // Add this line
            ) {
                Text(text = "Profile")
            }
        }
    }
}
