package com.example.qa3at.ui.screens.bookings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.qa3at.R
import com.example.qa3at.domain.model.BookingStatus
import com.example.qa3at.ui.components.EmptyState
import com.example.qa3at.ui.components.Qa3atTopBar

data class BookingSummary(
    val id: String,
    val venueName: String,
    val venueNameAr: String,
    val venueImage: String,
    val date: String,
    val guests: Int,
    val total: Double,
    val status: BookingStatus
)

@Composable
fun MyBookingsScreen(
    onBookingClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val bookings = remember {
        listOf(
            BookingSummary(
                id = "BK001",
                venueName = "Exhibition World Bahrain",
                venueNameAr = "قاعة معرض البحرين العالمي",
                venueImage = "https://www.arabiaweddings.com/sites/default/files/styles/400x400/public/companies/images/2024/03/venue_fountain.jpeg?itok=aaqi0nZp",
                date = "March 15, 2025",
                guests = 200,
                total = 2500.0,
                status = BookingStatus.CONFIRMED
            ),
            BookingSummary(
                id = "BK002",
                venueName = "Al Dana Hall",
                venueNameAr = "قاعة الدانة",
                venueImage = "https://www.arabiaweddings.com/sites/default/files/styles/400x400/public/companies/images/2018/01/al_dana_halls.jpg?itok=-A_lM44S",
                date = "April 20, 2025",
                guests = 150,
                total = 1800.0,
                status = BookingStatus.PENDING
            )
        )
    }

    Scaffold(
        topBar = {
            Qa3atTopBar(
                title = stringResource(R.string.my_bookings),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        if (bookings.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.EventAvailable,
                title = stringResource(R.string.no_bookings),
                subtitle = stringResource(R.string.no_bookings_subtitle)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bookings) { booking ->
                    BookingCard(
                        booking = booking,
                        onClick = { onBookingClick(booking.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingCard(
    booking: BookingSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = booking.venueImage,
                contentDescription = booking.venueName,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = booking.venueName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = booking.venueNameAr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    StatusBadge(status = booking.status)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = booking.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Filled.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${booking.guests}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "BHD ${booking.total.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: BookingStatus) {
    val (backgroundColor, textColor, textRes) = when (status) {
        BookingStatus.PENDING -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            R.string.status_pending
        )
        BookingStatus.CONFIRMED -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            R.string.status_confirmed
        )
        BookingStatus.CANCELLED -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            R.string.status_cancelled
        )
        BookingStatus.COMPLETED -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFF1565C0),
            R.string.status_completed
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
