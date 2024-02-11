package com.me.app.thoughts.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.me.app.thoughts.pages.thought.AddThought
import com.me.app.thoughts.pages.thought.ThoughtList

private val pages = listOf("列表", "添加", "分析")
@Composable
fun App() {
    var presses by remember { mutableStateOf("添加") }

    Scaffold(
        topBar = {},
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    pages.forEach {
                        Button(onClick = { presses = it }) {
                            Text(text = it)
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        Card(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (presses) {
                "列表" -> ThoughtList()
                else -> AddThought()
            }

        }
    }
}