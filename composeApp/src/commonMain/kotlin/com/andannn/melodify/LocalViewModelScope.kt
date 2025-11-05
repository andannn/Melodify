package com.andannn.melodify

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel

class CommonViewModel : ViewModel()

/**
 * Provide viewmodel scope for current back record.
 */
@Composable
fun viewModelScope() = viewModel<CommonViewModel>().viewModelScope
