package kd.dhyani.projectone

import HomePage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kd.dhyani.projectone.Pages.EnterQuiz
import kd.dhyani.projectone.Pages.LeaderboardPage
import kd.dhyani.projectone.Pages.LoginPage
import kd.dhyani.projectone.Pages.Profile
import kd.dhyani.projectone.Pages.SignupPage
import kd.dhyani.projectone.Pages.UnrankedQuizPage

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current  // Get the current context
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context)) // Use the context for ViewModel

    val navController: NavHostController = rememberNavController()

    // Observe authentication state
    val authState = authViewModel.authState.observeAsState()

    // Determine the starting destination based on the authentication state
    val startDestination = if (authState.value == AuthState.Authencitated) "home" else "login"

    // NavHost to handle navigation
    NavHost(
        navController = navController,
        startDestination = startDestination, // Start destination based on auth state
        modifier = modifier
    ) {
        composable("login") {
            LoginPage(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("signup") {
            SignupPage(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("home") {
            HomePage(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("profile") {
            Profile(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("unrankedQuiz") {
            UnrankedQuizPage(
                navController = navController
            )
        }
        composable("rankedQuiz") {
            RankedQuizPage(navController = navController, context = context)  // Pass context to RankedQuizPage
        }
        composable("enterQuiz") {
            EnterQuiz(navController = navController)
        }
        composable("leaderboard") {
            LeaderboardPage(navController = navController)
        }
    }
}
