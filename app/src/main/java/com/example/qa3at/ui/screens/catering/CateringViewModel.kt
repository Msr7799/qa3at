package com.example.qa3at.ui.screens.catering

import androidx.lifecycle.ViewModel
import com.example.qa3at.data.repository.CateringRepository
import com.example.qa3at.domain.model.CateringPackage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CateringViewModel @Inject constructor(
    private val repository: CateringRepository
) : ViewModel() {

    private val _packages = MutableStateFlow<List<CateringPackage>>(emptyList())
    val packages: StateFlow<List<CateringPackage>> = _packages.asStateFlow()

    init {
        loadPackages()
    }

    private fun loadPackages() {
        _packages.value = repository.getCateringPackages()
    }
}
