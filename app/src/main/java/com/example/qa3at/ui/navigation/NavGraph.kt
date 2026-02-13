package com.example.qa3at.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.qa3at.ui.screens.assistant.AssistantScreen
import com.example.qa3at.ui.screens.auth.LoginScreen
import com.example.qa3at.ui.screens.auth.RegisterScreen
import com.example.qa3at.ui.screens.bookings.MyBookingsScreen
import com.example.qa3at.ui.screens.catering.CateringScreen
import com.example.qa3at.ui.screens.home.HomeScreen
import com.example.qa3at.ui.screens.packagebuilder.PackageBuilderScreen
import com.example.qa3at.ui.screens.payment.PaymentScreen
import com.example.qa3at.ui.screens.profile.ProfileScreen
import com.example.qa3at.ui.screens.search.SearchScreen
import com.example.qa3at.ui.screens.summary.SummaryScreen
import com.example.qa3at.ui.screens.venue.VenueDetailScreen
import com.example.qa3at.ui.screens.venue.VenueListScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavRoutes.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.Home.route) {
            HomeScreen(
                onServiceClick = { serviceId ->
                    when (serviceId) {
                        "venues" -> navController.navigate(NavRoutes.VenueList.route)
                        "catering" -> navController.navigate(NavRoutes.Catering.route)
                        // TODO: Handle other services
                        else -> navController.navigate(NavRoutes.Search.route)
                    }
                },
                onAssistantClick = { navController.navigate(NavRoutes.Assistant.route) },
                onMyBookingsClick = { navController.navigate(NavRoutes.MyBookings.route) },
                onProfileClick = { navController.navigate(NavRoutes.Profile.route) }
            )
        }
        
        composable(NavRoutes.Catering.route) {
            CateringScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Search.route) {
            SearchScreen(
                onSearch = { city, date, guests, slotId ->
                    navController.navigate(NavRoutes.VenueList.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.VenueList.route) {
            VenueListScreen(
                onVenueClick = { venueId ->
                    navController.navigate(NavRoutes.VenueDetail.createRoute(venueId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.VenueDetail.route,
            arguments = listOf(
                navArgument("venueId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val venueId = backStackEntry.arguments?.getString("venueId") ?: ""

            VenueDetailScreen(
                venueId = venueId,
                onBookNow = { date, slotId, guests ->
                    navController.navigate(NavRoutes.PackageBuilder.createRoute(venueId, date, slotId, guests))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.PackageBuilder.route,
            arguments = listOf(
                navArgument("venueId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("slotId") { type = NavType.StringType },
                navArgument("guests") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val venueId = backStackEntry.arguments?.getString("venueId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val slotId = backStackEntry.arguments?.getString("slotId") ?: ""
            val guests = backStackEntry.arguments?.getInt("guests") ?: 0

            PackageBuilderScreen(
                venueId = venueId,
                date = date,
                slotId = slotId,
                guests = guests,
                onContinue = { bookingId ->
                    navController.navigate(NavRoutes.Summary.createRoute(bookingId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.Summary.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""

            SummaryScreen(
                bookingId = bookingId,
                onProceedToPayment = {
                    navController.navigate(NavRoutes.Payment.createRoute(bookingId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.Payment.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""

            PaymentScreen(
                bookingId = bookingId,
                onPaymentSuccess = {
                    navController.navigate(NavRoutes.MyBookings.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MyBookings.route) {
            MyBookingsScreen(
                onBookingClick = { bookingId ->
                    navController.navigate(NavRoutes.Summary.createRoute(bookingId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Assistant.route) {
            AssistantScreen(
                onVenueSelected = { venueId ->
                    navController.navigate(NavRoutes.VenueDetail.createRoute(venueId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(NavRoutes.Register.route) }
            )
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
