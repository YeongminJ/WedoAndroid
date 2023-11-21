package com.hostd.wedo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hostd.wedo.data.Constants.IS_COMPLETE_INIT
import com.hostd.wedo.main.MainActivity
import com.hostd.wedo.util.PreferenceUtils

//NOTE 앱 첫 실행 유저가 기본설정할 닉네임, 내 할일 색상 설정
class IntroActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PreferenceUtils.get(IS_COMPLETE_INIT, false)) {
            finish()
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
            }
        }
        else {
            //show Tutorials
            setContent {

            }
        }
    }

}