package com.hostd.wedo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import com.hostd.wedo.data.Wedo
import com.hostd.wedo.data.WedoGroup
import com.hostd.wedo.util.Log
import com.hostd.wedo.util.WedoViewModelFactory

class MainActivity: ComponentActivity() {

    lateinit var viewModel: WedoViewModel

//    val mutableItems = remember { mutableStateListOf<String>() }

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val groups = viewModel.groups.observeAsState()
            Log.e("JDI", "groups: ${groups.value?.size}")
            val showDialog = remember { mutableStateOf(false) }
            if (showDialog.value) {
                TodoDialog(showDialog, addAction = { text->
//                    mutableItems.add(it)
                    //TODO Group 셀렉션
                    groups.value?.getOrNull(0)?.let { group->
                        viewModel.addWedo(text, group.groupId)
                    } ?: kotlin.run {
                        Log.w("group is null")
                    }
                })
            }
            Scaffold(
                bottomBar = {MainScreen()},
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        showDialog.value = true
                    }) {

                    }
                }

            ) { innerPadding->

                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    // 0번째 그룹만 우선 처리
                    groups.value?.let { groupItems ->
                        Log.e("JDI", "groups222: ${groups.value?.size}")
                        groupItems.getOrNull(0)?.let { wedos->
                            items(wedos.wedos) {
                                TodoItem(it, wedos)
                            }
                        }
                    }

                    /* ... */
                }
            }
        }

        //TODO Hilt 적용시 대체, 임시 Provider 로 이용중
//        val repository = LocalRepositoryImpl()
        viewModel = ViewModelProvider(this, WedoViewModelFactory())[WedoViewModel::class.java]

//        viewModel.initWedo()
    }
}


@OptIn(ExperimentalUnitApi::class)
@Composable
fun TodoItem(wedo: Wedo, group: WedoGroup) {
    Card(
        Modifier
            .fillMaxWidth()
            .border(width = 4.dp,
                color = Color.Black
            )) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                wedo.todo,
                modifier = Modifier.padding(10.dp),
                fontSize = 30.sp,
            )
            Text(
                group.groupname,
                color = Color.Red
            )
        }
    }
}

val navItems = listOf("Wedo", "BucketList", "Group")
val navIcons = listOf(Icons.Default.Done, Icons.Default.List, Icons.Default.AccountCircle)
@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val selectedIndex = remember { mutableStateOf(0) }
    NavigationBar {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
//                enabled = index == 0,   //TODO Enable and 화면전환
                icon = { Icon(navIcons.getOrElse(index) { Icons.Filled.Done }, contentDescription = item) },
                label = { Text(item) },
                selected = index == selectedIndex.value,
                onClick = { selectedIndex.value = index }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDialog(showDialog: MutableState<Boolean>, addAction: (String)-> Unit) {
    Modifier.padding()
    val textFieldString = remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = {
       showDialog.value = false
    }, title = {
        Text(text = "할 일 추가")
    }, confirmButton = {
        IconButton(onClick = {
            addAction(textFieldString.value)
            showDialog.value = false
        }) {
            Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "추가")
        }
    }, text = {
        TextField(value = textFieldString.value, onValueChange = {
            Log.e("JDI", "onValue : $it")
            textFieldString.value = it
        })
    }, properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true))
}