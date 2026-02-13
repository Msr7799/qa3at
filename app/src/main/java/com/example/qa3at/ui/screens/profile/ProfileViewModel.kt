package com.example.qa3at.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.example.qa3at.data.local.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val languageManager: LanguageManager
) : ViewModel()
