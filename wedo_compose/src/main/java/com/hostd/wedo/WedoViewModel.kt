package com.hostd.wedo

//import com.hostd.wedo.data.repository.LocalRepository
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hostd.wedo.data.Constants.GROUPS
import com.hostd.wedo.data.Constants.USERS
import com.hostd.wedo.data.LocalGroup
import com.hostd.wedo.data.LocalUser
import com.hostd.wedo.data.LocalWedo
import com.hostd.wedo.data.User
import com.hostd.wedo.data.Wedo
import com.hostd.wedo.data.WedoGroup
import com.hostd.wedo.data.repository.StoreRepository
import com.hostd.wedo.util.Log
import com.hostd.wedo.util.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//TODO Data 레이어에 맞게 수정 Repository, Datasource, Entity
class WedoViewModel(val repository: StoreRepository) : ViewModel() {

    //TODO 구조의 수정이 필요함. 처음에 불러올때, User 에 대한 정보를 로컬에 저장해야하고,
    // Wedo 그릴때 저장된 정보를 토대로 바로 그릴수 있게 해야함. 그래야 동적으로 데이터 호출이 없어짐.
    

    val LAST_GROUP_ID = "last_group_id"

    var currentGroupId: String = PreferenceUtils.get(LAST_GROUP_ID, "")

    val origin: MutableLiveData<List<WedoGroup>> = MutableLiveData(listOf())

    private val _wedos: MutableLiveData<List<Wedo>> = MutableLiveData(listOf())
    val wedos: LiveData<List<Wedo>> = _wedos

    private val _loadState:MutableState<Boolean> = mutableStateOf(false)
    val loadState: State<Boolean> = _loadState

    val splashLoading: MutableState<Boolean> = mutableStateOf(true)

    val _localWedos: MutableLiveData<List<LocalWedo>> = MutableLiveData(listOf())

    // 기본 그룹 ID
    val defaultUID: String = PreferenceUtils.getDefaultUid()

    //TODO LocalRepository 로 db 로 저장, FireStore 갱신시에는 local 도 갱신되게
    //Key UID, Value User
    val localUsers = hashMapOf<String, LocalUser>()
//    val localGroups = mutableListOf<LocalGroup>()
    val localGroups = hashMapOf<String, LocalGroup>()
//    val localWedo = mutableListOf<LocalWedo>()

