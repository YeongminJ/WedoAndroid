package com.jdi.wedo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jdi.wedo.data.Constants.GROUPS
import com.jdi.wedo.data.Constants.USERS
import com.jdi.wedo.data.User
import com.jdi.wedo.data.WedoGroup
import com.jdi.wedo.data.repository.LocalRepository
import com.jdi.wedo.util.Log
import com.jdi.wedo.util.PreferenceUtils

//TODO Data 레이어에 맞게 수정 Repository, Datasource, Entity
class WedoViewModel(val repository: LocalRepository): ViewModel() {

    private val _groups = MutableLiveData<List<WedoGroup>>()
    val groups: LiveData<List<WedoGroup>> = _groups
//    var groups = mutableStateListOf<WedoGroup>()
//        private set

//    val groups = _groups

    // 기본 그룹 ID
    val defaultUID: String = PreferenceUtils.getDefaultUid()

    val db = Firebase.firestore
    init {
        initWedo()
    }

    fun initWedo() {
        //초기 저장된 정보가 아무것도 없을 때?
        Log.w("Init Wedo : $defaultUID")
        //1. uuid 정보로 내 그룹 목록 가져오기
        db.collection(USERS).apply {
            document(defaultUID).get().addOnCompleteListener { task->
                val user = task.result.toObject(User::class.java)
                if (user == null) {
                    firstInitWedo()
                }
                else {
                    //TODO 이 경우는 로그인 구현되고 나서의 일인듯
                }
            }
        }
    }

    fun firstInitWedo() {
        //create
        //1. group 먼저 만들기
        val groupUid = WedoGroup.getGeneratorGroup()
        db.collection(GROUPS).also { gc->
            WedoGroup(groupUid).also { group->
                //만들어진 그룹 저장
                gc.document(groupUid).set(group)
            }
        }
        //2. 유저 정보 만들기 TODO Email
        User(uid = defaultUID, groups = listOf(groupUid)).also {
            db.collection(USERS).document(defaultUID).set(it)
        }
    }

    /*fun writeWedo() {
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
    }*/
}