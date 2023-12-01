package com.hostd.wedo

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.hostd.wedo.data.Constants
import com.hostd.wedo.data.LocalWedo
import com.hostd.wedo.util.Log
import com.hostd.wedo.util.PreferenceUtils
import com.hostd.wedo.util.WedoViewModelFactory

class MainActivity: ComponentActivity() {

    lateinit var viewModel: WedoViewModel

    enum class Screen {
        INTRO_SCREEN, MAIN_SCREEN
    }
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //잠금화면에서 동작 처리
        setTurnScreenOnLock()
        //TODO Hilt 적용시 대체, 임시 Provider 로 이용중
//        val repository = LocalRepositoryImpl()
        viewModel = ViewModelProvider(this, WedoViewModelFactory())[WedoViewModel::class.java]

        setContent {
            val destination = if (PreferenceUtils.get(Constants.TUTORIAL_COMPLETE, false)) {
                Screen.MAIN_SCREEN.name
            } else Screen.INTRO_SCREEN.name
            WedoScreen(viewModel = viewModel, startDestination = destination)
        }
    }
}


@OptIn(ExperimentalUnitApi::class)

@Composable
fun TodoItem(wedo: LocalWedo, viewModel: WedoViewModel) {
    Log.e("TodoItem : $wedo, ${wedo.members}, ${wedo.localGroup.groupId}")
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
fun BottomNavigationScreen() {
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

fun Activity.setTurnScreenOnLock() {
    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager?
    when {
        android.os.Build.VERSION.SDK_INT >= 27 -> {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            keyguardManager?.requestDismissKeyguard(this, null)
        }
        android.os.Build.VERSION.SDK_INT == 26 -> {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            keyguardManager?.requestDismissKeyguard(this, null)
        }
        else -> {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
    }
}