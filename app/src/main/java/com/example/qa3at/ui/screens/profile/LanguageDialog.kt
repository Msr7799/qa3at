package com.example.qa3at.ui.screens.profile

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.qa3at.data.local.LanguageManager
import com.example.qa3at.utils.LocaleHelper
import kotlinx.coroutines.launch

@Composable
fun LanguageDialog(
    languageManager: LanguageManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    var selectedLanguage by remember { mutableStateOf("ar") }

    LaunchedEffect(Unit) {
        languageManager.selectedLanguage.collect { lang ->
            selectedLanguage = lang
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "اختر اللغة / Choose Language",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                LanguageOption(
                    title = "العربية",
                    subtitle = "Arabic",
                    languageCode = "ar",
                    isSelected = selectedLanguage == "ar",
                    onClick = {
                        scope.launch {
                            languageManager.setLanguage("ar")
                            LocaleHelper.setLocale(context, "ar")
                            activity?.recreate()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                LanguageOption(
                    title = "English",
                    subtitle = "الإنجليزية",
                    languageCode = "en",
                    isSelected = selectedLanguage == "en",
                    onClick = {
                        scope.launch {
                            languageManager.setLanguage("en")
                            LocaleHelper.setLocale(context, "en")
                            activity?.recreate()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("إغلاق / Close")
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(
    title: String,
    subtitle: String,
    languageCode: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
