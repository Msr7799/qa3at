package com.example.qa3at.ui.screens.search

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qa3at.R
import com.example.qa3at.domain.model.City
import com.example.qa3at.domain.model.TimeSlot
import com.example.qa3at.ui.components.ErrorScreen
import com.example.qa3at.ui.components.LoadingScreen
import com.example.qa3at.ui.components.PrimaryButton
import com.example.qa3at.ui.components.Qa3atTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CityOption(
    val id: String,
    val name: String,
    val nameAr: String
)

data class TimeSlotOption(
    val id: String,
    val name: String,
    val nameAr: String,
    val startTime: String,
    val endTime: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onSearch: (city: String, date: String, guests: Int, slotId: String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Determine language
    val currentLocale = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().get(0)?.language 
    val isArabic = currentLocale == "ar"

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    val cities = remember(uiState.cities) {
        uiState.cities.map { it.toOption() }
    }

    val timeSlots = remember(uiState.timeSlots) {
        uiState.timeSlots.map { it.toOption() }
    }

    var selectedCity by remember { mutableStateOf<CityOption?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    var guestCount by remember { mutableIntStateOf(100) }
    var selectedSlot by remember { mutableStateOf<TimeSlotOption?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }
    var slotExpanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val isFormValid = selectedCity != null &&
            selectedDate.isNotBlank() &&
            guestCount > 0 &&
            selectedSlot != null

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    if (uiState.error != null) {
        ErrorScreen(
            message = uiState.error ?: stringResource(R.string.error_occurred),
            onRetry = { viewModel.load() }
        )
        return
    }

    Scaffold(
        topBar = {
            Qa3atTopBar(
                title = stringResource(R.string.search_venues),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.find_perfect_venue),
                style = MaterialTheme.typography.headlineSmall
            )

            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = { cityExpanded = !cityExpanded }
            ) {
                OutlinedTextField(
                    value = if (selectedCity != null) {
                        if (isArabic) selectedCity!!.nameAr else selectedCity!!.name
                    } else "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.city)) },
                    placeholder = { Text(stringResource(R.string.select_city)) },
                    leadingIcon = {
                        Icon(Icons.Filled.LocationCity, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = cityExpanded,
                    onDismissRequest = { cityExpanded = false }
                ) {
                    cities.forEach { city ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Text(city.name)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = city.nameAr,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                selectedCity = city
                                cityExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.date)) },
                placeholder = { Text(stringResource(R.string.select_date)) },
                leadingIcon = {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                enabled = false
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showDatePicker = true }
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.date)) },
                    placeholder = { Text(stringResource(R.string.select_date)) },
                    leadingIcon = {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            OutlinedTextField(
                value = if (guestCount > 0) guestCount.toString() else "",
                onValueChange = { value ->
                    guestCount = value.toIntOrNull() ?: 0
                },
                label = { Text(stringResource(R.string.guests)) },
                placeholder = { Text(stringResource(R.string.enter_guest_count)) },
                leadingIcon = {
                    Icon(Icons.Filled.Groups, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = slotExpanded,
                onExpandedChange = { slotExpanded = !slotExpanded }
            ) {
                OutlinedTextField(
                    value = selectedSlot?.let { 
                        val name = if (isArabic) it.nameAr else it.name
                        "$name (${it.startTime} - ${it.endTime})" 
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.time_slot)) },
                    placeholder = { Text(stringResource(R.string.select_time_slot)) },
                    leadingIcon = {
                        Icon(Icons.Filled.AccessTime, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = slotExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = slotExpanded,
                    onDismissRequest = { slotExpanded = false }
                ) {
                    timeSlots.forEach { slot ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Row {
                                        Text(slot.name)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = slot.nameAr,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${slot.startTime} - ${slot.endTime}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                selectedSlot = slot
                                slotExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = stringResource(R.string.search),
                onClick = {
                    selectedCity?.let { city ->
                        selectedSlot?.let { slot ->
                            onSearch(city.id, selectedDate, guestCount, slot.id)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            selectedDate = sdf.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun City.toOption(): CityOption {
    return CityOption(
        id = id,
        name = name,
        nameAr = nameAr
    )
}

private fun TimeSlot.toOption(): TimeSlotOption {
    return TimeSlotOption(
        id = id,
        name = name,
        nameAr = nameAr,
        startTime = startTime,
        endTime = endTime
    )
}
