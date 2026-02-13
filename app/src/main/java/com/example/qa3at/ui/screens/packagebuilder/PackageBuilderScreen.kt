package com.example.qa3at.ui.screens.packagebuilder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.qa3at.R
import com.example.qa3at.domain.model.PackageTier
import com.example.qa3at.ui.components.PrimaryButton
import com.example.qa3at.ui.components.Qa3atTopBar
import com.example.qa3at.ui.components.SecondaryButton
import com.example.qa3at.ui.components.StepIndicator
import com.example.qa3at.ui.components.TierBadge
import com.example.qa3at.ui.theme.Diamond
import com.example.qa3at.ui.theme.GoldTier
import com.example.qa3at.ui.theme.Silver

data class ServiceStep(
    val id: String,
    val titleRes: Int,
    val subtitleRes: Int,
    val iconRes: Int,
    val options: List<PackageOption>
)

data class PackageOption(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val tier: PackageTier?,
    val basePrice: Double,
    val pricePerPerson: Double
)

@Composable
fun PackageBuilderScreen(
    venueId: String,
    date: String,
    slotId: String,
    guests: Int,
    onContinue: (bookingId: String) -> Unit,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val selectedOptions = remember { mutableStateMapOf<String, PackageOption?>() }

    val steps = listOf(
        ServiceStep(
            id = "decoration",
            titleRes = R.string.step_decoration,
            subtitleRes = R.string.step_decoration_desc,
            iconRes = R.drawable.ic_service_decoration,
            options = listOf(
                PackageOption("dec_basic", "Basic Stage", "كوشة أساسية", "Simple elegant stage setup", null, 500.0, 0.0),
                PackageOption("dec_silver", "Silver Package", "الباقة الفضية", "Enhanced stage with flowers", PackageTier.SILVER, 1200.0, 0.0),
                PackageOption("dec_gold", "Gold Package", "الباقة الذهبية", "Premium stage and decoration", PackageTier.GOLD, 2500.0, 0.0),
                PackageOption("dec_diamond", "Diamond Package", "الباقة الماسية", "Luxury custom design", PackageTier.DIAMOND, 5000.0, 0.0)
            )
        ),
        ServiceStep(
            id = "catering",
            titleRes = R.string.step_catering,
            subtitleRes = R.string.step_catering_desc,
            iconRes = R.drawable.ic_service_catering,
            options = listOf(
                PackageOption("cat_silver", "Silver Buffet", "بوفيه فضي", "Quality buffet selection", PackageTier.SILVER, 0.0, 18.0),
                PackageOption("cat_gold", "Gold Buffet", "بوفيه ذهبي", "Premium buffet + desserts", PackageTier.GOLD, 0.0, 25.0),
                PackageOption("cat_diamond", "Diamond Buffet", "بوفيه ماسي", "Luxury buffet + cake", PackageTier.DIAMOND, 0.0, 35.0)
            )
        ),
        ServiceStep(
            id = "photography",
            titleRes = R.string.step_photography,
            subtitleRes = R.string.step_photography_desc,
            iconRes = R.drawable.ic_service_photo,
            options = listOf(
                PackageOption("photo_2h", "2 Hours", "ساعتان", "Basic photo coverage", null, 350.0, 0.0),
                PackageOption("photo_4h", "4 Hours", "4 ساعات", "Standard coverage + album", null, 750.0, 0.0),
                PackageOption("photo_8h", "Full Day", "يوم كامل", "Complete coverage + video", null, 1500.0, 0.0)
            )
        ),
        ServiceStep(
            id = "music",
            titleRes = R.string.step_music,
            subtitleRes = R.string.step_music_desc,
            iconRes = R.drawable.ic_service_music,
            options = listOf(
                PackageOption("music_dj", "DJ", "دي جي", "Professional DJ service", null, 400.0, 0.0),
                PackageOption("music_band", "Live Band", "فرقة موسيقية", "Live band performance", null, 1800.0, 0.0),
                PackageOption("music_combo", "Band + DJ", "فرقة + دي جي", "Live band then DJ", null, 2200.0, 0.0)
            )
        )
    )

    val stepTitles = steps.map { stringResource(it.titleRes) }
    val currentService = steps[currentStep]

    val totalPrice = remember(selectedOptions.toMap()) {
        var total = 1500.0 // Base venue price in BHD
        selectedOptions.values.filterNotNull().forEach { option ->
            total += option.basePrice + (option.pricePerPerson * guests)
        }
        total
    }

    Scaffold(
        topBar = {
            Qa3atTopBar(
                title = stringResource(R.string.build_package),
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.estimated_total),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "BHD ${totalPrice.toInt()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (currentStep < steps.size - 1) {
                                SecondaryButton(
                                    text = stringResource(R.string.skip),
                                    onClick = {
                                        selectedOptions[currentService.id] = null
                                        currentStep++
                                    },
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                            PrimaryButton(
                                text = if (currentStep < steps.size - 1)
                                    stringResource(R.string.next)
                                else
                                    stringResource(R.string.continue_text),
                                onClick = {
                                    if (currentStep < steps.size - 1) {
                                        currentStep++
                                    } else {
                                        onContinue("booking_${System.currentTimeMillis()}")
                                    }
                                },
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StepIndicator(
                currentStep = currentStep,
                totalSteps = steps.size,
                stepTitles = stepTitles,
                modifier = Modifier.padding(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(currentService.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(currentService.titleRes),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(currentService.subtitleRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                currentService.options.forEach { option ->
                    PackageOptionCard(
                        option = option,
                        guests = guests,
                        isSelected = selectedOptions[currentService.id]?.id == option.id,
                        onClick = {
                            selectedOptions[currentService.id] = if (selectedOptions[currentService.id]?.id == option.id) null else option
                        }
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun PackageOptionCard(
    option: PackageOption,
    guests: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val tierColor = when (option.tier) {
        PackageTier.DIAMOND -> Diamond
        PackageTier.GOLD -> GoldTier
        PackageTier.SILVER -> Silver
        null -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = option.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    option.tier?.let {
                        TierBadge(tier = it.name)
                    }
                }
                Text(
                    text = option.nameAr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val price = option.basePrice + (option.pricePerPerson * guests)
                Text(
                    text = "BHD ${price.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (option.pricePerPerson > 0) {
                    Text(
                        text = "(BHD ${option.pricePerPerson.toInt()} x $guests ${stringResource(R.string.guests_label)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
