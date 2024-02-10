package com.me.app.thoughts.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.me.app.thoughts.dto.Thought
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

// 添加碎碎念
@Composable
fun Add() {
    var level by remember { mutableFloatStateOf(4f) }
    var doWhat by remember { mutableStateOf("") }
    val doWhatList = listOf("无", "美食", "购物", "休息", "运动")

    var message by remember { mutableStateOf("") }

    var data by remember { mutableStateOf("") }

    val idInc = remember { AtomicInteger() }
    val onSubmit: () -> Unit = {
        val thought =
            Thought(idInc.incrementAndGet(), level.toInt(), doWhat, message, LocalDateTime.now())
        data = thought.toString()
    }

    val width = 0.9f

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // 选择心情 1-7, 默认4
        Slider(
            value = level,
            onValueChange = { level = it },
            steps = 5,
            valueRange = 1f..7f,
            modifier = Modifier.fillMaxWidth(width),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        )
        // Text(text = level.toString())

        // 选择在做什么
        Row(
            modifier = Modifier.fillMaxWidth(width),
            horizontalArrangement = Arrangement.SpaceAround
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
            modifier = Modifier.fillMaxWidth(width).requiredHeight(200.dp)
        )

        // 提交
        Button(onClick = onSubmit) {
            Text(text = "记录")
        }
    }
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
