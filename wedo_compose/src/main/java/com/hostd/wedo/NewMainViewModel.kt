package com.hostd.wedo

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hostd.wedo.data.Constants
import com.hostd.wedo.data.User
import com.hostd.wedo.data.Wedo
import com.hostd.wedo.data.WedoGroup
import com.hostd.wedo.util.Log
import com.hostd.wedo.util.PreferenceUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class NewMainViewModel: ViewModel() {

    val groups = MutableSharedFlow<List<WedoGroup>>()

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
        db.collection(Constants.USERS).apply {
            document(defaultUID).get().addOnCompleteListener { task->
                if (task.isSuccessful) {
                    val user = task.result.toObject(User::class.java)
                    if (user == null) {
                        firstInitWedo()
                    }
                    else {
                        //group 초기화
                        user.groups.forEach {
                            db.collection(Constants.GROUPS).document(it).get().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    it.result.toObject(WedoGroup::class.java)?.let { group->
                                        groups.collect
                                        groups.emit()
                                        val mGroups = _groups.value
                                        mGroups?.add(group)
                                        //refresh
                                        _groups.postValue(mGroups?.toMutableList())
//                                        _groups.value = mGroups?.toMutableList()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun firstInitWedo() {
        //create
        //1. group 먼저 만들기
        val groupUid = WedoGroup.getGeneratorGroup()
        db.collection(Constants.GROUPS).also { gc->
            WedoGroup(groupUid, member = listOf(defaultUID)).also { group->
                //만들어진 그룹 저장
                gc.document(groupUid).set(group)
                val mGroups = _groups.value
                mGroups?.add(group)
                //refresh
                _groups.postValue(mGroups?.toMutableList())
//                _groups.value = mGroups?.toMutableList()
            }
        }
        //2. 유저 정보 만들기 TODO Email
        User(uid = defaultUID, groups = listOf(groupUid)).also {
            db.collection(Constants.USERS).document(defaultUID).set(it)
        }
    }

    fun addWedo(text: String, gUid: String) {
        db.collection(Constants.GROUPS).also { gc->
            gc.document(gUid).get().addOnCompleteListener { task->
                if (task.isSuccessful) {
                    task.result.toObject(WedoGroup::class.java)?.let { group->
                        val wedos = group.wedos.toMutableList().apply {
                            add(Wedo(text))
                        }
                        group.wedos = wedos
                        gc.document(gUid).set(group)
                        _groups.value = _groups.value?.apply {
                            toMutableList().add(group)
                        }
                    }
                }
            }
        }
    }
    /*fun writeWedo() {
        Firebase.database.apply {
            val message = getReference("message")
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