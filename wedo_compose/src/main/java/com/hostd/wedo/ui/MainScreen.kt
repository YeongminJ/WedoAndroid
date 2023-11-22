package com.hostd.wedo.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hostd.wedo.BottomNavigationScreen
import com.hostd.wedo.TodoItem
import com.hostd.wedo.WedoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: WedoViewModel) {
    val wedoListState = viewModel._localWedos.observeAsState()
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        TodoDialog(showDialog, addAction = { text->
            viewModel.addWedo(text)
        })
    }

    Scaffold(
        bottomBar = { BottomNavigationScreen() },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog.value = true
            }) {

            }
        }
    ) { innerPadding->
        LazyColumn(
            modifier = androidx.compose.ui.Modifier.padding(innerPadding),
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