package com.jdi.wedo.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jdi.wedo.WedoViewModel
import com.jdi.wedo.repository.LocalRepository

class WedoViewModelFactory(val repository: LocalRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WedoViewModel::class.java)) {
            WedoViewModel(repository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}