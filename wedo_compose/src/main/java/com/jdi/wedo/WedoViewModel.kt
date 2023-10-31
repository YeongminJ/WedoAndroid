package com.jdi.wedo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jdi.wedo.data.Constants
import com.jdi.wedo.data.Wedo
import com.jdi.wedo.data.WedoGroup
import com.jdi.wedo.data.repository.LocalRepository
import com.jdi.wedo.util.Log
import com.jdi.wedo.util.PreferenceUtils
import com.jdi.wedo.util.Utils

//TODO Data 레이어에 맞게 수정 Repository, Datasource, Entity
class WedoViewModel(val repository: LocalRepository): ViewModel() {

    private val _groups = MutableLiveData<List<WedoGroup>>()
    val groups: LiveData<List<WedoGroup>> = _groups
//    var groups = mutableStateListOf<WedoGroup>()
//        private set

//    val groups = _groups

    // 기본 그룹 ID
    val defaultUID: String = PreferenceUtils.getString(Constants.DEFAULT_UID, Utils.generateRandomAlphanumericString(16))
    fun initWedo() {
        //초기 저WedoViewModel장된 정보가 아무것도 없을 때?
        Log.w("Init Wedo : $defaultUID")
        Firebase.database.reference.apply {
            val groupList = mutableListOf<WedoGroup>()
            //1. 기본 그룹을 가져옴
            child("groups").child(defaultUID).get().addOnSuccessListener {
                var group = it.getValue(WedoGroup::class.java)
//                it.getValue()
                Log.i("Success loaded : ${group.toString()}")
                if (group == null) {
                    //Init
                    group = firstInitWedo()
                }
                groupList.add(group)
                _groups.value = groupList
            }.addOnFailureListener {
                it.printStackTrace()
                Log.w("Fail loaded")
            }
            //TODO 등록한 여러 그룹을 가져옴. 저장된 LocalDB? or Preference 내에 있는 그룹 목록을 이용해서 가져옴
        }

        // 저장된 UID 기준으로 목록을 불러옴
    }

    fun firstInitWedo(): WedoGroup {
        Log.i("firstInitWedo : $defaultUID")
        val newGroup = WedoGroup(listOf(defaultUID), mutableListOf(Wedo("일정을 추가해보세용")), "할 일")
        Firebase.database.reference.apply {
            child("groups").child(defaultUID).setValue(newGroup)
            PreferenceUtils.set(Constants.DEFAULT_UID, defaultUID)
        }
        return newGroup
    }

    fun writeWedo() {
        Firebase.database.apply {
            val message = getReference("message")
        }
    }

    fun addWedo(text: String, groupName: String) {
        getGroupByName(groupName)?.let {
            it.wedos.toMutableList().add(Wedo(text))
            Firebase.database.reference.apply {
                child("groups").child(defaultUID).setValue(it)
                PreferenceUtils.set(Constants.DEFAULT_UID, defaultUID)
            }
            //update
            _groups.value = _groups.value?.toMutableList()
        }
    }

    fun getGroupByName(groupName: String): WedoGroup? {
        _groups.value?.forEachIndexed { index, wedoGroup ->
            if (wedoGroup.groupname == groupName) {
                return wedoGroup
            }
        }
        return null
    }
}