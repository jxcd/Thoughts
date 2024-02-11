package com.me.app.thoughts.pages.thought

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.me.app.thoughts.dto.Thought
import com.me.app.thoughts.dto.thoughtDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "thought.add"

@Preview
@Composable
fun View() {
    AddThought()
}

// 添加碎碎念
@Composable
fun AddThought(doWhatList: Collection<String> = DO_LIST_MAP.keys) {
    val scope = CoroutineScope(Dispatchers.Main)

    var level by remember { mutableIntStateOf(4) }
    var doWhat by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var allowSubmit by remember { mutableStateOf(true) }

    val onSubmit: () -> Unit = {
        scope.launch {
            val thought = Thought(
                level = level,
                doWhat = doWhat.trim(),
                message = message.trim()
            )
            thoughtDao().insert(thought)
            Log.d(TAG, "submit $thought")

            allowSubmit = false
            doWhat = ""
            message = ""
            delay(1000)
            allowSubmit = true
        }
    }

    val width = 0.9f

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SentimentIcon(level = level, size = 180)

        // 选择心情 1-7, 默认4
        Slider(
            value = level.toFloat(),
            onValueChange = { level = it.toInt() },
            steps = 5,
            valueRange = 1f..7.1f,
            modifier = Modifier.fillMaxWidth(width),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        )

        // 选择在做什么
        doWhatList.chunked(3).forEach { sub ->
            Row(
                modifier = Modifier.fillMaxWidth(width),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sub.map {
                    CheckItem(name = "${doWhatIcon(it)} $it", checked = doWhat == it) {
                        doWhat = if (doWhat == it) "" else it
                    }
                }
            }
        }

        // 输入文字
        OutlinedTextField(
            value = message, onValueChange = { message = it },
            label = { Text(text = "想再说点什么") },
            placeholder = { Text(text = "...") },
            maxLines = 2,
            modifier = Modifier
                .fillMaxWidth(width)
                .requiredHeight(200.dp)
        )

        // 提交
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onSubmit,
                enabled = allowSubmit && doWhat.isNotBlank(),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
            ) {
                Text(text = "记录")
            }
        }
    }
}

@Composable
fun SentimentIcon(level: Int, size: Int) {
    Icon(
        modifier = Modifier.size(size.dp),
        painter = painterResource(id = sentimentIconId(level)), contentDescription = null,
        tint = sentimentColor(level)
    )
}

@Composable
fun CheckItem(name: String, checked: Boolean, onChecked: () -> Unit) {
    val width = 100.dp

    if (checked) {
        FilledTonalButton(onClick = onChecked, modifier = Modifier.width(width)) {
            Text(text = name)
        }
        return
    }

    OutlinedButton(onClick = onChecked, modifier = Modifier.width(width)) {
        Text(text = name)
    }
}
