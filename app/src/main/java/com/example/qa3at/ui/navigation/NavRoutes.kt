package com.example.qa3at.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Search : NavRoutes("search")
    data object VenueList : NavRoutes("venue_list")
    data object VenueDetail : NavRoutes("venue_detail/{venueId}") {
        fun createRoute(venueId: String) = "venue_detail/$venueId"
    }
    data object PackageBuilder : NavRoutes("package_builder/{venueId}/{date}/{slotId}/{guests}") {
        fun createRoute(venueId: String, date: String, slotId: String, guests: Int) =
            "package_builder/$venueId/$date/$slotId/$guests"
    }
    data object Summary : NavRoutes("summary/{bookingId}") {
        fun createRoute(bookingId: String) = "summary/$bookingId"
    }
    data object Payment : NavRoutes("payment/{bookingId}") {
        fun createRoute(bookingId: String) = "payment/$bookingId"
    }
    data object MyBookings : NavRoutes("my_bookings")
    data object Assistant : NavRoutes("assistant")
    data object Login : NavRoutes("login")
    data object Register : NavRoutes("register")
    data object Profile : NavRoutes("profile")
    data object Catering : NavRoutes("catering")
}
