package com.me.app.thoughts.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.unit.dp
import com.me.app.thoughts.R
import com.me.app.thoughts.dto.Thought
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

const val TAG = "thought.add"

val DO_LIST_MAP = mapOf(
    "无" to "🎈",
    "休息" to "🛏",
    "阅读" to "📖",
    "思考" to "✨",
    "美食" to "🍔",
    "购物" to "🛒",
    "运动" to "🚵‍♂️",
)

// 添加碎碎念
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Add(doWhatList: Collection<String> = DO_LIST_MAP.keys) {
    val scope = CoroutineScope(Dispatchers.Main)

    var level by remember { mutableIntStateOf(4) }
    var doWhat by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var allowSubmit by remember { mutableStateOf(true) }

    val idInc = remember { AtomicInteger() }
    val onSubmit: () -> Unit = {
        val thought =
            Thought(idInc.incrementAndGet(), level, doWhat, message, LocalDateTime.now())
        Log.d(TAG, "submit $thought")

        allowSubmit = false
        doWhat = ""
        message = ""
        scope.launch {
            delay(1000)
            allowSubmit = true
        }
    }

    val width = 0.9f

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        SentimentIcon(level = level, size = 200)

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
        val maxItemsInEachRow = if (doWhatList.size % 4 < 3) 3 else 4

        FlowRow(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(width),
            horizontalArrangement = Arrangement.SpaceBetween,
            maxItemsInEachRow = maxItemsInEachRow,
        ) {
            doWhatList.map {
                CheckItem(name = it, checked = doWhat == it) {
                    doWhat = it
                }
            }
        }

        // 输入文字
        OutlinedTextField(
            value = message, onValueChange = { message = it },
            label = { Text(text = "想再说点什么") },
            placeholder = { Text(text = "...") },
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth(width)
                .requiredHeight(200.dp)
        )

        // 提交
        Button(
            onClick = onSubmit,
            enabled = allowSubmit,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
                .padding(bottom = 10.dp),
        ) {
            Text(text = "记录")
        }
    }
}

fun sentimentId(level: Int) = when (level) {
    1 -> R.drawable.sentiment_very_very_dissatisfied
    2 -> R.drawable.sentiment_very_dissatisfied
    3 -> R.drawable.sentiment_dissatisfied
    4 -> R.drawable.sentiment_neutral
    5 -> R.drawable.sentiment_satisfied
    6 -> R.drawable.sentiment_satisfied_alt
    7 -> R.drawable.sentiment_very_satisfied
    else -> R.drawable.sentiment_neutral
}

@Composable
fun SentimentIcon(level: Int, size: Int = 100) {
    Image(
        modifier = Modifier.size(size.dp),
        painter = painterResource(id = sentimentId(level)),
        contentDescription = null
    )
}

@Composable
fun CheckItem(name: String, checked: Boolean, onChecked: () -> Unit) {
    if (checked) {
        FilledTonalButton(onClick = onChecked) {
            Text(text = name)
        }
        return
    }

    OutlinedButton(onClick = onChecked) {
        Text(text = name)
    }
}
