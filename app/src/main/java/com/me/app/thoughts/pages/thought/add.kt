package com.me.app.thoughts.pages.thought

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import com.me.app.thoughts.compose.Select
import com.me.app.thoughts.compose.SelectOption
import com.me.app.thoughts.data.Thought
import com.me.app.thoughts.data.thoughtDao
import com.me.app.thoughts.util.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

const val TAG = "thought.add"

@Preview
@Composable
fun View() {
    AddThought()
}

// 添加碎碎念
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddThought(doWhatList: Collection<String> = DO_LIST_MAP.keys) {
    val scope = CoroutineScope(Dispatchers.IO)

    var level by remember { mutableIntStateOf(4) }
    var doWhat by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var expand by remember { mutableStateOf(false) }
    var pid by remember { mutableIntStateOf(0) }
    val pidEmptyOptions = mapOf(0 to SelectOption("无", 0))
    var pidOptions: Map<Int, SelectOption<Int>> by remember { mutableStateOf(pidEmptyOptions) }

    var showTimePicker by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf(TimePickerState(0, 0, true)) }

    var allowSubmit by remember { mutableStateOf(true) }

    val onSubmit: () -> Unit = {
        scope.launch {
            val thought = Thought(
                pid = if (expand) pid else 0,
                level = level,
                doWhat = doWhat.trim(),
                message = message.trim(),
                timestamp = if (expand)
                    TimeUtil.timestamp(time.hour, time.minute) else System.currentTimeMillis(),
            )
            thoughtDao().insert(thought)
            Log.d(TAG, "submit $thought")

            allowSubmit = false
            doWhat = ""
            message = ""
            expand = false

            delay(1000)
            allowSubmit = true
        }
    }

    val width = 0.9f

    LaunchedEffect(key1 = expand) {
        if (expand) {
            scope.launch {
                val list = thoughtDao().listByPidAndTimestamp()
                val options = LinkedHashMap(pidEmptyOptions)
                list.forEach { options[it.id] = SelectOption(it.message.lines().first(), it.id) }
                pidOptions = options

                val now = LocalTime.now()
                time = TimePickerState(now.hour, now.minute, true)
            }

        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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

        if (expand) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "父项")

                Select(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    selected = pid, onSelect = { pid = it }, options = pidOptions,
                )

                Text(text = "时间")

                ElevatedButton(onClick = { showTimePicker = !showTimePicker }) {
                    Text(text = "${time.hour}:${time.minute}")
                }
            }

            if (showTimePicker) {
                Dialog(onDismissRequest = { showTimePicker = false }) {
                    TimePicker(state = time, modifier = Modifier)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onSubmit,
                enabled = allowSubmit && doWhat.isNotBlank(),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
            ) {
                Text(text = "记录")
            }

            Button(
                onClick = { expand = !expand },
                modifier = Modifier.height(50.dp)
            ) {
                Text(text = "更多")
                Icon(
                    if (expand) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null
                )
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
