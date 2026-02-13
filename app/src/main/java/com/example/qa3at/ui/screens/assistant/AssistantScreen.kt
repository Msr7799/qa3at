package com.example.qa3at.ui.screens.assistant

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qa3at.R
import com.example.qa3at.domain.model.AssistantMessage
import com.example.qa3at.domain.model.MessageRole
import com.example.qa3at.domain.model.RecommendationTier
import com.example.qa3at.domain.model.Venue
import com.example.qa3at.domain.model.VenueRecommendation
import com.example.qa3at.ui.components.Qa3atTopBar
import com.example.qa3at.ui.components.TierBadge
import java.util.UUID

@Composable
fun AssistantScreen(
    onVenueSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val messages = remember { mutableStateListOf<AssistantMessage>() }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        messages.add(
            AssistantMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.ASSISTANT,
                content = "Hello! I'm your wedding planning assistant. I can help you find the perfect venue and packages.\n\nTo get started, please tell me:\n1. Which city are you looking in?\n2. What date do you have in mind?\n3. How many guests are you expecting?\n4. What's your approximate budget?"
            )
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Qa3atTopBar(
                title = stringResource(R.string.assistant),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        onVenueClick = onVenueSelected
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(stringResource(R.string.type_message)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            messages.add(
                                AssistantMessage(
                                    id = UUID.randomUUID().toString(),
                                    role = MessageRole.USER,
                                    content = inputText
                                )
                            )

                            val mockRecommendations = if (
                                inputText.lowercase().contains("manama") ||
                                inputText.lowercase().contains("200") ||
                                inputText.lowercase().contains("budget")
                            ) {
                                listOf(
                                    VenueRecommendation(
                                        venue = Venue(
                                            id = "1",
                                            name = "Al Dana Hall",
                                            nameAr = "قاعة الدانة",
                                            description = "Beautiful garden venue in Bahrain",
                                            descriptionAr = "قاعة حديقة جميلة في البحرين",
                                            address = "Manama, Bahrain",
                                            addressAr = "المنامة، البحرين",
                                            city = "Manama",
                                            cityAr = "المنامة",
                                            latitude = null,
                                            longitude = null,
                                            capacity = 300,
                                            minCapacity = 50,
                                            pricePerPerson = 10.0,
                                            basePrice = 1200.0,
                                            rating = 4.6,
                                            reviewCount = 189,
                                            photos = listOf(
                                                "https://www.arabiaweddings.com/sites/default/files/styles/400x400/public/companies/images/2018/01/al_dana_halls.jpg?itok=-A_lM44S"
                                            ),
                                            amenities = emptyList(),
                                            vendorId = "v1"
                                        ),
                                        tier = RecommendationTier.BUDGET,
                                        reason = "Great value with beautiful gardens",
                                        reasonAr = "قيمة رائعة مع حدائق جميلة",
                                        estimatedTotal = 4500.0
                                    ),
                                    VenueRecommendation(
                                        venue = Venue(
                                            id = "2",
                                            name = "Exhibition World Bahrain",
                                            nameAr = "قاعة معرض البحرين العالمي",
                                            description = "Prestigious wedding hall",
                                            descriptionAr = "قاعة أفراح راقية",
                                            address = "Bahrain International Exhibition Centre, Manama",
                                            addressAr = "مركز البحرين الدولي للمعارض، المنامة",
                                            city = "Manama",
                                            cityAr = "المنامة",
                                            latitude = null,
                                            longitude = null,
                                            capacity = 500,
                                            minCapacity = 100,
                                            pricePerPerson = 12.0,
                                            basePrice = 1500.0,
                                            rating = 4.8,
                                            reviewCount = 245,
                                            photos = listOf(
                                                "https://www.arabiaweddings.com/sites/default/files/styles/400x400/public/companies/images/2024/03/venue_fountain.jpeg?itok=aaqi0nZp"
                                            ),
                                            amenities = emptyList(),
                                            vendorId = "v2"
                                        ),
                                        tier = RecommendationTier.BALANCED,
                                        reason = "Perfect balance of prestige and value",
                                        reasonAr = "توازن مثالي بين الفخامة والقيمة",
                                        estimatedTotal = 7500.0
                                    ),
                                    VenueRecommendation(
                                        venue = Venue(
                                            id = "3",
                                            name = "Alkawthar Hall",
                                            nameAr = "قاعة الكوثر",
                                            description = "Ultimate luxury venue",
                                            descriptionAr = "قاعة فخامة مطلقة",
                                            address = "Sitra, Bahrain",
                                            addressAr = "سترة، البحرين",
                                            city = "Sitra",
                                            cityAr = "سترة",
                                            latitude = null,
                                            longitude = null,
                                            capacity = 400,
                                            minCapacity = 80,
                                            pricePerPerson = 15.0,
                                            basePrice = 1800.0,
                                            rating = 4.9,
                                            reviewCount = 312,
                                            photos = listOf(
                                                "https://www.infobahrain.com/wp-content/uploads/classified-listing/2023/08/alkawthar-hall-bahrain.jpg"
                                            ),
                                            amenities = emptyList(),
                                            vendorId = "v3"
                                        ),
                                        tier = RecommendationTier.LUXURY,
                                        reason = "Premium venue with exceptional service",
                                        reasonAr = "قاعة مميزة بخدمة استثنائية",
                                        estimatedTotal = 12000.0
                                    )
                                )
                            } else {
                                emptyList()
                            }

                            messages.add(
                                AssistantMessage(
                                    id = UUID.randomUUID().toString(),
                                    role = MessageRole.ASSISTANT,
                                    content = if (mockRecommendations.isNotEmpty())
                                        "Based on your preferences, here are my top 3 recommendations:"
                                    else
                                        "Thank you for the information! Could you please provide more details about your preferred city, date, guest count, and budget?",
                                    recommendations = mockRecommendations
                                )
                            )

                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.send),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: AssistantMessage,
    onVenueClick: (String) -> Unit
) {
    val isUser = message.role == MessageRole.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.assistant_name),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        message.recommendations?.let { recommendations ->
            Spacer(modifier = Modifier.height(12.dp))
            recommendations.forEach { rec ->
                RecommendationCard(
                    recommendation = rec,
                    onClick = { onVenueClick(rec.venue.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: VenueRecommendation,
    onClick: () -> Unit
) {
    val tierLabel = when (recommendation.tier) {
        RecommendationTier.BUDGET -> stringResource(R.string.tier_budget)
        RecommendationTier.BALANCED -> stringResource(R.string.tier_balanced)
        RecommendationTier.LUXURY -> stringResource(R.string.tier_luxury)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recommendation.venue.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TierBadge(tier = tierLabel)
            }
            Text(
                text = recommendation.venue.nameAr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.reason,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "BHD ${recommendation.estimatedTotal.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
