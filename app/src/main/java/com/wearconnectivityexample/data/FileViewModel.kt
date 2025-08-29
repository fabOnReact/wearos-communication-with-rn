package com.wearconnectivityexample.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel responsible for holding the path of the image received from the
 * connected device. The state is exposed as a [StateFlow] so that UI elements
 * can reactively update when the image changes.
 */
class FileViewModel : ViewModel() {
    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath

    fun setImagePath(path: String?) {
        _imagePath.value = path
    }
}

/**
 * Simple factory that returns a single instance of [FileViewModel].
 * This allows components like composables and services to obtain the same
 * ViewModel without relying on a full DI framework.
 */
object FileViewModelFactory : ViewModelProvider.Factory {
    private val viewModel = FileViewModel()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    fun get(): FileViewModel = viewModel
}

