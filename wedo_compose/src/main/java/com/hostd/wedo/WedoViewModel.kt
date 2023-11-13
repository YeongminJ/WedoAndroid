package com.hostd.wedo

//import com.hostd.wedo.data.repository.LocalRepository
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hostd.wedo.data.Constants.GROUPS
import com.hostd.wedo.data.Constants.USERS
import com.hostd.wedo.data.User
import com.hostd.wedo.data.Wedo
import com.hostd.wedo.data.WedoGroup
import com.hostd.wedo.util.Log
import com.hostd.wedo.util.PreferenceUtils

//TODO Data 레이어에 맞게 수정 Repository, Datasource, Entity
class WedoViewModel(/*val repository: LocalRepository*/): ViewModel() {

    val LAST_GROUP_ID = "last_group_id"

    var currentGroupId: String = PreferenceUtils.get(LAST_GROUP_ID, "")

    val origin: MutableLiveData<List<WedoGroup>> = MutableLiveData(listOf())

    private val _wedos: MutableLiveData<List<Wedo>> = MutableLiveData(listOf())
    val wedos: LiveData<List<Wedo>> = _wedos

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
                if (task.isSuccessful) {
                    val user = task.result.toObject(User::class.java)
                    if (user == null) {
                        firstInitWedo()
                    }
                    else {
                        //group 초기화
                        user.groups.forEach {
                            db.collection(GROUPS).document(it).get().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    it.result.toObject(WedoGroup::class.java)?.let { mGroup->
                                        _wedos.value = _wedos.value?.toMutableList()?.apply {
                                            addAll(mGroup.wedos)
                                        }
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
        db.collection(GROUPS).also { gc->
            WedoGroup(groupUid, member = listOf(defaultUID)).also { group->
                //만들어진 그룹 저장
                gc.document(groupUid).set(group)
//                groups.add(group)
                currentGroupId = groupUid
                PreferenceUtils.set(LAST_GROUP_ID, groupUid)
            }
        }
        //2. 유저 정보 만들기 TODO Email
        User(uid = defaultUID, groups = listOf(groupUid)).also {
            db.collection(USERS).document(defaultUID).set(it)
        }
    }

    fun addWedo(text: String, gUid: String = currentGroupId) {
        db.collection(GROUPS).also { gc->
            gc.document(gUid).get().addOnCompleteListener { task->
                if (task.isSuccessful) {
                    task.result.toObject(WedoGroup::class.java)?.let { group->
                        val newWedo = Wedo(todo = text, groupId = gUid)
                        val wedos = group.wedos.toMutableList().apply {
                            add(newWedo)
                        }
                        group.wedos = wedos
                        gc.document(gUid).set(group)

                        _wedos.value = _wedos.value?.toMutableList()?.apply {
                            add(newWedo)
                        }
                        Log.d("now Maybe recomposition")
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