    val db = Firebase.firestore
    init {
        viewModelScope.launch {
            delay(1000)
            splashLoading.value = false
        }
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
                        Log.i("Start Group Init")
                        val groupTasks = mutableListOf<Task<DocumentSnapshot>>()
                        val userTasks = mutableListOf<Task<DocumentSnapshot>>()
                        user.groups.forEach {
                            //Complete 안쓰면 Exception 발생
                            groupTasks.add(db.collection(GROUPS).document(it).get())

                            /*db.collection(GROUPS).document(it).get().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    it.result.toObject(WedoGroup::class.java)?.let { mGroup->
                                        _wedos.value = _wedos.value?.toMutableList()?.apply {
                                            addAll(mGroup.wedos)
                                        }

                                        localGroups.add(LocalGroup(mGroup.groupId, mGroup.groupname))
                                        Log.i("Group Insert : ${mGroup.groupId}")

                                        userTasks.add(db.collection(USERS).document(mGroup.groupId).get())
                                    }
                                }
                            }*/
                        }
                        Tasks.whenAllComplete(groupTasks).addOnCompleteListener { list->
                            Log.d("Group Task Complete")
                            list.result.forEach {
                                (it.result as DocumentSnapshot).toObject(WedoGroup::class.java)?.let { mGroup->
                                    Log.d("Group : $mGroup")
                                    val localGroup = LocalGroup(mGroup.groupId, mGroup.groupname, members = mGroup.members, wedos = mGroup.wedos)
                                    localGroups.put(mGroup.groupId, localGroup)
//                                    _wedos.value = mGroup.wedos

//                                    mGroup.wedos.forEach { wedo->
//                                        //TODO Member는 userTask 완료시에 넣기 or View 그릴때?
//
////                                        localWedo.add(LocalWedo(todo = wedo.todo, localGroup = localGroup, starCount = wedo.starCount, createDate = wedo.createDate))
//                                    }


                                    //유저 테스크 시작
                                    mGroup.members.forEach {
                                        userTasks.add(db.collection(USERS).document(it).get())
                                    }
                                }
                            }

                            //그룹 끝난뒤 유저 순차적임.
                            Tasks.whenAllComplete(userTasks).addOnCompleteListener { list ->
                                Log.i("User Task Complete")
                                list.result.forEach {
                                    (it.result as DocumentSnapshot).toObject(User::class.java)?.let { user->
                                        Log.d("User : $user")
                                        localUsers[user.uid] = LocalUser(user.uid, user.email, user.thumbnail)
                                    }
                                }
                                //user 까지 받아오기 끝남 LocalWedo Make
                                makeLocalWedo()
                            }
                        }

                        //실제 끝지점이 아님.
                        Log.i("End Group Init")
                    }
                }
            }
        }
    }

    private fun makeLocalWedo() {
        val localWedos = mutableListOf<LocalWedo>()
        localGroups.forEach { map ->

            val memberUsers = mutableListOf<LocalUser>()
            map.value.members.forEach { memberId->
                //멤버 아이디 기준으로 LocalUser 를 찾은 다음에 LocalUser 를 추가
                localUsers[memberId]?.let { memberUsers.add(it) }
            }
            map.value.wedos.forEach { wedo->
                Log.d("LocalWedo : ${wedo.todo}")
                localWedos.add(LocalWedo(wedo.todo, map.value, wedo.starCount, wedo.createDate, memberUsers))
            }
        }
        Log.w("LocalWedo Count : ${localWedos.size}")
        //recomposition
        _localWedos.value = localWedos
    }

    fun firstInitWedo() {
        //create
        //1. group 먼저 만들기
        val groupUid = WedoGroup.getGeneratorGroup()
        db.collection(GROUPS).also { gc->
            WedoGroup(groupUid, members = listOf(defaultUID)).also { group->
                //만들어진 그룹 저장
                gc.document(groupUid).set(group)
//                groups.add(group)
                currentGroupId = groupUid
                PreferenceUtils.set(LAST_GROUP_ID, groupUid)
                localGroups
            }
        }
        //2. 유저 정보 만들기 TODO Email
        User(uid = defaultUID, groups = listOf(groupUid)).also {
            db.collection(USERS).document(defaultUID).set(it)
        }
    }

    fun addWedo(text: String, gUid: String = currentGroupId) {
        val newWedo = Wedo(todo = text, groupId = gUid)
        db.collection(GROUPS).also { gc->
            gc.document(gUid).get().addOnCompleteListener { task->

                if (task.isSuccessful) {
                    task.result.toObject(WedoGroup::class.java)?.let { group->
                        //add to firestore
                        val wedos = group.wedos.toMutableList().apply {
                            add(newWedo)
                        }
                        group.wedos = wedos
                        gc.document(gUid).set(group)

                        //Deprecated old wedo
//                        _wedos.value = _wedos.value?.toMutableList()?.apply {
//                            add(newWedo)
//                        }

                        //add to local
                        Log.w("Add Local Wedo")
                        localGroups[gUid]?.let { localGroup ->
                            val memberUsers = mutableListOf<LocalUser>()
                            localGroup.members.forEach {
                                localUsers[it]?.let { user-> memberUsers.add(user) }
                            }
                            val newLocalWedo = LocalWedo(newWedo.todo, localGroup, newWedo.starCount, newWedo.createDate, memberUsers)
                            _localWedos.value = _localWedos.value?.toMutableList()?.apply {
                                add(newLocalWedo)
                            }
                        }
                        Log.d("now Maybe recomposition")
                    }
                }
            }
        }
    }

    suspend fun groupUsers(wedo: Wedo): List<User> {
        val users = mutableListOf<User>()
        withContext(Dispatchers.IO) {
            db.collection(GROUPS).document(wedo.groupId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.toObject(WedoGroup::class.java)?.let { group ->
                        group.members.forEach {
                            db.collection(USERS).document(it).get().addOnCompleteListener {
                                it.result.toObject(User::class.java)?.let { user -> users.add(user) }
                            }
                        }
                    }
                }
            }
        }
        return users
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