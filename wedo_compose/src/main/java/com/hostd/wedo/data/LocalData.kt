package com.hostd.wedo.data

val NONE: Int = -1

// Network 계속 요청하면 동적으로 얻어야 하기 때문에, 메모리상에 상주할 Data Group 을 활성화 시킴. 이정보를 DB 에 넣어 두고 쓸까?
data class LocalGroup(
    var groupId: String = "",
    var groupName: String = "",
    var groupColor: Int = NONE // 자신한테 보여질 그룹에 칼러 ( 현재 구분이 어렵기 때문에 생각 )
)

data class LocalUser(
    var uid: String = "",
    var email: String = "",
    var thumbnail: String = ""
)

//data class LocalWedo(
//    var todo: String = "",
//    var localGroup: LocalGroup,
//    var starCount: Int = 0,
//    var createDate: Long = System.currentTimeMillis(),
//    var members: List<String> = mutableListOf()
//)