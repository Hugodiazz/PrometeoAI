package com.hdev.prometeoai.ViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AppViewModel: ViewModel(){
    private val _selectedItem = MutableStateFlow<ImageVector>(Icons.Default.Edit)
    val selectedItem: StateFlow<ImageVector> = _selectedItem
    private val _selectedMod = MutableStateFlow<Int>(0)
    val selectedMod: StateFlow<Int> = _selectedMod

    fun onItemSelected(item: ImageVector) {
        _selectedItem.update { item }
    }

    fun onModoSelected(modo: Int) {
        _selectedMod.update { modo }
    }
}