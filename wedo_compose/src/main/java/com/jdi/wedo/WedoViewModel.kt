package com.jdi.wedo

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class WedoViewModel: ViewModel() {

    fun writeWedo() {
        Firebase.database.apply {
            val message = getReference("message")

        }
    }
}