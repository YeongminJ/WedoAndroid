package com.hostd.wedo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import com.hostd.wedo.components.SimpleProgressDialog
import com.hostd.wedo.data.Wedo
import com.hostd.wedo.util.Log
import com.hostd.wedo.util.WedoViewModelFactory

class MainActivity: ComponentActivity() {

    lateinit var viewModel: WedoViewModel

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO Hilt 적용시 대체, 임시 Provider 로 이용중
//        val repository = LocalRepositoryImpl()
        viewModel = ViewModelProvider(this, WedoViewModelFactory())[WedoViewModel::class.java]

        setContent {
            //TODO 다이얼로그 State 처리
            if (viewModel.loadState.value) {
                SimpleProgressDialog(Modifier.padding(0.dp), "Working", onDismissRequest = {
                    //TODO 여기서 다이얼로그 State 변경
                    Log.i("Touch Outside")
                })
            }
            val wedoListState = viewModel.wedos.observeAsState()
            Log.e("JDI", "wedo count : ${wedoListState.value?.size}")
            val showDialog = remember { mutableStateOf(false) }
            if (showDialog.value) {
                TodoDialog(showDialog, addAction = { text->
                    viewModel.addWedo(text)
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

                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    //이것은 리스트 컨테이너 패딩
//                    contentPadding = PaddingValues(16.dp)
                ) {
                    // 0번째 그룹만 우선 처리
                    wedoListState.value?.let { wedos->
                        items(wedos) {
                            TodoItem(it, viewModel)
                        }
                    }
                }
            }
        }



//        viewModel.initWedo()
    }
}


@OptIn(ExperimentalUnitApi::class)

@Composable
fun TodoItem(wedo: Wedo, viewModel: WedoViewModel) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 8.dp, end = 8.dp),
        shape = RoundedCornerShape(16.dp),

    ) {
        Row(Modifier.padding(16.dp)) {
            Text(text = wedo.todo, fontSize = 18.sp)

//            viewModel.groupUsers()
//            viewModel.groupUsers(wedo)
//            Text(text = )
        }
//        Box(contentAlignment = Alignment.Center) {
//            Text(
//                wedo.todo,
//                modifier = Modifier.padding(10.dp),
//                fontSize = 30.sp,
//            )
//        }
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