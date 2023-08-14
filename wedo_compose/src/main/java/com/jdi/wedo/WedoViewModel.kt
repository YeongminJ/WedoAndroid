package com.jdi.wedo

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jdi.wedo.data.Constants
import com.jdi.wedo.data.Wedo
import com.jdi.wedo.data.WedoGroup
import com.jdi.wedo.repository.LocalRepository
import com.jdi.wedo.util.Log
import com.jdi.wedo.util.PreferenceUtils
import com.jdi.wedo.util.Utils
import kotlin.random.Random

class WedoViewModel(val repository: LocalRepository): ViewModel() {

    // 기본 그룹 ID
    val defaultUID: String = PreferenceUtils.getString(Constants.DEFAULT_UID, Utils.generateRandomAlphanumericString(16))
    fun initWedo() {
        //초기 저WedoViewModel장된 정보가 아무것도 없을 때?
        Firebase.database.reference.apply {
            child("groups").child(defaultUID).get().addOnSuccessListener {
                Log.i("Success loaded")
            }.addOnFailureListener {
                Log.w("Fail loaded")
                //Init
                firstInitWedo()
            }
        }

        // 저장된 UID 기준으로 목록을 불러옴
    }

    fun firstInitWedo() {
        Log.i("firstInitWedo")
        Firebase.database.reference.apply {
            val newGroup = WedoGroup(listOf(defaultUID), mutableListOf(Wedo("일정을 추가해보세용")), "할 일")
            child("groups").child(defaultUID).setValue(newGroup)
        }
    }

    fun writeWedo() {
        Firebase.database.apply {
            val message = getReference("message")
        }
    }
}