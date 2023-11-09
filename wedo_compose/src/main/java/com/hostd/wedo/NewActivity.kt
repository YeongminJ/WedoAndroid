package com.hostd.wedo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import com.hostd.wedo.util.Log

class NewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = WedoViewModel()

        setContent {
            val groupsState = viewModel.groups.observeAsState()
            groupsState.value?.forEach {
                Log.w("groupID : ${it.groupId}")
            }
        }
    }
}