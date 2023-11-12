package com.hostd.wedo.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hostd.wedo.NewMainViewModel
import com.hostd.wedo.WedoViewModel
//import com.hostd.wedo.data.repository.LocalRepository

class NewWedoViewModelFactory(/*val repository: LocalRepository*/): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NewMainViewModel::class.java)) {
            NewMainViewModel(/*repository*/) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}