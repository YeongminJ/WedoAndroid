package com.hostd.wedo.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hostd.wedo.WedoViewModel
import com.hostd.wedo.data.repository.StoreRepository

//import com.hostd.wedo.data.repository.LocalRepository

class WedoViewModelFactory(/*val repository: LocalRepository*/): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WedoViewModel::class.java)) {
            val repository = StoreRepository()
            WedoViewModel(repository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}