package com.hostd.wedo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import com.hostd.wedo.util.Log
import com.hostd.wedo.util.NewWedoViewModelFactory

class NewActivity : ComponentActivity() {
    lateinit var viewModel: NewMainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = NewWedoViewModelFactory().create(NewMainViewModel::class.java)

        setContent {
            val groupsState = viewModel.groups.collectAsState(emptyList())
            groupsState.value.forEach {
                Log.w("groupID : ${it.groupId}")
            }
        }
    }
}