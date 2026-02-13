package com.example.qa3at.ui.screens.catering

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.qa3at.R
import com.example.qa3at.domain.model.CateringPackage
import com.example.qa3at.domain.model.CateringTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CateringScreen(
    onBack: () -> Unit
) {
    val viewModel: CateringViewModel = hiltViewModel()
    val packages by viewModel.packages.collectAsState()
    
    // Determine language
    val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)?.language 
    val isArabic = currentLocale == "ar"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.service_catering)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Group packages by tier
            val grouped = packages.groupBy { it.tier }

            // Tier 1: The Best
            grouped[CateringTier.THE_BEST]?.let { list ->
                val title = if (isArabic) "الأفضل (اقتصادي)" else "The Best (Economic)"
                item { SectionHeader(title, Color(0xFF4CAF50)) }
                items(list) { pkg -> CateringCard(pkg, isArabic) }
            }

            // Tier 2: Very Excellent
            grouped[CateringTier.VERY_EXCELLENT]?.let { list ->
                val title = if (isArabic) "ممتاز جداً (بريميوم)" else "Very Excellent (Premium)"
                item { SectionHeader(title, Color(0xFFFFA000)) } // Gold
                items(list) { pkg -> CateringCard(pkg, isArabic) }
            }

            // Tier 3: Excellent (Luxury)
            grouped[CateringTier.EXCELLENT]?.let { list ->
                val title = if (isArabic) "ممتاز (فاخر)" else "Excellent (Luxury)"
                item { SectionHeader(title, Color(0xFF9C27B0)) } // Purple
                items(list) { pkg -> CateringCard(pkg, isArabic) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.2f),
            contentColor = color
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun CateringCard(pkg: CateringPackage, isArabic: Boolean) {
    // Select localized text
    val providerName = if (isArabic) pkg.providerNameAr else pkg.providerName
    val packageName = if (isArabic) pkg.packageNameAr else pkg.packageName
    val description = if (isArabic) pkg.descriptionAr else pkg.description
    val menuItems = if (isArabic) pkg.menuItemsAr else pkg.menuItems
    val currency = if (isArabic) "د.ب" else "BHD"
    val perPerson = stringResource(R.string.per_person)
    val menuHighlights = if (isArabic) "أبرز الأطباق:" else "Menu Highlights:"
    val selectPackage = if (isArabic) "اختر الباقة" else "Select Package"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Image
            AsyncImage(
                model = pkg.imageUrl,
                contentDescription = packageName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = providerName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = packageName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${pkg.pricePerPerson} $currency / $perPerson",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = menuHighlights,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                menuItems.take(4).forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { /* TODO: Request Quote */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(selectPackage)
                }
            }
        }
    }
}
